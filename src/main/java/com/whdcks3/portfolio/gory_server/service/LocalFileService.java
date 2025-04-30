package com.whdcks3.portfolio.gory_server.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.whdcks3.portfolio.gory_server.exception.FileUploadFailureException;

@Service
public class LocalFileService implements FileService {

    @Value("${upload.image.location}")
    String location;

    @Override
    public String upload(MultipartFile file) {
        try {
            String filename = UUID.randomUUID().toString(); // 랜덤난수생성,
            Path filePath = Paths.get(location, filename);

            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            return filename;
        } catch (IOException e) {
            throw new FileUploadFailureException(e);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            Path filePath = Paths.get(location, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileUploadFailureException(e);
        }

    }
}
