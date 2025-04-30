package ru.netology.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.constant.ErrorMessages;
import ru.netology.dto.FileDownloadResponse;
import ru.netology.dto.FileRenameRequest;
import ru.netology.error.BadRequestException;
import ru.netology.error.ServerErrorException;
import ru.netology.model.File;
import ru.netology.service.FileService;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

@RestController
@Slf4j
@Data
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(
            value = "/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void uploadFile(
            HttpServletRequest request,
            @RequestParam(
                    value = "filename",
                    required = false) String filename,
            @RequestPart("file") MultipartFile multipartFileData
    ) {
        checkFilename(filename);
        String login = (String) request.getAttribute("login");
        Long size = multipartFileData.getSize();
        byte[] fileData = multipartToBytes(multipartFileData);
        String hash = calculateSHA256(fileData);
        File file = new File(filename, fileData, hash, size);
        log.info("Upload file: {}",
                filename);
        fileService.uploadFile(login, file);
    }

    @GetMapping(
            value = "/file",
            produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public FileDownloadResponse downloadFile(
            HttpServletRequest request,
            @RequestParam(
                    value = "filename",
                    required = false) String filename
    ) {
        checkFilename(filename);
        String login = (String) request.getAttribute("login");
        log.info("Download file: {}",
                filename);
        File file = fileService.downloadFile(login, filename);
        return new FileDownloadResponse(
                file.getHash(),
                file.getFileData());
    }

    @DeleteMapping("/file")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(
            HttpServletRequest request,
            @RequestParam("filename") String filename
    ) {
        checkFilename(filename);
        String login = (String) request.getAttribute("login");
        log.info("Delete file: {}",
                filename);
        fileService.deleteFile(login, filename);
    }

    @PutMapping("/file")
    @ResponseStatus(HttpStatus.OK)
    public void editFilename(
            HttpServletRequest request,
            @RequestParam(
                    value = "filename",
                    required = false) String filename,
            @RequestBody FileRenameRequest fileRequest
    ) {
        checkFilename(filename);
        String newFilename = fileRequest.getName();
        checkFilename(newFilename);
        String login = (String) request.getAttribute("login");
        log.info("Rename file: {} -> {}",
                filename, fileRequest.getName());
        fileService.renameFile(login, filename, newFilename);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<File> getFileList(
            HttpServletRequest request,
            @RequestParam(
                    value = "limit",
                    defaultValue = "10") int limit
    ) {
        String login = (String) request.getAttribute("login");
        log.info("Get files list, limit: {}",
                limit);
        return fileService.getFileList(login, limit);
    }

    private void checkFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            log.warn("Empty filename");
            throw new BadRequestException(
                    ErrorMessages.ERR_INPUT.message);
        }
    }

    private String calculateSHA256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new ServerErrorException(
                    ErrorMessages.ERR_HASH.message);
        }
    }

    private byte[] multipartToBytes(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                log.error("Empty file");
                throw new ServerErrorException(
                        ErrorMessages.ERR_UPLOAD.message);
            }
            return file.getBytes();
        } catch (Exception e) {
            throw new ServerErrorException(
                    ErrorMessages.ERR_UPLOAD.message);
        }
    }
}
