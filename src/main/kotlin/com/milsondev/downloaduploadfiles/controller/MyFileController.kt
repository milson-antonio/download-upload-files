package com.milsondev.downloaduploadfiles.controller

import com.milsondev.downloaduploadfiles.service.MyFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping
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
class MyFileController @Autowired constructor(private val myFileService: MyFileService) {

    private val UPLOAD_DIR = "./uploads/"

    @GetMapping
    fun index(): String {
        return "index"
    }

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile, attributes: RedirectAttributes): String {
        if (file.isEmpty) {
            attributes.addFlashAttribute("message", "Por favor, selecione um arquivo para fazer upload.")
            return "redirect:/"
        }

        val fileName = StringUtils.cleanPath(file.originalFilename.toString())

        try {
            val path: Path = Paths.get(UPLOAD_DIR + fileName)
            Files.copy(file.inputStream, path, StandardCopyOption.REPLACE_EXISTING)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        attributes.addFlashAttribute("message", "Arquivo $fileName enviado com sucesso!")
        return "redirect:/"
    }





}
