package ru.netology.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.constant.ErrorMessages;
import ru.netology.error.BadRequestException;
import ru.netology.error.UnauthorizedException;
import ru.netology.model.User;
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

        User user = new User(
                "user",
                "password");
        when(authRepository.getPassword(user.getLogin()))
                .thenReturn("encodePassword");
        when(passwordEncoder.matches(
                "password",
                "encodePassword"))
                .thenReturn(true);
        doNothing().when(authRepository)
                .saveToken(any(), any());

        String token = authService.login(user);

        assertNotNull(token);
        verify(authRepository)
                .getPassword("user");
        verify(authRepository)
                .dropToken("user");
        verify(authRepository)
                .saveToken(eq("user"), eq(token));
    }

    @Test
    void loginThrowExceptionInvalidUsername() {

        User user = new User(
                "unknown",
                "password");
        when(authRepository.getPassword("unknown"))
                .thenThrow(new BadRequestException(
                        ErrorMessages.ERR_LOGIN.message));

        assertThrows(BadRequestException.class, () ->
                authService.login(user));
    }

    @Test
    void loginThrowExceptionInvalidPassword() {

        User user = new User(
                "user",
                "wrongPassword");
        when(authRepository.getPassword("user"))
                .thenReturn("encodePassword");
        when(passwordEncoder.matches(
                "wrongPassword",
                "encodePassword"))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () ->
                authService.login(user));
    }

    @Test
    void logoutDeleteToken() {

        when(authRepository.dropToken("valid_token"))
                .thenReturn(true);

        assertDoesNotThrow(() -> authService
                .logout("valid_token"));
        verify(authRepository)
                .dropToken("valid_token");
    }

    @Test
    void logoutThrowExceptionInvalidToken() {

        when(authRepository.dropToken("invalid"))
                .thenReturn(false);

        assertThrows(UnauthorizedException.class, () ->
                authService.logout("invalid"));
    }
}
