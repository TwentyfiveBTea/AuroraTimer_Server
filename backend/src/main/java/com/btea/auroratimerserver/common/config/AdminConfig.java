package com.btea.auroratimerserver.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 21:41
 * @Description: 管理员配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "admin")
public class AdminConfig {

    /**
     * 管理员用户名
     */
    private String username;

    /**
     * 管理员密码
     */
    private String password;
}