package com.milsondev.downloaduploadfiles.controller;

import com.milsondev.downloaduploadfiles.service.MyFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/")
public class MyFileController {

    private final MyFileService myFileService;

    private final String UPLOAD_DIR = "./uploads/";

    @Autowired
    public MyFileController(final MyFileService myFileService) {
        this.myFileService = myFileService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("fileList", myFileService.getMyFileList());
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Por favor, selecione um arquivo para fazer upload.");
            return "redirect:/";
        }
        myFileService.addFile(file);
        attributes.addFlashAttribute("fileList", myFileService.getMyFileList());
        return "redirect:/";
    }
}
