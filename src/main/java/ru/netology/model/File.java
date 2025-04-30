package ru.netology.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class File {
    private String filename;
    private byte[] fileData;
    private String hash;
    private Long size;

    public File(
            String filename,
            byte[] fileData,
            String hash,
            Long size
    ) {
        this.filename = filename;
        this.fileData = fileData;
        this.hash = hash;
        this.size = size;
    }

    public File(String filename, Long size) {
        this.filename = filename;
        this.size = size;
    }
}
