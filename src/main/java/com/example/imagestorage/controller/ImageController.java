package com.example.imagestorage.controller;

import com.example.imagestorage.dto.ImageDto;
import com.example.imagestorage.exception.BadImageException;
import com.example.imagestorage.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/image")
@Validated
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping()
    public ImageDto read (
            @RequestParam(name = "taskid", required = false)
            @NotNull(message = "You must specify correct task id")
            @Min(value = 1L, message = "Task ID cannot be less than 1") Long taskId) {
        return imageService.getImage(taskId);
    }

    @GetMapping(path = "/list")
    public List<ImageDto> readList(
            @RequestPart(name = "idlist")
            @NotNull(message = "You must specify correct task id list") List<Long> taskIds) {
        checkListOfId(taskIds);
        return imageService.getAllImagesByIds(taskIds);
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

    private void checkListOfId(List<Long> ids) {
        if (ids.isEmpty())
            throw new HttpClientErrorException(
                    "List of ids must not be empty",
                    HttpStatus.BAD_REQUEST,
                    "Error", null, null, null);
        for (Long id : ids)
            if (id < 1) {
                throw new HttpClientErrorException(
                        "List of ids must contains only positive ids",
                        HttpStatus.BAD_REQUEST,
                        "Error", null, null, null);
            }
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