package ru.netology.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.constant.ErrorMessages;
import ru.netology.dto.LoginRequest;
import ru.netology.error.BadRequestException;
import ru.netology.repository.AuthRepository;

import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository,
                       PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(LoginRequest loginRequest) {
        try {
            String dbPassword = authRepository
                    .getPassword(loginRequest.getLogin());
            if (dbPassword == null || dbPassword.isEmpty()) {
                log.warn("User not found: {}",
                        loginRequest.getLogin());
                throw new BadRequestException(
                        ErrorMessages.ERR_LOGIN.message);
            }
            String userPassword = passwordEncoder
                    .encode(loginRequest.getPassword());
            if (dbPassword.equals(userPassword)) {
                String token = UUID.randomUUID().toString();
                log.info("Generated token {} to user: {}",
                        token, loginRequest.getLogin());
                authRepository
                        .saveToken(loginRequest.getLogin(), token);
                return token;
            }
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        }
        log.warn("Something wrong in AuthService");
        throw new BadRequestException(
                ErrorMessages.ERR_LOGIN.message);
    }

    public void logout(String token) {
        log.debug("Logging out token: {}", token);
        authRepository.dropToken(token);
    }

    public boolean checkToken(String token) {
        log.debug("Checking token: {}", token);
        return authRepository.checkToken(token);
    }
}
