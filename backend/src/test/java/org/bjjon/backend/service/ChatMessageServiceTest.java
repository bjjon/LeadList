package org.bjjon.backend.service;

import org.bjjon.backend.dto.chatmessage.ChatMessageRequest;
import org.bjjon.backend.dto.chatmessage.ChatMessageResponse;
import org.bjjon.backend.entity.ChatMessage;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.repository.ChatMessageRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepo chatMessageRepo;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatMessageService chatMessageService;

    private User user;

    @BeforeEach
    void setUp() {
        chatMessageService = new ChatMessageService(chatMessageRepo, messagingTemplate);

        user = User.builder()
                .id(UUID.randomUUID())
                .firstname("Erika")
                .lastname("Musterfrau")
                .email("erika.musterfrau@example.com")
                .password("irrelevant")
                .build();
    }

    @Test
    void getAll_multipleMessages_returnsMappedResponsesInRepoOrder() {
        ChatMessage message1 = ChatMessage.builder()
                .id(UUID.randomUUID())
                .user(user)
                .content("Hallo zusammen")
                .createdAt(Instant.now())
                .build();
        ChatMessage message2 = ChatMessage.builder()
                .id(UUID.randomUUID())
                .user(user)
                .content("Wer übernimmt den Rückruf?")
                .createdAt(Instant.now())
                .build();
        when(chatMessageRepo.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(message1, message2));

        List<ChatMessageResponse> result = chatMessageService.getAll();

        assertEquals(2, result.size());
        assertEquals("Hallo zusammen", result.get(0).content());
        assertEquals("Wer übernimmt den Rückruf?", result.get(1).content());
        assertEquals(user.getId(), result.get(0).sender().id());
    }

    @Test
    void getAll_noMessagesInRepo_returnsEmptyList() {
        when(chatMessageRepo.findAllByOrderByCreatedAtAsc()).thenReturn(List.of());

        List<ChatMessageResponse> result = chatMessageService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void send_validRequest_savesChatMessageWithUserAndContent() {
        ChatMessageRequest request = new ChatMessageRequest("Kunde hat zugesagt");

        chatMessageService.send(user, request);

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepo).save(captor.capture());
        ChatMessage saved = captor.getValue();
        assertEquals(user, saved.getUser());
        assertEquals("Kunde hat zugesagt", saved.getContent());
    }

    @Test
    void send_validRequest_returnsMappedChatMessageResponse() {
        ChatMessageRequest request = new ChatMessageRequest("Kunde hat zugesagt");

        ChatMessageResponse result = chatMessageService.send(user, request);

        assertEquals("Kunde hat zugesagt", result.content());
        assertEquals(user.getId(), result.sender().id());
    }

    @Test
    void send_validRequest_broadcastsResponseToTopicChatMessages() {
        ChatMessageRequest request = new ChatMessageRequest("Kunde hat zugesagt");

        chatMessageService.send(user, request);

        verify(messagingTemplate).convertAndSend(eq("/topic/chat-messages"), any(ChatMessageResponse.class));
    }
}
