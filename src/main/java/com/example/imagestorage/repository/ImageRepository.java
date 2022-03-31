package com.example.imagestorage.repository;

import com.example.imagestorage.domain.Image;

public interface ImageRepository {
    Image get(Long taskId);

    Image insert(Long taskId, String fileExtension);

    Image update(Long taskId, String fileExtension);

    void delete(Long taskId);
}

