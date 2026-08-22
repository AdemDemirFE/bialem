package com.bialem.backend.web.rest;

import com.bialem.backend.service.DirectMessagingService;
import com.bialem.backend.service.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/app/messages")
public class DirectMessagingResource {

    private final DirectMessagingService messagingService;

    public DirectMessagingResource(DirectMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @GetMapping("/conversations")
    public List<DirectConversationDTO> conversations(@RequestParam(defaultValue = "ALL") String filter, @RequestParam(defaultValue = "") String q) {
        return messagingService.listConversations("UNREAD".equalsIgnoreCase(filter), q);
    }

    @GetMapping("/people")
    public List<MessageRecipientDTO> people(@RequestParam(defaultValue = "") String q) {
        return messagingService.searchRecipients(q);
    }

    @PostMapping("/conversations")
    public DirectConversationDTO start(@Valid @RequestBody StartConversationRequest request) {
        return messagingService.startConversation(request.recipientProfileId());
    }

    @GetMapping("/conversations/{id}/messages")
    public List<DirectMessageDTO> messages(@PathVariable Long id) {
        return messagingService.listMessages(id);
    }

    @PostMapping("/conversations/{id}/messages")
    public DirectMessageDTO send(@PathVariable Long id, @Valid @RequestBody SendMessageRequest request) {
        return messagingService.sendMessage(id, request.body());
    }

    @PutMapping("/conversations/{id}/read")
    public ResponseEntity<Void> read(@PathVariable Long id) {
        messagingService.markRead(id);
        return ResponseEntity.noContent().build();
    }

    public record StartConversationRequest(@NotNull Long recipientProfileId) {}
    public record SendMessageRequest(@NotBlank @Size(max = 2000) String body) {}
}
