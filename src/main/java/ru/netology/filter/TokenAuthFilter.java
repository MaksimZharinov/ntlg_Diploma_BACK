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
import ru.netology.constant.ErrorMessages;
import ru.netology.repository.AuthRepository;

import java.io.IOException;
import java.util.List;

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

        if (request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("auth-token");
        log.debug("Checking token for URL: {}", request.getRequestURI());

        if (token == null || !authRepository.checkToken(token)) {
            log.warn("Token not valid to URL: {}", request.getRequestURI());
            response.sendError(401, ErrorMessages.ERR_TOKEN.message);
            return;
        } else {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "token_user",
                    null,
                    List.of(new SimpleGrantedAuthority("VALID_TOKEN")) // Магическая строка
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
