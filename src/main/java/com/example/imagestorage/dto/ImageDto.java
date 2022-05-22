package com.example.imagestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageDto {
    Long taskId;
    String imageBase64;
}
