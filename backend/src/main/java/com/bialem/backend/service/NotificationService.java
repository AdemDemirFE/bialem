package com.bialem.backend.service;

import com.bialem.backend.domain.Notification;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.repository.NotificationRepository;
import com.bialem.backend.repository.ProfileRepository;
import com.bialem.backend.service.dto.AppNotificationDTO;
import com.bialem.backend.service.dto.NotificationDTO;
import com.bialem.backend.service.dto.UnreadCountDTO;
import com.bialem.backend.service.mapper.NotificationMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing legacy {@link com.bialem.backend.domain.Notification}.
 */
@Service
@Transactional
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final ProfileRepository profileRepository;

    private final AppNotificationService appNotificationService;

    public NotificationService(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        ProfileRepository profileRepository,
        AppNotificationService appNotificationService
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.profileRepository = profileRepository;
        this.appNotificationService = appNotificationService;
    }

    public NotificationDTO save(NotificationDTO notificationDTO) {
        LOG.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    public NotificationDTO update(NotificationDTO notificationDTO) {
        LOG.debug("Request to update Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    public Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        LOG.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(existingNotification -> {
                notificationMapper.partialUpdate(existingNotification, notificationDTO);

                return existingNotification;
            })
            .map(notificationRepository::save)
            .map(notificationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        LOG.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
    }

    public AppNotificationDTO sendToUser(Long userId, String title, String body, String type, String referenceId, String route) {
        Profile profile = resolveProfile(userId);
        return appNotificationService.sendToUser(profile.getUser().getId(), title, body, type, referenceId, route);
    }

    @Transactional(readOnly = true)
    public List<AppNotificationDTO> listCurrentUser() {
        return appNotificationService.listCurrentUser();
    }

    @Transactional(readOnly = true)
    public UnreadCountDTO unreadCountCurrentUser() {
        return appNotificationService.unreadCountCurrentUser();
    }

    public AppNotificationDTO markCurrentUserRead(Long id) {
        return appNotificationService.markCurrentUserRead(id);
    }

    public void markAllCurrentUserRead() {
        appNotificationService.markAllCurrentUserRead();
    }

    private Profile resolveProfile(Long userId) {
        return profileRepository
            .findById(userId)
            .or(() -> profileRepository.findOneByUser_Id(userId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı"));
    }
}
