package com.shop.module.product.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppProductResponseAssemblerTest {

    @Test
    void shouldFormatFenAsTwoDecimalYuan() {
        assertEquals("29.90", AppProductResponseAssembler.formatPrice(2990));
        assertEquals("0.00", AppProductResponseAssembler.formatPrice(null));
    }
}
