package com.xinyang.weblog.admin.controller;

import com.xinyang.weblog.admin.service.AdminFileService;
import com.xinyang.weblog.common.aspect.ApiOperationLog;
import com.xinyang.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 文件模块")
public class AdminFileController {
    @Autowired
    private AdminFileService fileService;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/file/upload")
    @ApiOperation(value = "文件上传")
    @ApiOperationLog(description = "文件上传")
    public Response UploadFile(@RequestParam MultipartFile file){
        return fileService.uploadFile(file);
    }
}
