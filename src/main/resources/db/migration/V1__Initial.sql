CREATE SEQUENCE IF NOT EXISTS image_sequence START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS image(taskid BIGINT PRIMARY KEY, uuid VARCHAR);