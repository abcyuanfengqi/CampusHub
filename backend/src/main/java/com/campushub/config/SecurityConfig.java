package com.campushub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * 当前阶段：放行所有请求（后续接入JWT过滤器后收紧权限）
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
                .requestMatchers("/auth/**", "/system/**").permitAll()
                // 放行Swagger文档
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 其他请求暂时全部放行（JWT过滤器接入后改为 authenticated()）
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
