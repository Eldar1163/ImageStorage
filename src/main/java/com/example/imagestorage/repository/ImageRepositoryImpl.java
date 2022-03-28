package com.example.imagestorage.repository;

import com.example.imagestorage.domain.Image;
import com.example.imagestorage.service.Storage;
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
            UUID.fromString(res.getString("uuid"))
    );

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger;
    private final Storage storage;

    public ImageRepositoryImpl(JdbcTemplate jdbcTemplate, Logger logger, Storage storage) {
        this.jdbcTemplate = jdbcTemplate;
        this.logger = logger;
        this.storage = storage;
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
    public Image upcreate(Long taskId) {
        UUID uuid = UUID.randomUUID();
        String query;
        Image curImage = get(taskId);
        if (curImage == null) {
            query = "INSERT INTO image(taskid, uuid) values(?, ?)";
            jdbcTemplate.update(
                    query,
                    taskId,
                    uuid.toString());
        } else {
            query = "UPDATE image SET uuid = ? WHERE taskid = ?";
            jdbcTemplate.update(
                    query,
                    uuid.toString(),
                    taskId);
            storage.remove(curImage.getUuid());
        }
        return get(taskId);
    }

    @Override
    public void delete(Long taskId) {
        jdbcTemplate.update(
                "DELETE FROM image WHERE taskid = ?",
                taskId);
    }
}
