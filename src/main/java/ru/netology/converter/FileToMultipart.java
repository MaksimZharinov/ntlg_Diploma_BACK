package ru.netology.converter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import ru.netology.dto.FileDownloadResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class FileToMultipart implements HttpMessageConverter<FileDownloadResponse> {

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return FileDownloadResponse.class.equals(clazz) &&
                MediaType.MULTIPART_FORM_DATA.includes(mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.MULTIPART_FORM_DATA);
    }

    @Override
    public FileDownloadResponse read(
            Class<? extends FileDownloadResponse> clazz,
            HttpInputMessage inputMessage
    )
            throws IOException, HttpMessageNotReadableException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void write(
            FileDownloadResponse file,
            MediaType mediaType,
            HttpOutputMessage outputMessage
    )
            throws IOException, HttpMessageNotWritableException {

        String boundary = "netology";

        // Напрямую выводим multipart-формат
        OutputStream os = outputMessage.getBody();

        // Первый блок: хэш
        os.write(("--" + boundary + "\r\n").getBytes());
        os.write("Content-Disposition: form-data; name=\"hash\"\r\n\r\n".getBytes());
        os.write(file.getHash().getBytes());
        os.write("\r\n".getBytes());

        // Второй блок: файл
        os.write(("--" + boundary + "\r\n").getBytes());
        os.write("Content-Disposition: form-data; name=\"file\"\r\n".getBytes());
        os.write("Content-Type: application/octet-stream\r\n\r\n".getBytes());
        os.write(file.getFile());
        os.write("\r\n".getBytes());

        // Заканчиваем multipart
        os.write(("--" + boundary + "--\r\n").getBytes());
    }
}
