package org.bjjon.backend.service;

import org.bjjon.backend.dto.chatmessage.ChatMessageRequest;
import org.bjjon.backend.dto.chatmessage.ChatMessageResponse;
import org.bjjon.backend.entity.ChatMessage;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.repository.ChatMessageRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    private static final String TOPIC_CHAT_MESSAGES = "/topic/chat-messages";

    private final ChatMessageRepo chatMessageRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageService(ChatMessageRepo chatMessageRepo, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepo = chatMessageRepo;
        this.messagingTemplate = messagingTemplate;
    }

    public List<ChatMessageResponse> getAll() {
        return chatMessageRepo.findAllByOrderByCreatedAtAsc().stream()
                .map(ChatMessageResponse::fromEntity)
                .toList();
    }

    public ChatMessageResponse send(User user, ChatMessageRequest request) {
        ChatMessage chatMessage = ChatMessage.builder()
                .user(user)
                .content(request.content())
                .build();
        chatMessageRepo.save(chatMessage);

        ChatMessageResponse response = ChatMessageResponse.fromEntity(chatMessage);
        messagingTemplate.convertAndSend(TOPIC_CHAT_MESSAGES, response);

        return response;
    }
}
