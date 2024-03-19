package com.milsondev.downloaduploadfiles.db.entity;

//import jakarta.persistence.*;
//import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

//@Entity
//@Table(name = "tb_my_file")
public class MyFile {


    //@Id
    //@GeneratedValue(generator = "uuid2")
    //@GenericGenerator(name = "uuid2", strategy = "uuid2")
    //@Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    //@Column(name="file_name")
    private String fileName;

}
