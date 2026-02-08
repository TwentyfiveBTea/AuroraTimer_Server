package com.btea.auroratimerserver.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/02/09 03:12
 * @Description: 阿里云配置类
 */
@Data
@Component
public class AliyunConfig {

    /**
     * 阿里云 accessKeyId
     */
    @Value("${aliyun.access-key-id}")
    private String accessKeyId;

    /**
     * 阿里云 accessKeySecret
     */
    @Value("${aliyun.access-key-secret}")
    private String accessKeySecret;

    /**
     * 阿里云云服务所在的数据中心地域
     */
    @Value("${aliyun.smsCode.region-id}")
    private String regionId;

    /**
     * endpoint
     */
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    /**
     * 阿里云 OSS 存储桶名称
     */
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;
}
