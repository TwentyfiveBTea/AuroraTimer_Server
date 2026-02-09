package com.btea.auroratimerserver.common.interceptor;

import com.btea.auroratimerserver.common.context.UserContext;
import com.btea.auroratimerserver.common.convention.exception.ClientException;
import com.btea.auroratimerserver.common.enums.JwtRoleEnum;
import com.btea.auroratimerserver.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 14:27
 * @Description: 管理员认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    private static final String ADMIN_ROLE = "admin";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头提取 Token
        String authHeader = request.getHeader("Authorization");
        String token = jwtUtil.extractTokenFromHeader(authHeader);

        // 验证 Token 是否有效
        if (!jwtUtil.validateToken(token)) {
            throw new ClientException("未登录或登录已过期");
        }

        // 验证是否是管理员
        JwtRoleEnum role = jwtUtil.parseRole(token);
        if (!ADMIN_ROLE.equals(role)) {
            log.warn("非管理员尝试访问后台: 用户ID={}, 请求路径={}",
                    jwtUtil.parseUserId(token),
                    request.getRequestURI());
            throw new ClientException("需要管理员权限");
        }

        // 存入上下文
        Long userId = jwtUtil.parseUserId(token);
        UserContext.setCurrentUserId(userId);
        UserContext.setCurrentUserRole(role);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理上下文
        UserContext.clear();
    }
}
