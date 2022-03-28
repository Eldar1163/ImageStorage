package com.example.imagestorage.controller;

import com.example.imagestorage.exception.BadImageException;
import com.example.imagestorage.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/image")
@Validated
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping()
    public ResponseEntity<Resource> read (
            @RequestParam(name = "taskid", required = false)
            @NotNull(message = "You must specify correct task id")
            @Min(value = 1L, message = "Task ID cannot be less than 1") Long taskId) {
        Resource image = imageService.getImage(taskId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFilename() + "\"")
                .body(image);
    }

    @PostMapping
    public void create(
            @RequestParam(value = "image", required = false)
            @NotNull(message = "Image is required")
                    MultipartFile imageFile,
            @RequestParam(name = "taskid", required = false)
            @NotNull(message = "You must specify correct task id")
            @Min(value = 1L, message = "Task ID cannot be less than 1")
                    Long taskId) {
        checkImage(imageFile);
        imageService.saveImage(taskId, imageFile);
    }

    @DeleteMapping
    public void delete(
            @RequestParam(name = "taskid", required = false)
            @NotNull(message = "You must specify correct task id")
            @Min(value = 1L, message = "Task ID cannot be less than 1") Long taskId) {
        imageService.deleteImage(taskId);
    }

    private void checkImage(MultipartFile file) {
        if (file.isEmpty())
            throw new BadImageException("Your file is empty");
        if (!isAllowedImage(file.getContentType()))
            throw new BadImageException("This file type is unsupported");
    }

    private boolean isAllowedImage(String contentType) {
        return isSupportedContentType(contentType);
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/bmp")
                || contentType.equals("image/gif")
                || contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}