package com.example.imagestorage.service;

import com.example.imagestorage.domain.Image;
import com.example.imagestorage.exception.DatabaseException;
import com.example.imagestorage.exception.NotFoundException;
import com.example.imagestorage.repository.ImageRepositoryImpl;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ImageService {
    private final ImageRepositoryImpl imageRepository;
    private final Storage storage;

    public ImageService(ImageRepositoryImpl imageRepository,
                        Storage storage) {
        this.imageRepository = imageRepository;
        this.storage = storage;
    }

    public Resource getImage(Long taskId) {
        Image image = imageRepository.get(taskId);
        if (image == null)
            throw new NotFoundException();
        return storage.read(image.getUuid());
    }

    public void saveImage(Long taskId, MultipartFile imageFile) {
        Image image = imageRepository.upcreate(taskId);
        if (image == null)
            throw new DatabaseException("Cannot add record to database");
        storage.store(image.getUuid(), imageFile);
    }

    public void deleteImage(Long taskId) {
        Image image = imageRepository.get(taskId);
        if (image == null)
            throw new NotFoundException();
        storage.remove(image.getUuid());
        imageRepository.delete(taskId);
    }
}
