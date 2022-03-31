package com.example.imagestorage.service;

import com.example.imagestorage.domain.Image;
import com.example.imagestorage.exception.NotFoundException;
import com.example.imagestorage.repository.ImageRepositoryImpl;
import org.apache.commons.io.FilenameUtils;
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
        return storageService.read(image.getUuid(), image.getFileExtension());
    }

    public void saveImage(Long taskId, MultipartFile imageFile) {
        Image oldImage = imageRepository.get(taskId);
        Image newImage;
        if (oldImage != null) {
            storageService.remove(oldImage.getUuid(), oldImage.getFileExtension());
            newImage = imageRepository.update(taskId, FilenameUtils.getExtension(imageFile.getOriginalFilename()));
        } else {
            newImage = imageRepository.insert(taskId, FilenameUtils.getExtension(imageFile.getOriginalFilename()));
        }
        storageService.store(newImage.getUuid(), newImage.getFileExtension(), imageFile);
    }

    public void deleteImage(Long taskId) {
        Image image = imageRepository.get(taskId);
        if (image == null)
            throw new NotFoundException();
        storageService.remove(image.getUuid(), image.getFileExtension());
        imageRepository.delete(taskId);
    }
}
