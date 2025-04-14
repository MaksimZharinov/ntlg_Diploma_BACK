package ru.netology.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtils {

    public static void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write(
                String.format(
                        "{\"message\":\"%s\",\"code\":%d}",
                        message,
                        status
                )
        );
    }
}
