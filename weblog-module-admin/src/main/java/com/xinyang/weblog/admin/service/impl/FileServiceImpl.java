package com.xinyang.weblog.admin.service.impl;

import com.xinyang.weblog.admin.model.vo.file.UploadFileRspVO;
import com.xinyang.weblog.admin.service.FileService;
import com.xinyang.weblog.admin.utils.MinioUtil;
import com.xinyang.weblog.admin.utils.MinioUtils;
import com.xinyang.weblog.common.enums.ResponseCodeEnum;
import com.xinyang.weblog.common.exception.BizException;
import com.xinyang.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    private MinioUtils minioUtils;
    @Override
    public Response uploadFile(MultipartFile file) {
        try {
            String url = minioUtils.uploadFile(file);
            // 返回给前端
            UploadFileRspVO uploadFileRspVO = UploadFileRspVO.builder().url(url).build();
            return Response.success(uploadFileRspVO);
        } catch (Exception e) {
            log.error("==> 上传文件至 Minio 错误: ", e);
            // 手动抛出业务异常，提示 “文件上传失败”
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }
}
