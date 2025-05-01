package ru.netology.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileListResponse {
    private String filename;
    private Long size;
}
