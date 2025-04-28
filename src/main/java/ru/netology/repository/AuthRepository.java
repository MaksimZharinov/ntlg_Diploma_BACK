package ru.netology.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.netology.constant.ErrorMessages;
import ru.netology.constant.SqlQueries;
import ru.netology.error.BadRequestException;

@Repository
@Slf4j
@Data
@AllArgsConstructor
public class AuthRepository {

    private final JdbcTemplate jdbcTemplate;

    public String getPassword(String login) {
        log.debug("Fetching password for user: {}",
                login);
        try {
            return jdbcTemplate.queryForObject(
                    SqlQueries.CHECK_PASSWORD.query,
                    String.class,
                    login);
        } catch (EmptyResultDataAccessException e) {
            log.warn("User not found: {}",
                    login);
            throw new BadRequestException(
                    ErrorMessages.ERR_LOGIN.message);
        } catch (DataAccessException e) {
            log.error("Database error while fetching password for user: {}",
                    login, e);
            throw new BadRequestException(
                    ErrorMessages.ERR_LOGIN.message);
        }
    }

    public String getLogin(String token) {
        log.debug("Fetching login for token: {}",
                token);
        return jdbcTemplate.queryForObject(
                SqlQueries.GET_LOGIN_BY_TOKEN.query,
                String.class,
                token);
    }

    public void saveToken(String login, String token) {
        log.debug("Saving token for user: {}",
                login);
        jdbcTemplate.update(SqlQueries.CREATE_TOKEN.query,
                login,
                token);
    }

    public boolean checkToken(String token) {
        log.debug("Checking if token exists: {}",
                token);
        Integer count = jdbcTemplate.queryForObject(
                SqlQueries.CHECK_TOKEN.query,
                Integer.class,
                token);
        return count != null && count == 1;
    }

    public boolean dropToken(String login) {
        log.debug("Deleting token");
        return jdbcTemplate.update(
                SqlQueries.DROP_TOKEN.query,
                login) > 0;
    }
}

