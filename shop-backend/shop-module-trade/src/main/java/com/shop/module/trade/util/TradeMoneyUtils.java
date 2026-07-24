package com.shop.module.trade.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class TradeMoneyUtils {

    private TradeMoneyUtils() {
    }

    public static String formatYuan(Integer cent) {
        int value = cent == null ? 0 : cent;
        return BigDecimal.valueOf(value)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    public static Integer parseCent(Object value) {
        if (value == null) {
            return 0;
        }
        return BigDecimal.valueOf(Double.parseDouble(value.toString()))
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
