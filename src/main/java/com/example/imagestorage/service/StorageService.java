package com.example.imagestorage.service;

import com.example.imagestorage.ConfigurationProperties;
import com.example.imagestorage.exception.StorageException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileFilter;
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

    public StorageService(Logger logger, ConfigurationProperties config) {
        this.logger = logger;
        root = Paths.get(config.getPath());
    }

    public void store(UUID fileUUID, MultipartFile file) {
        String filename = fileUUID.toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), root.resolve(filename));
        } catch (IOException e) {
            logger.error("Could not store file with UDID = " + filename);
            throw new StorageException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Resource read(UUID fileUUID) {
        try {
            FileFilter filter = new WildcardFileFilter(fileUUID.toString()+".*");
            File[] files = new File(root.toString()).listFiles(filter);
            if (files == null || files.length == 0)
                throw new StorageException("Could not find file because it not exist");
            Path file = files[0].toPath();
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

    public void remove(UUID fileUUID) {
        try {
            FileFilter filter = new WildcardFileFilter(fileUUID.toString()+".*");
            File[] files = new File(root.toString()).listFiles(filter);
            if (files == null || files.length == 0)
                throw new StorageException("Could not remove file because it not exist");
            Path file = files[0].toPath();
            FileSystemUtils.deleteRecursively(file);
        } catch (IOException e) {
            logger.error("Could not remove file with UDID = " + fileUUID);
            throw new StorageException("Could not remove file. " + e.getMessage());
        }
    }
}
