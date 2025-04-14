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

        String dbPassword = authRepository.getPassword(loginRequest.getLogin());

        log.debug("DB Password: {}", dbPassword);
        log.debug("Input Password: {}", loginRequest.getPassword());

        if (dbPassword == null || dbPassword.isEmpty()) {
            log.warn("User not found: {}", loginRequest.getLogin());
            throw new BadRequestException(ErrorMessages.ERR_LOGIN.message);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), dbPassword)) {
            log.warn("Invalid password for user: {}", loginRequest.getLogin());
            throw new BadRequestException(ErrorMessages.ERR_LOGIN.message);
        }

        String token = UUID.randomUUID().toString();
        log.info("Generated token {} for user: {}", token, loginRequest.getLogin());
        authRepository.saveToken(loginRequest.getLogin(), token);
        return token;
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
