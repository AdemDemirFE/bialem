package com.bialem.backend.service;

import com.bialem.backend.domain.*;
import com.bialem.backend.domain.enumeration.AllowMessagesFrom;
import com.bialem.backend.repository.*;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.service.dto.*;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class DirectMessagingService {

    private final DirectConversationRepository conversationRepository;
    private final DirectMessageRepository messageRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final AccountPreferencesRepository preferencesRepository;
    private final FollowRepository followRepository;
    private final BlockRepository blockRepository;
    private final AppNotificationService appNotificationService;

    public DirectMessagingService(
        DirectConversationRepository conversationRepository,
        DirectMessageRepository messageRepository,
        ProfileRepository profileRepository,
        UserRepository userRepository,
        AccountPreferencesRepository preferencesRepository,
        FollowRepository followRepository,
        BlockRepository blockRepository,
        AppNotificationService appNotificationService
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.preferencesRepository = preferencesRepository;
        this.followRepository = followRepository;
        this.blockRepository = blockRepository;
        this.appNotificationService = appNotificationService;
    }

    @Transactional(readOnly = true)
    public List<DirectConversationDTO> listConversations(boolean unreadOnly, String query) {
        Profile current = currentProfile();
        String normalized = query == null ? "" : query.trim().toLowerCase();
        return conversationRepository.findAllForProfile(current.getId()).stream()
            .map(conversation -> toConversationDto(conversation, current.getId()))
            .filter(dto -> !unreadOnly || dto.unreadCount() > 0)
            .filter(dto -> normalized.isEmpty() || dto.displayName().toLowerCase().contains(normalized) || dto.username().toLowerCase().contains(normalized) || (dto.lastMessage() != null && dto.lastMessage().toLowerCase().contains(normalized)))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageRecipientDTO> searchRecipients(String query) {
        Profile current = currentProfile();
        String normalized = query == null ? "" : query.trim();
        return profileRepository.searchMessageRecipients(current.getId(), normalized, PageRequest.of(0, 30)).stream()
            .filter(profile -> canMessage(current, profile))
            .map(profile -> new MessageRecipientDTO(profile.getId(), profile.getDisplayName(), profile.getUsername(), profile.getAvatarUrl()))
            .toList();
    }

    public DirectConversationDTO startConversation(Long recipientProfileId) {
        Profile current = currentProfile();
        Profile recipient = profileRepository.findById(recipientProfileId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        if (current.getId().equals(recipient.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kendinize mesaj gönderemezsiniz");
        if (!canMessage(current, recipient)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu kullanıcı yeni mesaj kabul etmiyor");
        Profile first = current.getId() < recipient.getId() ? current : recipient;
        Profile second = current.getId() < recipient.getId() ? recipient : current;
        DirectConversation conversation = conversationRepository.findByParticipantOne_IdAndParticipantTwo_Id(first.getId(), second.getId()).orElseGet(() -> {
            Instant now = Instant.now();
            DirectConversation created = new DirectConversation();
            created.setParticipantOne(first);
            created.setParticipantTwo(second);
            created.setCreatedAt(now);
            created.setUpdatedAt(now);
            return conversationRepository.save(created);
        });
        return toConversationDto(conversation, current.getId());
    }

    @Transactional(readOnly = true)
    public List<DirectMessageDTO> listMessages(Long conversationId) {
        Profile current = currentProfile();
        accessibleConversation(conversationId, current.getId());
        return messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversationId).stream().map(this::toMessageDto).toList();
    }

    public DirectMessageDTO sendMessage(Long conversationId, String body) {
        Profile current = currentProfile();
        DirectConversation conversation = accessibleConversation(conversationId, current.getId());
        String cleanBody = body == null ? "" : body.trim();
        if (cleanBody.isEmpty() || cleanBody.length() > 2000) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mesaj 1-2000 karakter olmalıdır");
        Profile recipient = otherParticipant(conversation, current.getId());
        if (!canMessage(current, recipient)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu kullanıcı mesaj kabul etmiyor");
        Instant now = Instant.now();
        DirectMessage message = new DirectMessage();
        message.setConversation(conversation);
        message.setSender(current);
        message.setBody(cleanBody);
        message.setCreatedAt(now);
        conversation.setUpdatedAt(now);
        conversationRepository.save(conversation);
        DirectMessage saved = messageRepository.save(message);
        appNotificationService.sendToUser(
            recipient.getUser().getId(),
            current.getDisplayName() + " sana mesaj gönderdi",
            cleanBody.length() > 120 ? cleanBody.substring(0, 120) + "…" : cleanBody,
            "DIRECT_MESSAGE",
            String.valueOf(saved.getId()),
            "/messages/" + conversation.getId()
        );
        return toMessageDto(saved);
    }

    public void markRead(Long conversationId) {
        Profile current = currentProfile();
        accessibleConversation(conversationId, current.getId());
        messageRepository.markRead(conversationId, current.getId(), Instant.now());
    }

    private DirectConversationDTO toConversationDto(DirectConversation conversation, Long currentProfileId) {
        Profile other = otherParticipant(conversation, currentProfileId);
        DirectMessage last = messageRepository.findFirstByConversation_IdOrderByCreatedAtDesc(conversation.getId()).orElse(null);
        return new DirectConversationDTO(conversation.getId(), other.getId(), other.getDisplayName(), other.getUsername(), other.getAvatarUrl(), last != null ? last.getBody() : null, last != null ? last.getCreatedAt() : conversation.getUpdatedAt(), messageRepository.countByConversation_IdAndSender_IdNotAndReadAtIsNull(conversation.getId(), currentProfileId));
    }

    private DirectMessageDTO toMessageDto(DirectMessage message) {
        return new DirectMessageDTO(message.getId(), message.getConversation().getId(), message.getSender().getId(), message.getBody(), message.getCreatedAt(), message.getReadAt());
    }

    private DirectConversation accessibleConversation(Long id, Long profileId) {
        return conversationRepository.findAccessible(id, profileId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Konuşma bulunamadı"));
    }

    private Profile otherParticipant(DirectConversation conversation, Long currentProfileId) {
        return conversation.getParticipantOne().getId().equals(currentProfileId) ? conversation.getParticipantTwo() : conversation.getParticipantOne();
    }

    private boolean canMessage(Profile sender, Profile recipient) {
        if (blockRepository.existsByBlocker_IdAndBlockedUser_Id(sender.getId(), recipient.getId()) || blockRepository.existsByBlocker_IdAndBlockedUser_Id(recipient.getId(), sender.getId())) return false;
        AllowMessagesFrom setting = preferencesRepository.findOneByProfile_Id(recipient.getId()).map(AccountPreferences::getAllowMessagesFrom).orElse(AllowMessagesFrom.EVERYONE);
        if (setting == AllowMessagesFrom.NO_ONE) return false;
        return setting != AllowMessagesFrom.FOLLOWING || followRepository.existsByFollower_IdAndFollowed_Id(recipient.getId(), sender.getId());
    }

    private Profile currentProfile() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Long userId = userRepository.findOneByLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getId();
        return profileRepository.findOneByUser_Id(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı"));
    }
}
