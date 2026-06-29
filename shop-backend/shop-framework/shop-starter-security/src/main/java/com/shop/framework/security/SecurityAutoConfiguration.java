package com.shop.framework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.pojo.CommonResult;
import com.shop.common.exception.ErrorCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityAutoConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TokenService tokenService) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/app-api/member/auth/**").permitAll()
                .requestMatchers("/admin-api/system/auth/**").permitAll()
                .requestMatchers("/app-api/product/**").permitAll()
                .requestMatchers("/admin-api/product/**").permitAll()  // Demo阶段免登录
                .requestMatchers("/app-api/index/**").permitAll()      // 首页接口
                .requestMatchers("/app-api/auth/**").permitAll()       // 登录相关
                .requestMatchers("/app-api/catalog/**").permitAll()   // 分类
                .requestMatchers("/app-api/goods/**").permitAll()     // 商品统计
                .requestMatchers("/app-api/topic/**").permitAll()     // 专题
                .requestMatchers("/app-api/cart/**").permitAll()      // 购物车
                .requestMatchers("/app-api/user/**").permitAll()      // 用户
                .requestMatchers("/app-api/brand/**").permitAll()     // 品牌
                .requestMatchers("/app-api/comment/**").permitAll()   // 评论
                .requestMatchers("/app-api/collect/**").permitAll()   // 收藏
                .requestMatchers("/app-api/buy/**").permitAll()       // 购买
                // All other endpoints require auth
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                        new ObjectMapper().writeValueAsString(CommonResult.error(ErrorCode.UNAUTHORIZED))
                    );
                })
            )
            .addFilterBefore(new TokenAuthenticationFilter(tokenService),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
