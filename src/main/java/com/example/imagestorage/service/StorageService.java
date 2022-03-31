package com.example.imagestorage.service;

import com.example.imagestorage.exception.StorageException;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {
    private static Path root;
    private final Logger logger;

    public StorageService(Logger logger) {
        this.logger = logger;
        String imagePath = "Images";
        new File(imagePath).mkdirs();
        root = Paths.get(imagePath);
    }

    public void store(UUID fileUUID, String fileExtension, MultipartFile file) {
        String filename = fileUUID.toString() + "." + fileExtension;
        try {
            Files.copy(file.getInputStream(), root.resolve(filename));
        } catch (IOException e) {
            logger.error("Could not store file with UDID = " + filename);
            throw new StorageException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Resource read(UUID fileUUID, String fileExtension) {
        try {
            String filename = fileUUID.toString() + "." + fileExtension;
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                logger.error("Could not read file with UDID = " + fileUUID);
                throw new StorageException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            logger.error("MalformedURL: " + e.getMessage());
            throw new StorageException("MalformedURL: " + e.getMessage());
        }
    }

    public void remove(UUID fileUUID, String fileExtension) {
        try {
            String filename = fileUUID.toString() + "." + fileExtension;
            Path file = root.resolve(filename);
            FileSystemUtils.deleteRecursively(file);
        } catch (IOException e) {
            logger.error("Could not remove file with UDID = " + fileUUID);
            throw new StorageException("Could not remove file. " + e.getMessage());
        }
    }
}
