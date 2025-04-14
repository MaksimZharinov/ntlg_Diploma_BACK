package ru.netology.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import ru.netology.constant.AuthRole;
import ru.netology.constant.ErrorMessages;
import ru.netology.dto.ErrorResponse;
import ru.netology.filter.TokenAuthFilter;
import ru.netology.util.ResponseUtils;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ErrorResponse errorResponse = new ErrorResponse(
            ErrorMessages.ERR_TOKEN.message, 401);
    private final TokenAuthFilter tokenAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/logout").hasAnyAuthority(AuthRole.TOKEN.role)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        tokenAuthFilter,
                        AuthorizationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, ex) -> {
                            log.warn("Auth failed for {}", request.getRequestURI());
                            ResponseUtils.sendErrorResponse(
                                    response,
                                    errorResponse.getCode(),
                                    errorResponse.getMessage());
                        })
                );
        log.debug("Security initialization complete");
        return http.build();
    }


}
