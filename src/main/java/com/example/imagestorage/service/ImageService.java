package com.example.imagestorage.service;

import com.example.imagestorage.domain.Image;
import com.example.imagestorage.dto.ImageDto;
import com.example.imagestorage.exception.InternalException;
import com.example.imagestorage.exception.NotFoundException;
import com.example.imagestorage.repository.ImageRepositoryImpl;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Service
public class ImageService {
    private final ImageRepositoryImpl imageRepository;
    private final StorageService storageService;

    public ImageService(ImageRepositoryImpl imageRepository,
                        StorageService storageService) {
        this.imageRepository = imageRepository;
        this.storageService = storageService;
    }

    public List<ImageDto> getAllImagesByIds(List<Long> taskIds) {
        List<ImageDto> result = new ArrayList<>();
        for(Long taslId: taskIds)
            try {
                result.add(getImage(taslId));
            } catch (NotFoundException ignored) {

            }


        return result;
    }

    public ImageDto getImage(Long taskId) throws NotFoundException {
        Image image = imageRepository.get(taskId);
        if (image == null)
            throw new NotFoundException();

        Resource imageFile = storageService.read(image.getUuid(), image.getFileExtension());
        try {
            byte[] imageBytes = imageFile.getInputStream().readAllBytes();
            return new ImageDto(taskId, Base64.getEncoder().encodeToString(imageBytes));
        } catch (IOException exception) {
            throw new InternalException("Cannot encode image");
        }
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
