package com.milsondev.downloaduploadfiles.service;

import com.milsondev.downloaduploadfiles.api.Category;
import com.milsondev.downloaduploadfiles.db.entity.FileContent;
import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import com.milsondev.downloaduploadfiles.db.repository.FileContentRepository;
import com.milsondev.downloaduploadfiles.db.repository.MyFileRepository;
import com.milsondev.downloaduploadfiles.exceptions.DuplicateFileException;
import com.milsondev.downloaduploadfiles.exceptions.FileSizeException;
import com.milsondev.downloaduploadfiles.exceptions.MaximumNumberOfFilesExceptions;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Service
public class MyFileService {
    private final MyFileRepository myFileRepository;

    private final FileContentRepository fileContentRepository;

    @Value("${max.file.size:1048576}")
    private long MAX_FILE_SIZE;

    @Value("${max.number.of.files:10}")
    private long MAX_NUMBER_OF_FILES;

    @Value("${delete.and.add.file:false}")
    private boolean DELETE_AND_ADD_FILE;

    @Autowired
    public MyFileService(final MyFileRepository myFileRepository,
                         final FileContentRepository fileContentRepository) {
        this.myFileRepository = myFileRepository;
        this.fileContentRepository = fileContentRepository;
    }

    public void saveFile(final MultipartFile file, Category category) throws IOException {
        if(DELETE_AND_ADD_FILE){
            if(getMyFileList().size() < MAX_NUMBER_OF_FILES  ){
                if(file.getSize() / (1024 * 1024) <= MAX_FILE_SIZE ){
                    if(!isDuplicateFile(file)){
                        MyFile myFile = convertMultipartFileToMyFile(file, category);
                        UUID myFileId = myFileRepository.save(myFile).getId();
                        fileContentRepository.save(new FileContent(file.getBytes(), myFileId));
                    } else {
                        throw new DuplicateFileException("Error: File "+ file.getOriginalFilename() +" already uploaded!");
                    }
                } else {
                    throw new FileSizeException("Error: The file you're trying to upload is too too large " + formatSize(file.getSize()) + "!");
                }
            } else {
                throw new MaximumNumberOfFilesExceptions("Error: You've reached the maximum number of allowed uploads. Please delete one to upload a new file!");
            }
        }
    }

    private boolean isDuplicateFile(MultipartFile file) throws IOException {
        Long checksum = calculateChecksum(file.getBytes());
        String originalFilename = file.getOriginalFilename();
        String size = formatSize(file.getSize());
        List<MyFile> arquivos = myFileRepository.findByChecksumAndOriginalFilenameAndSize(checksum, originalFilename, size);
        return !arquivos.isEmpty();
    }

    private long calculateChecksum(final byte[] bytes) {
        Checksum cRC32Checksum = new CRC32();
        cRC32Checksum.update(bytes, 0, bytes.length);
        return cRC32Checksum.getValue();
    }

    public MyFile convertMultipartFileToMyFile(final MultipartFile file, Category category) throws IOException {
        MyFile myFile = new MyFile();
        myFile.setName(file.getName());
        myFile.setOriginalFilename(file.getOriginalFilename());
        myFile.setSize(formatSize(file.getSize()));
        myFile.setUploadDate(Instant.now());
        myFile.setContentType(file.getContentType());
        myFile.setCategory(category);
        myFile.setChecksum(calculateChecksum(file.getBytes()));
        return myFile;
    }

    public List<MyFile> getMyFileList() {
        return myFileRepository.findAll();
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
        if(DELETE_AND_ADD_FILE){
            myFileRepository.deleteById(id);
        }
    }

    @Transactional
    public MyFile downloadFile(UUID id) {
        if(DELETE_AND_ADD_FILE){
            Optional<MyFile> optionalMyFile = myFileRepository.findById(id);
            if(optionalMyFile.isPresent()){
                MyFile myFile = optionalMyFile.get();
                FileContent fileContent = fileContentRepository.findByFileId(id);
                myFile.setContent(fileContent.getContent());
                return myFile;
            }
        }

        return null;
    }
}
