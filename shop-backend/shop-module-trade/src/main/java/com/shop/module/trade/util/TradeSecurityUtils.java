package com.shop.module.trade.util;

import com.shop.common.exception.ServerException;
import com.shop.framework.security.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class TradeSecurityUtils {

    private TradeSecurityUtils() {
    }

    public static Long getRequiredUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser.getUserId();
        }
        throw new ServerException(401, "请先登录");
    }
}
