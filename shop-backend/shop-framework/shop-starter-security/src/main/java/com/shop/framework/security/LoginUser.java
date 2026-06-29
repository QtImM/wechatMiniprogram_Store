package com.shop.framework.security;

import lombok.Data;

@Data
public class LoginUser {
    private Long userId;
    private Integer userType; // 1=会员 2=管理员
}
