package ru.netology.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.dto.LoginRequest;
import ru.netology.dto.LoginResponse;
import ru.netology.error.BadRequestException;
import ru.netology.model.User;
import ru.netology.service.AuthService;

@RestController
@Slf4j
@Data
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request
    ) {
        User user = new User(
                request.getLogin(),
                request.getPassword());
        log.info("Login attempt for user: {}",
                user.getLogin());
        try {
            String token = authService
                    .login(user);
            log.debug("Successfully generated token for user: {}",
                    user.getLogin());
            return new LoginResponse(token);
        } catch (BadRequestException e) {
            log.warn("Login failed for user: {} - {}",
                    request.getLogin(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(
            HttpServletRequest request
    ) {
        String login = request
                .getAttribute("login")
                .toString();
        log.info("Logout request for user: {}",
                login);
        authService
                .logout(login);
    }
}