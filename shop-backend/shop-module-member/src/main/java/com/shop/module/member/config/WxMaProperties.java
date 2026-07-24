package com.shop.module.member.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.ma")
public class WxMaProperties {

    /** 小程序 AppID */
    private String appid;

    /** 小程序 AppSecret */
    private String secret;

    /**
     * 是否启用 Mock 模式（true=跳过真实微信接口调用，使用 dev openid）
     * 开发阶段默认 true，联调/上线改为 false
     */
    private boolean mockEnabled = true;
}
