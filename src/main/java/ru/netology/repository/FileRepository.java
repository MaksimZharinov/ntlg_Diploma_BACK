package ru.netology.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.netology.constant.ErrorMessages;
import ru.netology.constant.SqlQueries;
import ru.netology.dto.FileDownloadResponse;
import ru.netology.dto.FileListResponse;
import ru.netology.error.UnauthorizedException;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FileRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveFile(
            String token,
            String filename,
            byte[] fileData,
            String hash,
            long size) {
        String login = getLogin(token);
        log.debug("Saving file in DB {}", filename);
        jdbcTemplate.update(
                SqlQueries.SAVE_FILE.query,
                login, filename, fileData, hash, size
        );
    }

    public FileDownloadResponse getFile(String token, String filename) {
        String login = getLogin(token);
        if (login == null) {
            log.warn("Invalid token {}", token);
            throw new UnauthorizedException(ErrorMessages.ERR_TOKEN.message);
        }
        log.debug("Getting file in DB {}", filename);
        return jdbcTemplate.queryForObject(
                SqlQueries.GET_FILE.query,
                (rs, rowNum) -> new FileDownloadResponse(
                        rs.getString("hash"),
                        rs.getBytes("file_data")
                ),
                login, filename
        );
    }

    public boolean deleteFile(String token, String filename) {
        String login = getLogin(token);
        log.debug("Deleting file in DB {}", filename);
        return jdbcTemplate.update(
                SqlQueries.DELETE_FILE.query,
                login, filename
        ) > 0;
    }

    public List<FileListResponse> getFileList(String token, int limit) {
        String login = getLogin(token);
        log.debug("Getting file list in DB for user: {}", login);
        return jdbcTemplate.query(
                SqlQueries.GET_FILE_LIST.query,
                (rs, rowNum) -> new FileListResponse(
                        rs.getString("filename"),
                        rs.getLong("size")
                ),
                login, limit
        );
    }

    public boolean renameFile(String token, String filename, String newFilename) {
        String login = getLogin(token);
        log.debug("Renaming file in DB {}", filename);
        return jdbcTemplate.update(
                SqlQueries.RENAME_FILE.query,
                newFilename, login, filename
        ) > 0;
    }

    private String getLogin(String token) {
        return jdbcTemplate.queryForObject(
                SqlQueries.GET_LOGIN_BY_TOKEN.query,
                String.class,
                token);
    }

    public boolean checkFile(String token, String filename) {
        String login = getLogin(token);
        return jdbcTemplate.queryForObject(
                SqlQueries.FILE_IS_EXIST.query,
                Integer.class,
                login, filename
        ) > 0;
    }
}
