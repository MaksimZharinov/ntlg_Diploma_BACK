package ru.netology.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.constant.ErrorMessages;
import ru.netology.error.BadRequestException;
import ru.netology.error.UnauthorizedException;
import ru.netology.model.User;
import ru.netology.repository.AuthRepository;

import java.util.UUID;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(User user) {
        authRepository
                .dropToken(user.getLogin());
        String dbPassword = authRepository
                .getPassword(user.getLogin());
        log.debug("DB Password: {}",
                dbPassword);
        if (dbPassword == null || dbPassword.isEmpty()) {
            log.warn("User not found: {}",
                    user.getLogin());
            throw new BadRequestException(
                    ErrorMessages.ERR_LOGIN.message);
        }
        if (!passwordEncoder
                .matches(user.getPassword(), dbPassword)) {
            log.warn("Invalid password for user: {}",
                    user.getLogin());
            throw new BadRequestException(
                    ErrorMessages.ERR_LOGIN.message);
        }
        String token = UUID.randomUUID().toString();
        log.info("Generated token {} for user: {}",
                token, user.getLogin());
        authRepository
                .saveToken(user.getLogin(), token);
        return token;
    }

    public void logout(String login) {
        log.debug("Logout for user: {}",
                login);
        if (!authRepository
                .dropToken(login)) {
            log.warn("Token not found");
            throw new UnauthorizedException(
                    ErrorMessages.ERR_TOKEN.message);
        }
    }
}
