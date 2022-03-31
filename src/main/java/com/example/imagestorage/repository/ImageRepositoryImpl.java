package com.example.imagestorage.repository;

import com.example.imagestorage.domain.Image;
import org.slf4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.UUID;

@Component
public class ImageRepositoryImpl implements ImageRepository {
    RowMapper<Image> MAPPER = (ResultSet res, int rowNum) -> new Image(
            res.getLong("taskid"),
            UUID.fromString(res.getString("uuid")),
            res.getString("fileextension")
    );

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger;

    public ImageRepositoryImpl(JdbcTemplate jdbcTemplate, Logger logger) {
        this.jdbcTemplate = jdbcTemplate;
        this.logger = logger;
    }


    @Override
    public Image get(Long taskId) {
        Image image = null;
        try {
            image = jdbcTemplate.queryForObject("SELECT * FROM image WHERE taskid = ?", MAPPER, taskId);
        } catch (EmptyResultDataAccessException exception) {
            logger.warn("Image for taskid = " + taskId + " not exists");
        }
        return image;
    }

    @Override
    public Image insert(Long taskId, String fileExtension) {
        UUID uuid = UUID.randomUUID();
        String query = "INSERT INTO image(uuid, taskid, fileextension) values(?, ?, ?)";
        jdbcTemplate.update(
                query,
                uuid.toString(),
                taskId,
                fileExtension);
        return new Image(taskId, uuid, fileExtension);
    }

    @Override
    public Image update(Long taskId, String fileExtension) {
        UUID uuid = UUID.randomUUID();
        String query = "UPDATE image SET uuid = ?, fileextension = ? WHERE taskid = ?";
        jdbcTemplate.update(
                query,
                uuid.toString(),
                fileExtension,
                taskId);
        return new Image(taskId, uuid, fileExtension);
    }

    @Override
    public void delete(Long taskId) {
        jdbcTemplate.update(
                "DELETE FROM image WHERE taskid = ?",
                taskId);
    }
}
