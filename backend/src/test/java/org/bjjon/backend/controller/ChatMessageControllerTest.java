package org.bjjon.backend.controller;

import org.bjjon.backend.TestcontainersConfiguration;
import org.bjjon.backend.config.WithMockUserSupportConfig;
import org.bjjon.backend.dto.chatmessage.ChatMessageRequest;
import org.bjjon.backend.dto.chatmessage.ChatMessageResponse;
import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.service.ChatMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfiguration.class, WithMockUserSupportConfig.class})
class ChatMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatMessageService chatMessageService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .firstname("Erika")
                .lastname("Musterfrau")
                .email("erika.musterfrau@example.com")
                .password("irrelevant")
                .build();
    }

    @Test
    @WithMockUser
    void getAll_authenticatedUser_returns200WithChatMessageList() throws Exception {
        ChatMessageResponse response1 = ChatMessageResponse.builder()
                .id(UUID.randomUUID())
                .sender(UserResponse.fromEntity(user))
                .content("Hallo zusammen")
                .createdAt(Instant.now())
                .build();
        ChatMessageResponse response2 = ChatMessageResponse.builder()
                .id(UUID.randomUUID())
                .sender(UserResponse.fromEntity(user))
                .content("Wer übernimmt den Rückruf?")
                .createdAt(Instant.now())
                .build();
        when(chatMessageService.getAll()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/chat-messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Hallo zusammen"))
                .andExpect(jsonPath("$[1].content").value("Wer übernimmt den Rückruf?"));
    }

    @Test
    @WithMockUser
    void getAll_noMessagesExist_returns200WithEmptyList() throws Exception {
        when(chatMessageService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/chat-messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/chat-messages"))
                .andExpect(status().isUnauthorized());

        verify(chatMessageService, never()).getAll();
    }

    @Test
    void send_authenticatedUser_returns200WithChatMessageResponse() throws Exception {
        ChatMessageRequest request = new ChatMessageRequest("Kunde hat zugesagt");
        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(UUID.randomUUID())
                .sender(UserResponse.fromEntity(user))
                .content(request.content())
                .createdAt(Instant.now())
                .build();
        when(chatMessageService.send(eq(user), any(ChatMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/chat-messages")
                        .with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Kunde hat zugesagt"))
                .andExpect(jsonPath("$.sender.id").value(user.getId().toString()));
    }

    @Test
    @WithMockUser
    void send_blankContent_returns400() throws Exception {
        mockMvc.perform(post("/api/chat-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"\"}"))
                .andExpect(status().isBadRequest());

        verify(chatMessageService, never()).send(any(), any());
    }

    @Test
    @WithMockUser
    void send_contentTooLong_returns400() throws Exception {
        String tooLong = "a".repeat(2001);

        mockMvc.perform(post("/api/chat-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatMessageRequest(tooLong))))
                .andExpect(status().isBadRequest());

        verify(chatMessageService, never()).send(any(), any());
    }

    @Test
    void send_unauthenticated_returns401() throws Exception {
        ChatMessageRequest request = new ChatMessageRequest("Kunde hat zugesagt");

        mockMvc.perform(post("/api/chat-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(chatMessageService, never()).send(any(), any());
    }
}
