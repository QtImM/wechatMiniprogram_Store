package com.shop.common.exception;

import com.shop.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerException.class)
    public CommonResult<?> handleServerException(ServerException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<?> handleException(Exception e) {
        log.error("系统异常", e);
        return CommonResult.error(ErrorCode.INTERNAL_ERROR);
    }
}
