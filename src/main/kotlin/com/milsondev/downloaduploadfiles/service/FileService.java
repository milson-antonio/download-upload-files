package com.milsondev.downloaduploadfiles.service;

import com.milsondev.downloaduploadfiles.api.Category;
import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import com.milsondev.downloaduploadfiles.db.repository.MyFileRepository;
import com.milsondev.downloaduploadfiles.exceptions.DuplicateFileException;
import com.milsondev.downloaduploadfiles.exceptions.FileSizeException;
import com.milsondev.downloaduploadfiles.exceptions.MaximumNumberOfFilesExceptions;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Service
public class FileService {

    private final MyFileRepository myFileRepository;
    private final StorageService storageService;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    @Value("${max.file.size:1048576}")
    private long MAX_FILE_SIZE;

    @Value("${max.number.of.files:10}")
    private long MAX_NUMBER_OF_FILES;

    @Autowired
    public FileService(final MyFileRepository myFileRepository,
                       final StorageService storageService) {
        this.myFileRepository = myFileRepository;
        this.storageService = storageService;
    }

    public void saveFile(final MultipartFile file, final Category category) throws IOException {
            if(getMyFileList().size() < MAX_NUMBER_OF_FILES  ){
                if(file.getSize() / (1024 * 1024) <= MAX_FILE_SIZE ){
                    if(!isDuplicateFile(file)){
                        String path = storageService.save(file);
                        MyFile myFile = convertMultipartFileToMyFile(file, category, path);
                        myFileRepository.save(myFile);
                        LOGGER.info("File {} uploaded successfully", myFile.getOriginalFilename());
                    } else {
                        throw new DuplicateFileException("Error: File "+ file.getOriginalFilename() +" already uploaded...");
                    }
                } else {
                    throw new FileSizeException("Error: The file you're trying to upload is too too large " + formatSize(file.getSize()) + "...");
                }
            } else {
                throw new MaximumNumberOfFilesExceptions("Error: You've reached the maximum number of allowed uploads. Please delete one to upload a new file...");
            }
    }

    public boolean isDuplicateFile(final MultipartFile file) throws IOException {
        long checksum = calculateChecksum(file.getBytes());
        String originalFilename = file.getOriginalFilename();
        String size = String.valueOf(file.getSize());
        return myFileRepository.existsByChecksumAndOriginalFilenameAndSize(checksum, originalFilename, size);
    }

    private long calculateChecksum(final byte[] bytes) {
        Checksum cRC32Checksum = new CRC32();
        cRC32Checksum.update(bytes, 0, bytes.length);
        return cRC32Checksum.getValue();
    }

    public MyFile convertMultipartFileToMyFile(final MultipartFile file,
                                               final Category category,
                                               final String path) throws IOException {
        MyFile myFile = new MyFile();
        myFile.setName(file.getName());
        myFile.setOriginalFilename(file.getOriginalFilename());
        myFile.setSize(String.valueOf(file.getSize()));
        myFile.setUploadDate(Instant.now());
        myFile.setContentType(file.getContentType());
        myFile.setCategory(category);
        myFile.setChecksum(calculateChecksum(file.getBytes()));
        myFile.setFilePath(path);
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

    private String formatSize(final long fileSize){
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

    @Transactional
    public void deleteFile(final UUID id) throws IOException {
        Optional<MyFile> myFileOptional = myFileRepository.findById(id);
        if(myFileOptional.isPresent()){
            storageService.delete(myFileOptional.get().getFilePath());
            myFileRepository.deleteById(id);
            LOGGER.info("File {} deleted successfully", myFileOptional.get().getOriginalFilename());
        }
    }

    @Transactional
    public MyFile downloadFile(final UUID id) throws IOException {
            Optional<MyFile> optionalMyFile = myFileRepository.findById(id);
            if(optionalMyFile.isPresent()){
                MyFile myFile = optionalMyFile.get();
                String path = myFile.getFilePath();
                byte[] content = storageService.load(path);
                myFile.setContent(content);
                return myFile;
            }
        return null;
    }

    @PostConstruct
    public void removeOrphanFiles() {
        try {
            Set<String> dbPaths = new HashSet<>();
            List<MyFile> allFiles = myFileRepository.findAll();

            for (MyFile file : allFiles) {
                dbPaths.add(Paths.get(file.getFilePath()).toAbsolutePath().toString());
            }

            File uploadDir = new File("uploads");
            if (!uploadDir.exists() || !uploadDir.isDirectory()) {
                return;
            }

            File[] physicalFiles = uploadDir.listFiles();
            if (physicalFiles == null) return;

            Set<String> physicalPaths = Arrays.stream(physicalFiles)
                    .filter(File::isFile)
                    .map(f -> f.getAbsolutePath())
                    .collect(Collectors.toSet());

            for (String physicalPath : physicalPaths) {
                if (!dbPaths.contains(physicalPath)) {
                    storageService.delete(physicalPath);
                    LOGGER.info("[CLEANUP] File deleted from the disc (does not exist in the bank): {}", physicalPath);
                }
            }

            for (MyFile file : allFiles) {
                String absolutePath = Paths.get(file.getFilePath()).toAbsolutePath().toString();
                if (!physicalPaths.contains(absolutePath)) {
                    myFileRepository.deleteById(file.getId());
                    LOGGER.info("[CLEANUP] Record removed from database (file does not exist on disc): {}", absolutePath);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error when cleaning up orphaned files: {}", e.getMessage());
            e.printStackTrace();
        }
    }

}