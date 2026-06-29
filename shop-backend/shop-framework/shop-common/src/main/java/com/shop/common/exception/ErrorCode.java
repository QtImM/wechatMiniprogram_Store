package com.shop.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(0, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "系统异常"),

    // Business errors 1001+
    USER_NOT_EXISTS(1001, "用户不存在"),
    TOKEN_EXPIRED(1002, "Token已过期"),
    PRODUCT_NOT_EXISTS(1101, "商品不存在"),
    PRODUCT_OFF_SHELF(1102, "商品已下架");

    private final Integer code;
    private final String msg;
}
