package com.btea.auroratimerserver.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 13:45
 * @Description: JWT 配置类
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT签名密钥
     */
    private String secret;

    /**
     * Token过期时间（毫秒）
     */
    private Duration expiration;

    /**
     * 请求头名称
     */
    private String header;

    /**
     * Token前缀
     */
    private String prefix;

    /**
     * Token前缀长度
     */
    private Integer prefixLength;

    /**
     * 角色声明名称
     */
    private String roleClaim;

    /**
     * 默认角色
     */
    private String defaultRole;

    /**
     * 管理员角色
     */
    private String adminRole;
}
