package ru.netology.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.netology.dto.LoginRequest;
import ru.netology.dto.LoginResponse;
import ru.netology.error.BadRequestException;
import ru.netology.service.AuthService;

@RestController
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getLogin());
        try {
            String token = authService.login(request);
            log.debug("Successfully generated token for user: {}",
                    request.getLogin());
            return new LoginResponse(token);
        } catch (BadRequestException e) {
            log.warn("Login failed for user: {} - {}",
                    request.getLogin(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestHeader("auth-token") String token) {
        log.info("Logout request for token: {}", token);
        authService.logout(token);
    }
}