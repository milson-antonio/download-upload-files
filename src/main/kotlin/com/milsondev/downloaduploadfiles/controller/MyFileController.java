package com.milsondev.downloaduploadfiles.controller;

import com.milsondev.downloaduploadfiles.api.Category;
import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import com.milsondev.downloaduploadfiles.exceptions.DuplicateFileException;
import com.milsondev.downloaduploadfiles.exceptions.FileSizeException;
import com.milsondev.downloaduploadfiles.exceptions.MaximumNumberOfFilesExceptions;
import com.milsondev.downloaduploadfiles.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class MyFileController {

    private final FileService fileService;

    @Autowired
    public MyFileController(final FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("fileList", fileService.getMyFileList());
        model.addAttribute("categories", fileService.getCategories());
        return "index";
    }

    @PostMapping("/upload")
    public ModelAndView uploadFile(@RequestParam("file") final MultipartFile file,
                                    @RequestParam("category") final String category)  {

        ModelAndView mv = new ModelAndView("components/upload-table-overlay");

        try {
            fileService.saveFile(file, Category.fromString(category));
            mv.addObject("fileList", fileService.getMyFileList());
            mv.addObject("categories", fileService.getCategories());
            mv.addObject("showAlert", true);
            mv.addObject("alertMessage", "File uploaded successfully...");
        } catch (FileSizeException | MaximumNumberOfFilesExceptions | DuplicateFileException e) {
            mv.addObject("fileList", fileService.getMyFileList());
            mv.addObject("categories", fileService.getCategories());
            mv.addObject("showAlert", true);
            mv.addObject("alertMessage", e.getMessage());
        } catch (IOException e) {
            mv.addObject("fileList", fileService.getMyFileList());
            mv.addObject("categories", fileService.getCategories());
            mv.addObject("showAlert", true);
            mv.addObject("alertMessage", "Error: when processing the file...");
        } catch (Exception e) {
            mv.addObject("fileList", fileService.getMyFileList());
            mv.addObject("categories", fileService.getCategories());
            mv.addObject("showAlert", true);
            mv.addObject("alertMessage", "Error: unknown when uploading the file...");
        }

        mv.addObject("serverResponse", true);

        return mv;
    }

    @GetMapping("/delete-confirmation/{id}")
    public String showDeleteModal(@PathVariable UUID id, Model model) {
        model.addAttribute("id", id);
        return "components/delete-modal :: deleteModalContent";
    }

    @DeleteMapping(value = "/delete/{id}")
    public ModelAndView deleteFile(@PathVariable final UUID id) throws IOException {
        ModelAndView mv = new ModelAndView("components/upload-table-overlay");
        fileService.deleteFile(id);
        mv.addObject("fileList", fileService.getMyFileList());
        mv.addObject("categories", fileService.getCategories());
        mv.addObject("alertMessage", "");
        mv.addObject("serverResponse", true);
        return mv;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable final UUID id) throws IOException {
        MyFile myFile = fileService.downloadFile(id);
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(myFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + myFile.getOriginalFilename()
                                + "\"")
                .body(new ByteArrayResource(myFile.getContent()));
    }

    @GetMapping("/edit/{id}")
    public String editFileForm(@PathVariable final UUID id, Model model) {
        MyFile myFile = fileService.getFileById(id);

        String originalFilename = myFile.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            int lastDotIndex = originalFilename.lastIndexOf('.');
            String filenameWithoutExtension = originalFilename.substring(0, lastDotIndex);
            myFile.setOriginalFilename(filenameWithoutExtension);
        }

        model.addAttribute("file", myFile);
        model.addAttribute("categories", fileService.getCategories());
        return "components/modal-content :: editFragment";
    }

    @PutMapping("/update/{id}")
    public ModelAndView updateFile(@PathVariable final UUID id,
                             @RequestParam final String name,
                             @RequestParam final String category) {
        fileService.updateFile(id, name, category);
        ModelAndView mv = new ModelAndView("components/upload-table-overlay");
        mv.addObject("fileList", fileService.getMyFileList());
        mv.addObject("categories", fileService.getCategories());
        mv.addObject("serverResponse", true);
        return mv;
    }
}