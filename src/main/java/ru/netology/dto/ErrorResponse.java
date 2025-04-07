package ru.netology.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String message;
    private final int code;
}
