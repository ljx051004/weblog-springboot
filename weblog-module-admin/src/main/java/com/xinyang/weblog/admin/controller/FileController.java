package com.xinyang.weblog.admin.controller;

import com.xinyang.weblog.admin.service.FileService;
import com.xinyang.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
public class FileController {
    @Autowired
    private FileService fileService;
    public Response UploadFile(@RequestParam MultipartFile file){
        return fileService.uploadFile(file);
    }

}
