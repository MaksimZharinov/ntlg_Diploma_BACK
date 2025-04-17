package ru.netology.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.constant.ErrorMessages;
import ru.netology.dto.FileDownloadResponse;
import ru.netology.dto.FileListResponse;
import ru.netology.error.BadRequestException;
import ru.netology.error.ServerErrorException;
import ru.netology.repository.FileRepository;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public void uploadFile(String token, String filename, MultipartFile file) {
        if (!checkFilename(filename)) {
            throw new BadRequestException(ErrorMessages.ERR_INPUT.message);
        }
        try {
            String hash = calculateSHA256(file.getBytes());
            fileRepository.saveFile(
                    token,
                    filename,
                    file.getBytes(),
                    hash,
                    file.getSize());
            log.info("File uploaded: token={}, filename={}", token, filename);
        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage());
            throw new BadRequestException(ErrorMessages.ERR_INPUT.message);
        }
    }

    public FileDownloadResponse downloadFile(String token, String filename) {
        if (!checkFilename(filename)) {
            throw new BadRequestException(ErrorMessages.ERR_INPUT.message);
        }
        if (!fileRepository.checkFile(token, filename)) {
            throw new ServerErrorException(ErrorMessages.ERR_UPLOAD.message);
        }
        FileDownloadResponse file = fileRepository.getFile(token, filename);
        if (file.getFile() == null) {
            log.warn("File not found: token={}, filename={}", token, filename);
            throw new ServerErrorException(ErrorMessages.ERR_UPLOAD.message);
        }
        log.info("File downloaded: token={}, filename={}", token, filename);
        return file;
    }

    public void deleteFile(String token, String filename) {
        if (!fileRepository.deleteFile(token, filename)) {
            log.warn("File not found for deletion: token={}, filename={}",
                    token, filename);
            throw new ServerErrorException(ErrorMessages.ERR_DELETE.message);
        }
        log.info("File deleted: token={}, filename={}", token, filename);
    }

    public List<FileListResponse> getFileList(String token, int limit) {
        return fileRepository.getFileList(token, limit);
    }

    public void renameFile(String token, String filename, String newFilename) {
        if (!checkFilename(filename)) {
            throw new BadRequestException(ErrorMessages.ERR_INPUT.message);
        }
        if (!fileRepository.renameFile(token, filename, newFilename)) {
            log.warn("Error rename file");
            throw new ServerErrorException(ErrorMessages.ERR_UPLOAD.message);
        }
        log.info("File renamed: {} -> {}", filename, newFilename);
    }

    private String calculateSHA256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new ServerErrorException(ErrorMessages.ERR_HASH.message);
        }
    }

    private boolean checkFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            log.warn("Empty filename");
            return false;
        }
        return true;
    }
}

