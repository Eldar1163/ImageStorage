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
    private final StorageService storageService;

    public ImageService(ImageRepositoryImpl imageRepository,
                        StorageService storageService) {
        this.imageRepository = imageRepository;
        this.storageService = storageService;
    }

    public Resource getImage(Long taskId) {
        Image image = imageRepository.get(taskId);
        if (image == null)
            throw new NotFoundException();
        return storageService.read(image.getUuid());
    }

    public void saveImage(Long taskId, MultipartFile imageFile) {
        Image oldImage = imageRepository.get(taskId);
        Image newImage;
        if (oldImage != null) {
            storageService.remove(oldImage.getUuid());
            newImage = imageRepository.update(taskId);
        } else {
            newImage = imageRepository.insert(taskId);
        }
        if (newImage == null)
            throw new DatabaseException("Cannot add record to database");
        storageService.store(newImage.getUuid(), imageFile);
    }

    public void deleteImage(Long taskId) {
        Image image = imageRepository.get(taskId);
        if (image == null)
            throw new NotFoundException();
        storageService.remove(image.getUuid());
        imageRepository.delete(taskId);
    }
}
