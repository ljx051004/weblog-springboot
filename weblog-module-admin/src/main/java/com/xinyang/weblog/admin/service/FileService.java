package com.xinyang.weblog.admin.service;

import com.xinyang.weblog.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    Response uploadFile(MultipartFile file);
}
