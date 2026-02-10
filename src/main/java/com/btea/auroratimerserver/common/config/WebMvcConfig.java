package com.btea.auroratimerserver.common.config;

import com.btea.auroratimerserver.common.interceptor.AdminAuthInterceptor;
import com.btea.auroratimerserver.common.interceptor.UserAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 14:25
 * @Description: 拦截器配置类
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    // 普通用户拦截器
    private final UserAuthInterceptor authInterceptor;
    // 管理员拦截器
    private final AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 管理员专用接口（需要 admin 权限）
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/auth/logout",
                        "/admin/notifications");

        // 普通用户接口（需要 user 权限）
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/register",
                        "/auth/reset-password",
                        "/admin/auth/login"
                ); // 无需任何权限
    }
}
