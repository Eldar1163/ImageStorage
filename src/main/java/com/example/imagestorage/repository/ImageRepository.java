package com.example.imagestorage.repository;

import com.example.imagestorage.domain.Image;

public interface ImageRepository {
    Image get(Long taskId);

    Image upcreate(Long taskId);

    void delete(Long taskId);
}

