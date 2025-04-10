package ru.netology.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.netology.constant.ErrorMessages;
import ru.netology.constant.SqlQueries;
import ru.netology.error.BadRequestException;
import ru.netology.error.UnauthorizedException;

@Repository
@Slf4j
public class AuthRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getPassword(String login) throws UnauthorizedException {
        log.debug("Fetching password for user: {}", login);
        try {
            return jdbcTemplate.queryForObject(SqlQueries.CHECK_PASSWORD.query,
                    String.class, login);
        } catch (Exception e) {
            throw new BadRequestException(ErrorMessages.ERR_LOGIN.message);
        }
    }

    public void saveToken(String login, String token) {
        log.debug("Saving token for user: {}", login);
        jdbcTemplate.update(SqlQueries.CREATE_TOKEN.query, login, token);
    }

    public boolean checkToken(String token) {
        log.debug("Checking if token exists: {}", token);
        Integer count = jdbcTemplate.queryForObject(SqlQueries.CHECK_TOKEN.query,
                Integer.class, token);
        return count != null && count == 1;
    }

    public void dropToken(String token) {
        log.debug("Deleting token: {}", token);
        jdbcTemplate.update(SqlQueries.DROP_TOKEN.query, token);
    }
}

