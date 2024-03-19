package com.milsondev.downloaduploadfiles.service;

import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import com.milsondev.downloaduploadfiles.db.repository.MyFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Service
public class MyFileService {
    private final MyFileRepository myFileRepository;

    @Autowired
    public MyFileService(final MyFileRepository myFileRepository) {
        this.myFileRepository = myFileRepository;
    }

    public void addFile(final MultipartFile file){
        myFileRepository.save(convertMultipartFileToMyFile(file));
    }


    private MyFile convertMultipartFileToMyFile(final MultipartFile file){
        MyFile myFile = new MyFile();
        myFile.setFileName(file.getOriginalFilename());
        myFile.setFileSize(String.valueOf(file.getSize()));
        myFile.setUploadDate(Instant.now());
        return myFile;
    }

    public List<MyFile> getMyFileList() {
        return myFileRepository.findAll();
    }

    public void deleteAll(){
        myFileRepository.deleteAll();
    }

}
