package com.milsondev.downloaduploadfiles.db.repository;

import com.milsondev.downloaduploadfiles.db.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepository extends JpaRepository<Student, Long> {
}
