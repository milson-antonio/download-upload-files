package com.milsondev.downloaduploadfiles.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    private final Path uploadPath = Paths.get("uploads");

    public StorageService() throws IOException {
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    public String save(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path destination = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }

    public byte[] load(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public void delete(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

}