package ru.netology.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDownloadResponse {
    private String hash;
    private byte[] file;
}
