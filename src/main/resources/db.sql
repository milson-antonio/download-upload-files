CREATE TABLE IF NOT EXISTS tb_my_file (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    upload_date TIMESTAMP,
    size VARCHAR(255),
    category VARCHAR(255),
    original_file_name VARCHAR(255),
    content_type VARCHAR(255),
    check_sum BIGINT,
    file_content_id UUID
);



CREATE TABLE IF NOT EXISTS tb_my_file_content (
    id UUID PRIMARY KEY,
    content OID,
    file_id UUID
);
