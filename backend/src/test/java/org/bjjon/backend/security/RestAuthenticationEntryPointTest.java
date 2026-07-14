package org.bjjon.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bjjon.backend.exception.ErrorMessage;
import org.bjjon.backend.exception.auth.AuthException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private RestAuthenticationEntryPoint entryPoint;

    @Test
    void commence_setsUnauthorizedStatus() throws Exception {
        when(response.getWriter()).thenReturn(printWriter);
        BadCredentialsException authException = new BadCredentialsException("bad credentials");

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_setsJsonContentType() throws Exception {
        when(response.getWriter()).thenReturn(printWriter);
        BadCredentialsException authException = new BadCredentialsException("bad credentials");

        entryPoint.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void commence_writesErrorMessageWithExceptionMessageAndUnauthorizedStatus() throws Exception {
        when(response.getWriter()).thenReturn(printWriter);
        BadCredentialsException authException = new BadCredentialsException("bad credentials");

        Instant before = Instant.now();
        entryPoint.commence(request, response, authException);
        Instant after = Instant.now();

        ArgumentCaptor<ErrorMessage> captor = ArgumentCaptor.forClass(ErrorMessage.class);
        verify(objectMapper).writeValue(eq(printWriter), captor.capture());

        ErrorMessage errorMessage = captor.getValue();
        assertEquals("bad credentials", errorMessage.message());
        assertEquals(401, errorMessage.status());
        assertNotNull(errorMessage.timestamp());
        assertFalse(errorMessage.timestamp().isBefore(before));
        assertFalse(errorMessage.timestamp().isAfter(after));
    }

    @Test
    void commence_withCustomAuthException_usesItsMessage() throws Exception {
        when(response.getWriter()).thenReturn(printWriter);
        AuthException authException = new AuthException();

        entryPoint.commence(request, response, authException);

        ArgumentCaptor<ErrorMessage> captor = ArgumentCaptor.forClass(ErrorMessage.class);
        verify(objectMapper).writeValue(eq(printWriter), captor.capture());

        assertEquals("You are not authorized. Please Login again.", captor.getValue().message());
    }
}
