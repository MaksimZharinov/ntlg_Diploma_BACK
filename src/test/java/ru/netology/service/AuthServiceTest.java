package ru.netology.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.constant.ErrorMessages;
import ru.netology.dto.LoginRequest;
import ru.netology.error.BadRequestException;
import ru.netology.error.UnauthorizedException;
import ru.netology.repository.AuthRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnTokenWhenCredentialIsValid() {

        LoginRequest request = new LoginRequest();
        request.setLogin("user");
        request.setPassword("password");
        when(authRepository.getPassword("user"))
                .thenReturn("encodePassword");
        when(passwordEncoder.matches(
                "password",
                "encodePassword"))
                .thenReturn(true);
        doNothing().when(authRepository)
                .saveToken(any(), any());

        String token = authService.login(request);

        assertNotNull(token);
        verify(authRepository).getPassword("user");
        verify(authRepository).refreshToken("user");
        verify(authRepository).saveToken(eq("user"), eq(token));
    }

    @Test
    void loginThrowExceptionInvalidUsername() {

        LoginRequest request = new LoginRequest();
        request.setLogin("unknown");
        request.setPassword("password");
        when(authRepository.getPassword("unknown")).thenThrow(
                new BadRequestException(ErrorMessages.ERR_LOGIN.message));

        assertThrows(BadRequestException.class, () ->
                authService.login(request));
    }

    @Test
    void loginThrowExceptionInvalidPassword() {

        LoginRequest request = new LoginRequest();
        request.setLogin("user");
        request.setPassword("wrongPassword");
        when(authRepository.getPassword("user"))
                .thenReturn("encodePassword");
        when(passwordEncoder.matches(
                "wrongPassword",
                "encodePassword"))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () ->
                authService.login(request));
    }

    @Test
    void logoutDeleteToken() {

        when(authRepository.dropToken("valid_token")).thenReturn(true);

        assertDoesNotThrow(() -> authService.logout("valid_token"));
        verify(authRepository).dropToken("valid_token");
    }

    @Test
    void logoutThrowExceptionInvalidToken() {

        when(authRepository.dropToken("invalid")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () ->
                authService.logout("invalid"));
    }

    @Test
    void checkTokenTrueValidToken() {

        when(authRepository.checkToken("valid")).thenReturn(true);

        assertTrue(authService.checkToken("valid"));
    }

    @Test
    void checkTokenFalseInvalidToken() {

        when(authRepository.checkToken("invalid")).thenReturn(false);

        assertFalse(authService.checkToken("invalid"));
    }

}
