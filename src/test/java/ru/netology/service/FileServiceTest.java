package ru.netology.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileDownloadResponse;
import ru.netology.dto.FileListResponse;
import ru.netology.error.BadRequestException;
import ru.netology.error.ServerErrorException;
import ru.netology.repository.FileRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileRepository fileRepository;
    @InjectMocks
    private FileService fileService;

    @Test
    void uploadFileWhenValid() throws IOException {

        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(file.getSize()).thenReturn(3L);
        doNothing().when(fileRepository)
                .saveFile(any(), any(), any(), any(), anyLong());

        fileService.uploadFile(
                "valid_token",
                "test.txt",
                file);

        verify(fileRepository).saveFile(
                eq("valid_token"),
                eq("test.txt"),
                any(),
                anyString(),
                eq(3L)
        );
    }

    @Test
    void uploadFilThrowExceptionFilenameEmpty() {

        assertThrows(BadRequestException.class, () ->
                fileService.uploadFile(
                        "token",
                        "",
                        mock(MultipartFile.class)));
        verifyNoInteractions(fileRepository);
    }

    @Test
    void downloadFileReturnFile() {

        when(fileRepository.checkFile(
                "token",
                "test.txt"))
                .thenReturn(true);
        when(fileRepository.getFile(
                "token",
                "test.txt"))
                .thenReturn(new FileDownloadResponse(
                        "hash",
                        new byte[]{1, 2, 3}));

        FileDownloadResponse response = fileService
                .downloadFile("token", "test.txt");

        assertEquals("hash", response.getHash());
        assertArrayEquals(new byte[]{1, 2, 3}, response.getFile());
    }

    @Test
    void downloadFileThrowExceptionFileNotFound() {

        when(fileRepository.checkFile(
                "token",
                "unknown.txt"))
                .thenReturn(false);

        assertThrows(ServerErrorException.class, () ->
                fileService.downloadFile(
                        "token",
                        "unknown.txt"));
    }

    @Test
    void deleteFileOk() {

        when(fileRepository.deleteFile(
                "token",
                "test.txt"))
                .thenReturn(true);

        assertDoesNotThrow(() -> fileService.deleteFile(
                "token",
                "test.txt"));
        verify(fileRepository).deleteFile(
                "token",
                "test.txt");
    }

    @Test
    void deleteFileThrowExceptionFileNotExists() {

        when(fileRepository.deleteFile(
                "token",
                "unknown.txt"))
                .thenReturn(false);

        assertThrows(ServerErrorException.class, () ->
                fileService.deleteFile(
                        "token",
                        "unknown.txt"));
    }

    @Test
    void getFileListReturnList() {

        List<FileListResponse> mockList = List.of(
                new FileListResponse(
                        "file1.txt",
                        100),
                new FileListResponse(
                        "file2.txt",
                        200)
        );
        when(fileRepository.getFileList(
                "token",
                10))
                .thenReturn(mockList);

        List<FileListResponse> result = fileService.getFileList(
                "token",
                10);

        assertEquals(2, result.size());
        assertEquals("file1.txt", result.get(0).getFilename());
    }
}
