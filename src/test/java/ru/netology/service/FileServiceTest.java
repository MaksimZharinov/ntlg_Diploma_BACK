package ru.netology.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.error.ServerErrorException;
import ru.netology.model.File;
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

        File file = mock(File.class);
        when(fileRepository.saveFile(any(), any()))
                .thenReturn(true);

        fileService
                .uploadFile("login", file);

        verify(fileRepository)
                .saveFile(eq("login"), eq(file));
    }

    @Test
    void downloadFileReturnFile() {

        File file = mock(File.class);
        when(file.getFilename())
                .thenReturn("filename");
        when(file.getFileData())
                .thenReturn(new byte[]{1, 2, 3});
        when(file.getHash())
                .thenReturn("hash");
        when(file.getSize())
                .thenReturn(3L);
        when(fileRepository.getFile(
                "login",
                "filename"))
                .thenReturn(file);

        File downloadFile = fileService
                .downloadFile("login", "filename");

        assertEquals("filename", downloadFile.getFilename());
        assertArrayEquals(new byte[]{1, 2, 3}, downloadFile.getFileData());
        assertEquals("hash", downloadFile.getHash());
        assertEquals(3L, downloadFile.getSize());
    }

    @Test
    void downloadFileThrowExceptionFileNotFound() {

        when(fileRepository.getFile(
                "login",
                "notExistFilename"))
                .thenReturn(null);

        assertThrows(ServerErrorException.class, () ->
                fileService.downloadFile(
                        "login",
                        "notExistFilename"));
    }

    @Test
    void deleteFileOk() {

        when(fileRepository.deleteFile(
                "login",
                "filename"))
                .thenReturn(true);

        assertDoesNotThrow(() -> fileService.deleteFile(
                "login",
                "filename"));
        verify(fileRepository).deleteFile(
                "login",
                "filename");
    }

    @Test
    void deleteFileThrowExceptionFileNotExists() {

        when(fileRepository.deleteFile(
                "login",
                "notExistFilename"))
                .thenReturn(false);

        assertThrows(ServerErrorException.class, () ->
                fileService.deleteFile(
                        "login",
                        "notExistFilename"));
    }

    @Test
    void getFileListReturnList() {

        List<File> mockList = List.of(
                new File(
                        "filename1",
                        100L),
                new File(
                        "filename2",
                        200L)
        );
        when(fileRepository.getFileList(
                "login",
                10))
                .thenReturn(mockList);

        List<File> result = fileService.getFileList(
                "login",
                10);

        assertEquals(2, result.size());
        assertEquals("filename1", result.get(0).getFilename());
    }
}
