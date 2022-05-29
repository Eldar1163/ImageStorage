package com.example.imagestorage.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityConfig {
    private String username;
    private String password;
}
