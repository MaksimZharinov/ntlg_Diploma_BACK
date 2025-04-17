package ru.netology.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadRequest {
    private String filename;
    private MultipartFile file;
}
