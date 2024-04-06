package com.milsondev.downloaduploadfiles.db.repository;

import com.milsondev.downloaduploadfiles.db.entity.FileContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileContentRepository extends JpaRepository<FileContent, UUID> {
    FileContent findByFileId(UUID fileId);
}
