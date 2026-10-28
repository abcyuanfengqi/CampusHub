package com.campushub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * 认证与授权：JWT 拦截器（JwtAuthInterceptor）负责 Token 校验，
 * 本配置负责白名单放行与无状态会话（ADR-005）
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 关闭CSRF（前后端分离架构）
            .csrf(csrf -> csrf.disable())
            // 无状态会话（JWT认证）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 请求授权
            .authorizeHttpRequests(auth -> auth
                // 放行认证与健康检查接口
                .requestMatchers("/api/auth/**", "/api/system/**", "/auth/**", "/system/**").permitAll()
                // 放行Swagger文档
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 其余接口由 JwtAuthInterceptor 校验（放行后拦截器统一鉴权，避免默认401页面）
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
