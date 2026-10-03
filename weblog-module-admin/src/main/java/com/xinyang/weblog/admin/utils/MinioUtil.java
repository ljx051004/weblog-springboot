package com.xinyang.weblog.admin.utils;

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
public class MinioUtil {
    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;

    /**
     * 上传文件
     */
    public String UpLoadFile(MultipartFile file) throws Exception {
        // 判断文件是否为空
        if(file == null || file.getSize() == 0){
            log.info("==> 上传文件异常：文件大小为空 ...");
            throw new RuntimeException("文件大小不能为空");
        }

        // 文件原始名称
        String originalFilename = file.getOriginalFilename();
        // 文件的 Content-type
        String contentType = file.getContentType();// 获取文件的类型

        // 生成存储对象的名称（将 UUID 字符串中的 - 替换成空字符串）
        // replace 方法用于将字符串中的指定字符替换为新的字符
        String key  = UUID.randomUUID().toString().replace("-", "");
        // 获取文件的后缀，如 jpg
        // suffix 后缀
        // lastIndexOf 方法用于获取指定字符在字符串中最后一次出现的位置
        // substring 截取当前位置到末尾
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 拼接上文件后缀，即为要存储的文件名 UUID+.jpg=objectName 然后存储到MinIO中
        String objectName = String.format("%s%s", key, suffix);
        log.info("==> 开始上传文件至 Minio, ObjectName: {}", objectName);

        // 上传文件至 Minio
        // file.getInputStream()文件的二进制流（文件的实际内容
        // file.getSize()文件的大小
        // -1 分片大小，-1 表示让 MinIO 自动决定分片大小
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())// 存到哪个桶 → "weblog"
                .object(objectName)// 存储对象的名称 → "uuid.jpg"
                .stream(file.getInputStream(),file.getSize(),-1)// 文件流
                .contentType(contentType)// 文件的类型 Content-type  → "image/jpeg"
                .build());

        // 返回文件的访问链接
        // 第1个 %s http://127.0.0.1:9000
        // 第2个 %s weblog
        // 第3个 %s a1b2c3d4.jpg
        // url = "http://127.0.0.1:9000/weblog/a1b2c3d4.jpg"
        // 前端用来访问/显示这张图片的地址。
        String url = String.format("%s/%s/%s", minioProperties.getEndpoint(), minioProperties.getBucketName(), objectName);
        log.info("==> 上传文件至 Minio 成功，访问路径: {}", url);
        return url;
    }
}
