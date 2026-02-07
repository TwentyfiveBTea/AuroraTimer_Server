package com.btea.auroratimerserver.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/02/08 03:15
 * @Description: 阿里云 OSS 工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunOssUtil {

    private final AliyunConfig aliyunConfig;

    /**
     * 创建阿里云客户端
     *
     * @return 阿里云客户端
     */
    public OSS createClient() {
        return new OSSClientBuilder().build(aliyunConfig.getEndpoint(), aliyunConfig.getAccessKeyId(), aliyunConfig.getAccessKeySecret());
    }

    /**
     * 上传文件到阿里云OSS
     *
     * @param file     要上传的文件
     * @param filePath 文件在OSS中的存储路径
     * @return 文件访问URL，如果上传失败则返回null
     */
    public String uploadFile(MultipartFile file, String filePath) {
        OSS ossClient = createClient();
        try {
            log.info("开始上传文件到OSS，Bucket: {}, 文件路径: {}", aliyunConfig.getBucketName(), filePath);

            // 获取文件输入流
            InputStream inputStream = file.getInputStream();
            log.info("文件名: {}, 文件大小: {} bytes", file.getOriginalFilename(), file.getSize());

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(aliyunConfig.getBucketName(), filePath, inputStream);
            log.info("创建上传请求完成");

            // 上传文件
            ossClient.putObject(putObjectRequest);
            log.info("文件上传到OSS完成");

            // 构造文件访问URL
            String fileUrl = "https://" + aliyunConfig.getBucketName() + "." + aliyunConfig.getEndpoint() + "/" + filePath;

            log.info("文件上传成功，文件访问URL: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("文件上传失败，Bucket: {}, 文件路径: {}", aliyunConfig.getBucketName(), filePath, e);
            return null;
        } finally {
            // 关闭客户端
            if (ossClient != null) {
                ossClient.shutdown();
                log.info("OSS客户端已关闭");
            }
        }
    }

    /**
     * 上传用户头像
     *
     * @param file   要上传的文件
     * @param userId 用户ID
     * @return 文件访问URL，如果上传失败则返回null
     */
    public String uploadAvatar(MultipartFile file, String userId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = ".jpg"; // 默认jpg格式
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filePath = "avatar/" + userId + fileExtension;
            return uploadFile(file, filePath);
        } catch (Exception e) {
            log.error("上传用户头像失败", e);
            return null;
        }
    }

    /**
     * 上传特定类型的文件
     *
     * @param file 要上传的文件
     * @param type 文件类型（如avatar、document等）
     * @param id   关联的ID（如用户ID、文档ID等）
     * @return 文件访问URL，如果上传失败则返回null
     */
    public String uploadFileWithType(MultipartFile file, String type, String id) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filePath = type + "/" + id + fileExtension;
            return uploadFile(file, filePath);
        } catch (Exception e) {
            log.error("上传文件失败，类型: {}, ID: {}", type, id, e);
            return null;
        }
    }
}
