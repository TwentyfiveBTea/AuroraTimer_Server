package com.btea.auroratimerserver.common.util;

import com.btea.auroratimerserver.common.config.JwtConfig;
import com.btea.auroratimerserver.common.convention.exception.ClientException;
import com.btea.auroratimerserver.common.enums.JwtRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 13:45
 * @Description: JWT 工具类
 */
@Slf4j
@Component
public class JwtUtil {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成普通用户 Token
     */
    public String generateUserToken(String userId) {
        return generateToken(userId, JwtRoleEnum.USER.getRole());
    }

    /**
     * 生成管理员 Token
     */
    public String generateAdminToken(String userId) {
        return generateToken(userId, JwtRoleEnum.ADMIN.getRole());
    }

    private String generateToken(String subject, String roleCode) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtConfig.getRoleClaim(), roleCode);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getExpiration().toMillis());

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    private JwtParser createParser() {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        try {
            return createParser()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new ClientException("Token已过期，请重新登录");
        } catch (SignatureException e) {
            log.warn("Token签名验证失败: {}", e.getMessage());
            throw new ClientException("Token无效");
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误: {}", e.getMessage());
            throw new ClientException("Token格式错误");
        } catch (IllegalArgumentException e) {
            log.warn("Token为空: {}", e.getMessage());
            throw new ClientException("Token不能为空");
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            throw new ClientException("Token解析失败");
        }
    }

    /**
     * 解析用户 ID
     */
    public Long parseUserId(String token) {
        Claims claims = parseToken(token);
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new ClientException("Token用户ID格式错误");
        }
    }

    /**
     * 解析角色
     */
    public JwtRoleEnum parseRole(String token) {
        Claims claims = parseToken(token);
        String roleCode = claims.get(jwtConfig.getRoleClaim(), String.class);

        if (roleCode == null) {
            return JwtRoleEnum.USER;
        }

        if (JwtRoleEnum.isAdmin(roleCode)) {
            return JwtRoleEnum.ADMIN;
        } else if (JwtRoleEnum.isUser(roleCode)) {
            return JwtRoleEnum.USER;
        } else {
            throw new ClientException("未知的角色类型");
        }
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证是否为指定角色
     */
    public boolean hasRole(String token, JwtRoleEnum role) {
        try {
            return parseRole(token) == role;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证是否为指定角色（字符串）
     */
    public boolean hasRole(String token, String roleCode) {
        try {
            JwtRoleEnum role = parseRole(token);
            return role.getRole().equalsIgnoreCase(roleCode);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从请求头提取 Token
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(jwtConfig.getPrefix())) {
            throw new ClientException("Authorization请求头格式错误，应为: " + jwtConfig.getPrefix() + " xxx");
        }
        return authHeader.substring(jwtConfig.getPrefixLength());
    }

    /**
     * 刷新 Token
     */
    public String refreshToken(String oldToken) {
        Claims claims = parseToken(oldToken);

        long remainingTime = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (remainingTime > 3600000) {
            return oldToken;
        }

        String roleCode = claims.get(jwtConfig.getRoleClaim(), String.class);
        String subject = claims.getSubject();

        @SuppressWarnings("unchecked")
        Map<String, Object> claimsMap = new HashMap<>(claims);
        claimsMap.remove("exp");

        return generateToken(subject, roleCode);
    }
}
