package com.speakingpractice.partner.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Web安全配置
 * 禁用Spring Security的默认安全配置，只使用密码加密功能
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * 安全过滤链
     * 禁用所有安全检查，允许所有请求
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护
            .csrf(csrf -> csrf.disable())
            // 禁用表单登录
            .formLogin(formLogin -> formLogin.disable())
            // 禁用HTTP Basic认证
            .httpBasic(httpBasic -> httpBasic.disable())
            // 允许所有请求
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

        return http.build();
    }

}