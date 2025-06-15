CREATE SCHEMA IF NOT EXISTS download_upload_files;

CREATE TABLE IF NOT EXISTS download_upload_files.tb_my_file (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    upload_date TIMESTAMP,
    size VARCHAR(255),
    category VARCHAR(255),
    original_file_name VARCHAR(255),
    content_type VARCHAR(255),
    file_path VARCHAR(255),
    check_sum BIGINT
);