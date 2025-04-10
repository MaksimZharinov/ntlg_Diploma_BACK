package ru.netology.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.constant.ErrorMessages;
import ru.netology.repository.AuthRepository;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {

    private final AuthRepository authRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        String token = request.getHeader("auth-token");
        log.debug("Checking token for URL: {}", request.getRequestURI());

        if (request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response);
        }

        if (token == null || !authRepository.checkToken(token)) {
            log.warn("Token not valid to URL: {}", request.getRequestURI());
            response.sendError(401, ErrorMessages.ERR_TOKEN.message);
        }
        filterChain.doFilter(request, response);
    }
}
