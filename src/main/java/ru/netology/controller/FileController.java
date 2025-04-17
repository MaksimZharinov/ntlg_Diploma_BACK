package ru.netology.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileDownloadResponse;
import ru.netology.dto.FileListResponse;
import ru.netology.dto.FileRenameRequest;
import ru.netology.service.FileService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(
            value = "/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam(
                    value = "filename",
                    required = false) String filename,
            @RequestPart("file") MultipartFile file
    ) {
        log.info("Upload file: {}", filename);
        fileService.uploadFile(token, filename, file);
    }

    @GetMapping(value = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDownloadResponse downloadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam(
                    value = "filename",
                    required = false) String filename
    ) {
        log.info("Download file: {}", filename);
        return fileService.downloadFile(token, filename);
    }

    @DeleteMapping("/file")
    public void deleteFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename
    ) {
        log.info("Delete file: {}", filename);
        fileService.deleteFile(token, filename);
    }

    @PutMapping("/file")
    public void editFilename(
            @RequestHeader("auth-token") String token,
            @RequestParam(
                    value = "filename",
                    required = false) String filename,
            @RequestBody FileRenameRequest request
    ) {
        log.info("Rename file: {} -> {}", filename, request.getName());
        fileService.renameFile(token, filename, request.getName());
    }

    @GetMapping("/list")
    public List<FileListResponse> getFileList(
            @RequestHeader("auth-token") String token,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        log.info("Get files list, limit: {}", limit);
        return fileService.getFileList(token, limit);
    }
}
