package com.shop.common.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

    private final Integer code;

    public ServerException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public ServerException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
