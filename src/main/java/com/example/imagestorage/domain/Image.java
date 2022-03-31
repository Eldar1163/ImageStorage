package com.example.imagestorage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Image {
    Long taskId;
    UUID uuid;
    String fileExtension;
}
