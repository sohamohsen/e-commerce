package com.task.ecommerce.conversation;

import com.task.ecommerce.entity.Conversation;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.conversation.dto.SendChatMessageRequest;
import com.task.ecommerce.entity.ChatMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Conversations (Live Chat)", description = "Endpoints for initiating and managing customer support chat conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "Start chat conversation", description = "Initiates a new customer support conversation for the authenticated user.")
    @PostMapping("/conversation")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Conversation> startConversation(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.ok(
                conversationService.startConversation(user.getId())
        );
    }

    @Operation(summary = "Start conversation alternative", description = "Alias endpoint to start a chat conversation.")
    @PostMapping("/conversations")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Conversation start(
            @AuthenticationPrincipal User user
    ) {

        return conversationService.startConversation(user.getId());
    }

    @Operation(summary = "Get waiting conversations", description = "Retrieves chat conversations waiting for an admin to accept.")
    @GetMapping("/conversations/waiting")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<Conversation> waiting() {

        return conversationService.getWaitingConversations();
    }

    @Operation(summary = "Accept chat conversation", description = "Admin accepts a waiting conversation by ID.")
    @PostMapping("/conversations/{id}/accept")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Conversation accept(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {

        return conversationService.acceptConversation(
                id,
                user.getId()
        );
    }

    @Operation(summary = "Get current chat conversation")
    @GetMapping("/conversations/current")
    public ResponseEntity<Conversation> current(@AuthenticationPrincipal User user) {
        Conversation conversation = conversationService.getCurrentConversation(user);
        return conversation == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(conversation);
    }

    @Operation(summary = "Get conversation messages")
    @GetMapping("/conversations/{id}/messages")
    public List<ChatMessage> messages(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return chatMessageService.getMessages(id, user);
    }

    @Operation(summary = "Send a chat message")
    @PostMapping("/conversations/{id}/messages")
    public ChatMessage sendMessage(@PathVariable Long id, @Valid @RequestBody SendChatMessageRequest request,
                                   @AuthenticationPrincipal User user) {
        return chatMessageService.sendMessage(id, request.getContent(), user);
    }

    @Operation(summary = "Close a chat conversation")
    @PostMapping("/conversations/{id}/close")
    public Conversation close(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return conversationService.closeConversation(id, user);
    }

}
