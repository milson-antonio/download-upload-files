package com.milsondev.downloaduploadfiles.service;

import com.milsondev.downloaduploadfiles.api.Category;
import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import com.milsondev.downloaduploadfiles.db.repository.MyFileRepository;
import com.milsondev.downloaduploadfiles.exceptions.FileSizeException;
import com.milsondev.downloaduploadfiles.exceptions.MaximumNumberOfFilesExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyFileService {
    private final MyFileRepository myFileRepository;

    @Value("${max.file.size:1048576}")
    private long MAX_FILE_SIZE;

    @Value("${max.number.of.files:10}")
    private long MAX_NUMBER_OF_FILES;

    @Autowired
    public MyFileService(final MyFileRepository myFileRepository) {
        this.myFileRepository = myFileRepository;
    }

    public void saveFile(final MultipartFile file, Category category) throws IOException {
        if(getMyFileList().size() < MAX_NUMBER_OF_FILES ){
            if(file.getSize() <= MAX_FILE_SIZE ){
                MyFile myFile = convertMultipartFileToMyFile(file, category);
                myFileRepository.save(myFile);
            } else {
                throw new FileSizeException("Error: The file you're trying to upload is too too large " + formatSize(file.getSize()));
            }
        } else {
            throw new MaximumNumberOfFilesExceptions("Error: You've reached the maximum number of allowed uploads. Please delete one to upload a new file.");
        }
    }

    public MyFile convertMultipartFileToMyFile(final MultipartFile file, Category category) throws IOException {
        MyFile myFile = new MyFile();
        myFile.setName(file.getName());
        myFile.setOriginalFilename(file.getOriginalFilename());
        myFile.setSize(formatSize(file.getSize()));
        myFile.setUploadDate(Instant.now());
        myFile.setContent(file.getBytes());
        myFile.setContentType(file.getContentType());
        myFile.setCategory(category);
        return myFile;
    }

    public List<MyFile> getMyFileList() {
        return myFileRepository.findAll();
    }

    public void deleteAll(){
        myFileRepository.deleteAll();
    }

    public List<String> getCategories(){
        return Arrays.asList(Category.CV.toString(),
                Category.PASSAPORT.getDescription(),
                Category.LANGUAGE_CERTIFICATE.getDescription(),
                Category.OTHER.getDescription(),
                Category.LETTER_OF_MOTIVATION.getDescription(),
                Category.SCHOOL_CERTIFICATE.getDescription());
    }

    private String formatSize(long fileSize){
        String size;
        if (fileSize >= 1024 * 1024) {
            double sizeInMB = (double) fileSize / (1024 * 1024);
            size = new DecimalFormat("#.##").format(sizeInMB) + " MB";
        } else {
            double sizeInKB = (double) fileSize / 1024;
            size = new DecimalFormat("#").format(sizeInKB) + " KB";
        }
        return size;
    }

    public void deleteFile(UUID id) {
        myFileRepository.deleteById(id);
    }

    public MyFile downloadFile(UUID id) {
        Optional<MyFile> OptionalMyFile = myFileRepository.findById(id);
        return OptionalMyFile.orElse(null);
    }

}
