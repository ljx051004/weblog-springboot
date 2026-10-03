package com.xinyang.weblog.admin.utils;

import com.mysql.cj.util.StringUtils;
import com.xinyang.weblog.admin.config.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@Slf4j
public class MinioUtils {
    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;

    public String uploadFile(MultipartFile file)throws Exception{
        // 先判断文件是否为空
        if(file == null || file.getSize() == 0){
            log.info("==> 上传文件异常：文件大小为空 ...");
            throw new RuntimeException("文件大小不能为空");
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();

        // 获取文件类型
        String contentType = file.getContentType();

        // 存储对象的名称 UUID.jpg
        String key = UUID.randomUUID().toString().replace("-", "");

        // 截取后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        // 组合成存储对象的名称
        String objectName = String.format("%s%s", key, suffix);
        log.info("==> 开始上传文件至 Minio, ObjectName: {}", objectName);

        // 上传到MinioClient
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .stream(file.getInputStream(),file.getSize(),-1)
                .contentType(contentType)
                .build());

        // url="http://127.0.0.1:9000/weblog/a1b2c3d4.jpg"
        String url=String.format("%s%s%s",minioProperties.getEndpoint(),minioProperties.getBucketName(),objectName);
        log.info("==> 文件上传成功，URL: {}", url);
        return url;
    }
}
