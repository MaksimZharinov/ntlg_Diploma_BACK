package ru.netology.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.constant.ErrorMessages;
import ru.netology.dto.LoginRequest;
import ru.netology.error.BadRequestException;
import ru.netology.error.UnauthorizedException;
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

        authRepository.refreshToken(loginRequest.getLogin());
        
        String dbPassword = authRepository.getPassword(loginRequest.getLogin());
        log.debug("DB Password: {}", dbPassword);

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
        log.debug("Logout for token: {}", token);
        if (!authRepository.dropToken(token)) {
            log.warn("Token not found: {}", token);
            throw new UnauthorizedException(ErrorMessages.ERR_TOKEN.message);
        }
    }

    public boolean checkToken(String token) {
        log.debug("Checking token: {}", token);
        return authRepository.checkToken(token);
    }
}
