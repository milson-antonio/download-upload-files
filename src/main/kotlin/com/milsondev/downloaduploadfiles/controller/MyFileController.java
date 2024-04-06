package com.milsondev.downloaduploadfiles.controller;

import com.milsondev.downloaduploadfiles.api.Category;
import com.milsondev.downloaduploadfiles.db.entity.MyFile;
import com.milsondev.downloaduploadfiles.exceptions.DuplicateFileException;
import com.milsondev.downloaduploadfiles.exceptions.FileSizeException;
import com.milsondev.downloaduploadfiles.exceptions.MaximumNumberOfFilesExceptions;
import com.milsondev.downloaduploadfiles.service.MyFileService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class MyFileController {
    private final MyFileService myFileService;

    @Autowired
    public MyFileController(final MyFileService myFileService) {
        this.myFileService = myFileService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("fileList", myFileService.getMyFileList());
        model.addAttribute("categories", myFileService.getCategories());
        return "index";
    }

    @PostMapping("/upload")
    public ModelAndView uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("category") String category,
                             RedirectAttributes attributes)  {

        ModelAndView mv = new ModelAndView("components/file-table");

        try {
            myFileService.saveFile(file, Category.fromString(category));
            mv.addObject("fileList", myFileService.getMyFileList());
            mv.addObject("categories", myFileService.getCategories());
            mv.addObject("showAlert", false);
        } catch (FileSizeException | MaximumNumberOfFilesExceptions | DuplicateFileException e) {
            mv.addObject("fileList", myFileService.getMyFileList());
            mv.addObject("categories", myFileService.getCategories());
            mv.addObject("showAlert", true);
            mv.addObject("alertMessage", e.getMessage());
        } catch (IOException e) {
            mv.addObject("fileList", myFileService.getMyFileList());
            mv.addObject("categories", myFileService.getCategories());
            mv.addObject("showAlert", true);
            mv.addObject("alertMessage", "Error: when processing the file.");
        } catch (Exception e) {
            mv.addObject("fileList", myFileService.getMyFileList());
            mv.addObject("categories", myFileService.getCategories());
            mv.addObject("showAlert", true);
            mv.addObject("alertMessage", "Error: unknown when uploading the file.");
        }

        return mv;
    }


    @DeleteMapping(value = "/delete/{id}")
    public ModelAndView deleteFile(@PathVariable UUID id) {
        ModelAndView mv = new ModelAndView("components/file-table");
        myFileService.deleteFile(id);
        mv.addObject("fileList", myFileService.getMyFileList());
        mv.addObject("categories", myFileService.getCategories());
        mv.addObject("alertMessage", "");
        return mv;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable UUID id){
        MyFile myFile = myFileService.downloadFile(id);
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(myFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + myFile.getOriginalFilename()
                                + "\"")
                .body(new ByteArrayResource(myFile.getContent()));

    }
}
