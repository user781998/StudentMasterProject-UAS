CREATE DATABASE IF NOT EXISTS student_master
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE student_master;

CREATE TABLE IF NOT EXISTS students (
                                        student_id INT NOT NULL,
                                        name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    photo_path VARCHAR(255) NOT NULL,
    background_color CHAR(7) NOT NULL DEFAULT '#EAF2FF',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id)
    );