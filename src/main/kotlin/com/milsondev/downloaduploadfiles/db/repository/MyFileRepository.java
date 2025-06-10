package com.milsondev.downloaduploadfiles.db.repository;

import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MyFileRepository extends JpaRepository<MyFile, UUID> {
    boolean existsByChecksumAndOriginalFilenameAndSize(Long checkSum, String originalFilename, String size);
}