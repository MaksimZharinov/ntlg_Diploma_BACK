package ru.netology.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.constant.AuthRole;
import ru.netology.constant.ErrorMessages;
import ru.netology.dto.ErrorResponse;
import ru.netology.repository.AuthRepository;
import ru.netology.util.ResponseUtils;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {

    private final ErrorResponse errorResponse = new ErrorResponse(
            ErrorMessages.ERR_TOKEN.message, 401);
    private final AuthRepository authRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        if (request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("auth-token");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        log.debug("Checking token for URL: {}",
                request.getRequestURI());
        if (token == null || !authRepository
                .checkToken(token)) {
            log.warn("Token INVALID for URL: {}",
                    request.getRequestURI());
            ResponseUtils.sendErrorResponse(
                    response,
                    errorResponse.getCode(),
                    errorResponse.getMessage());
            return;
        } else {
            log.debug("Token VALID for URL: {}",
                    request.getRequestURI());
            String login = authRepository.getLogin(token);
            request.setAttribute("login", login);
            UsernamePasswordAuthenticationToken
                    auth = new UsernamePasswordAuthenticationToken(
                    "token_user",
                    null,
                    List.of(new SimpleGrantedAuthority(
                            AuthRole.TOKEN.role))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
