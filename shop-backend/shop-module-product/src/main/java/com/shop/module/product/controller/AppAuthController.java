package com.shop.module.product.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 小程序登录接口 - 开发阶段使用模拟数据
 */
@RestController
@RequestMapping("/app-api/auth")
public class AppAuthController {

    @RequestMapping("/code")
    public Map<String, Object> loginByCode(@RequestParam(value = "code", required = false) String code) {
        return mockLoginResponse();
    }

    @RequestMapping("/{code}")
    public Map<String, Object> loginByPathCode(@PathVariable String code) {
        return mockLoginResponse();
    }

    @RequestMapping("/LoginByMa")
    public Map<String, Object> loginByMa(@RequestBody(required = false) Map<String, Object> body) {
        return mockLoginResponse();
    }

    private Map<String, Object> mockLoginResponse() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userInfo", Map.of("nickName", "测试用户", "avatarUrl", ""));
        data.put("token", "dev-test-token");
        data.put("userId", 1);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", data);
        return result;
    }
}
