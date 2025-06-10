package com.milsondev.downloaduploadfiles.db.entity;

import com.milsondev.downloaduploadfiles.api.Category;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_my_file", schema = "download_upload_files")
@Data
public class MyFile {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name="name")
    private String name;

    @Column(name="upload_date")
    private Instant uploadDate;

    @Column(name="size")
    private String size;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name="original_file_name")
    private String originalFilename;

    @Column(name="content_type")
    private String contentType;

    @Column(name ="check_sum")
    private Long checksum;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Transient
    private byte[] content;

}
