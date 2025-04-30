package ru.netology.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.constant.ErrorMessages;
import ru.netology.error.ServerErrorException;
import ru.netology.model.File;
import ru.netology.repository.FileRepository;

import java.util.List;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public void uploadFile(String login, File file) {
        if (!fileRepository.saveFile(login, file)) {
            log.warn("Error upload file={}",
                    file.getFilename());
            throw new ServerErrorException(
                    ErrorMessages.ERR_UPLOAD.message);
        }
        log.info("File uploaded: login={}, filename={}",
                login, file.getFilename());
    }

    public File downloadFile(String login, String filename) {
        File file = fileRepository.getFile(login, filename);
        if (file == null) {
            log.warn("File not found: login={}, filename={}",
                    login, filename);
            throw new ServerErrorException(
                    ErrorMessages.ERR_UPLOAD.message);
        }
        log.info("File downloaded: login={}, filename={}",
                login, filename);
        return file;
    }

    public void deleteFile(String login, String filename) {
        if (!fileRepository.deleteFile(login, filename)) {
            log.warn("File not found for deletion: login={}, filename={}",
                    login, filename);
            throw new ServerErrorException(
                    ErrorMessages.ERR_DELETE.message);
        }
        log.info("File deleted: login={}, filename={}",
                login, filename);
    }

    public List<File> getFileList(String login, int limit) {
        return fileRepository.getFileList(login, limit);
    }

    public void renameFile(String login, String filename, String newFilename) {
        if (!fileRepository.renameFile(login, filename, newFilename)) {
            log.warn("Error rename file={}",
                    filename);
            throw new ServerErrorException(
                    ErrorMessages.ERR_UPLOAD.message);
        }
        log.info("File renamed: {} -> {}",
                filename, newFilename);
    }
}

