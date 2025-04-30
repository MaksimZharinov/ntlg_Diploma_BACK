package ru.netology.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.netology.constant.SqlQueries;
import ru.netology.model.File;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FileRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean saveFile(String login, File file) {
        log.debug("Saving file in DB {}",
                file.getFilename());
        return jdbcTemplate.update(
                SqlQueries.SAVE_FILE.query,
                login,
                file.getFilename(),
                file.getFileData(),
                file.getHash(),
                file.getSize()) > 0;
    }

    public File getFile(String login, String filename) {
        log.debug("Getting file in DB {}",
                filename);
        try {
            return jdbcTemplate.queryForObject(
                    SqlQueries.GET_FILE.query,
                    (rs, rowNum) -> new File(
                            rs.getString("filename"),
                            rs.getBytes("file_data"),
                            rs.getString("hash"),
                            rs.getLong("size")
                    ),
                    login, filename
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean deleteFile(String login, String filename) {
        log.debug("Deleting file in DB {}",
                filename);
        return jdbcTemplate.update(
                SqlQueries.DELETE_FILE.query,
                login, filename) > 0;
    }

    public List<File> getFileList(String login, int limit) {
        log.debug("Getting file list in DB for user: {}",
                login);
        return jdbcTemplate.query(
                SqlQueries.GET_FILE_LIST.query,
                (rs, rowNum) -> new File(
                        rs.getString("filename"),
                        rs.getLong("size")
                ),
                login, limit
        );
    }

    public boolean renameFile(
            String login,
            String filename,
            String newFilename
    ) {
        log.debug("Renaming file in DB {}",
                filename);
        return jdbcTemplate.update(
                SqlQueries.RENAME_FILE.query,
                newFilename, login, filename) > 0;
    }
}
