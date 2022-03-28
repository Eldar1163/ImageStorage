package com.example.imagestorage;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "image")
public class ConfigurationProperties {
    String path;
}
