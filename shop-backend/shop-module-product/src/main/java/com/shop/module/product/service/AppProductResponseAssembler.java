package com.shop.module.product.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class AppProductResponseAssembler {

    private AppProductResponseAssembler() {
    }

    static String formatPrice(Integer price) {
        if (price == null) {
            return "0.00";
        }
        return BigDecimal.valueOf(price, 2).setScale(2, RoundingMode.UNNECESSARY).toPlainString();
    }
}
