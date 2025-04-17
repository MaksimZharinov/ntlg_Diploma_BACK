package ru.netology.constant;

public enum ErrorMessages {
    // 400
    ERR_LOGIN("Bad credentials"),
    ERR_INPUT("Error input data"),

    // 401
    ERR_TOKEN("Unauthorized error"),

    // 500
    ERR_HASH("Hash calculation error"),
    ERR_DELETE("Error delete file"),
    ERR_UPLOAD("Error upload file");

    public final String message;

    ErrorMessages(String message) {
        this.message = message;
    }
}