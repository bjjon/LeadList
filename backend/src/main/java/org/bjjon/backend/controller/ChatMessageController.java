package org.bjjon.backend.controller;

import jakarta.validation.Valid;
import org.bjjon.backend.dto.chatmessage.ChatMessageRequest;
import org.bjjon.backend.dto.chatmessage.ChatMessageResponse;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.service.ChatMessageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat-messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping
    public List<ChatMessageResponse> getAll() {
        return chatMessageService.getAll();
    }

    @PostMapping
    public ChatMessageResponse send(@AuthenticationPrincipal User user, @Valid @RequestBody ChatMessageRequest request) {
        return chatMessageService.send(user, request);
    }
}
