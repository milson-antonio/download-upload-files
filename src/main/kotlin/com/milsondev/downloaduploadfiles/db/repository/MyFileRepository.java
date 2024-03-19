package com.milsondev.downloaduploadfiles.db.repository;

import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MyFileRepository extends JpaRepository<MyFile, UUID> {
}
