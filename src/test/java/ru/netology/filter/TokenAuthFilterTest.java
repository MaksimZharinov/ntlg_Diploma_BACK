package ru.netology.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.netology.constant.ErrorMessages;
import ru.netology.repository.AuthRepository;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenAuthFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AuthRepository authRepository;
    @InjectMocks
    private TokenAuthFilter tokenAuthFilter;

    @Test
    void doFilterSkipLogin() throws Exception {

        when(request.getRequestURI())
                .thenReturn("/login");
        FilterChain mockChain = mock(FilterChain.class);

        tokenAuthFilter.doFilterInternal(
                request,
                response,
                mockChain);

        verify(mockChain).doFilter(request, response);
        verifyNoInteractions(authRepository);
    }

    @Test
    void doFilterBlockMissingToken() throws Exception {

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter())
                .thenReturn(new PrintWriter(stringWriter));
        when(request.getRequestURI())
                .thenReturn("/file");
        when(request.getHeader("auth-token"))
                .thenReturn(null);

        tokenAuthFilter.doFilterInternal(
                request,
                response,
                mock(FilterChain.class));

        verify(response).setStatus(401);
        assertTrue(stringWriter
                .toString()
                .contains(ErrorMessages.ERR_TOKEN.message));
    }

    @Test
    void doFilterBlockInvalidToken() throws Exception {

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter())
                .thenReturn(new PrintWriter(stringWriter));
        when(request.getRequestURI())
                .thenReturn("/file");
        when(request.getHeader("auth-token"))
                .thenReturn("invalid_token");
        when(authRepository.checkToken("invalid_token"))
                .thenReturn(false);


        tokenAuthFilter.doFilterInternal(
                request,
                response,
                mock(FilterChain.class));

        verify(response).setStatus(401);
        verify(authRepository).checkToken("invalid_token");
    }

    @Test
    void doFilterAuthenticateValidToken() throws Exception {

        FilterChain mockChain = mock(FilterChain.class);
        when(request.getRequestURI())
                .thenReturn("/file");
        when(request.getHeader("auth-token"))
                .thenReturn("valid_token");
        when(authRepository.checkToken("valid_token"))
                .thenReturn(true);

        tokenAuthFilter.doFilterInternal(
                request,
                response,
                mockChain);

        verify(mockChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder
                .getContext()
                .getAuthentication());
    }

    @Test
    void filterBeImmutable() {

        assertDoesNotThrow(() -> {
            tokenAuthFilter
                    .getClass()
                    .getDeclaredField("authRepository")
                    .setAccessible(true);
            tokenAuthFilter
                    .getClass()
                    .getDeclaredField("errorResponse")
                    .setAccessible(true);
        });
    }
}
