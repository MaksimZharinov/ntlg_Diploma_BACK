package ru.netology.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.dto.ErrorResponse;
import ru.netology.error.BadRequestException;
import ru.netology.error.ServerErrorException;
import ru.netology.error.UnauthorizedException;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(BadRequestException e) {
        log.error("BAD REQUEST: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), 400);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorized(UnauthorizedException e) {
        log.error("UNAUTHORIZED ACCESS: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), 401);
    }

    @ExceptionHandler(ServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(ServerErrorException e) {
        log.error("SERVER ERROR: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), 500);
    }
}
