-- ============================================
-- 药食同源小程序商城 - 初始化SQL (Demo阶段)
-- ============================================

CREATE DATABASE IF NOT EXISTS `shop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `shop`;

-- ============ 会员相关 ============

CREATE TABLE `member_user` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `openid` varchar(64) DEFAULT NULL COMMENT '微信openid',
    `unionid` varchar(64) DEFAULT NULL COMMENT '微信unionid',
    `session_key` varchar(128) DEFAULT NULL COMMENT '微信session_key',
    `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
    `nickname` varchar(64) DEFAULT '' COMMENT '昵称',
    `avatar` varchar(512) DEFAULT '' COMMENT '头像URL',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 1=正常 0=禁用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` bit(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB COMMENT='会员用户表';

-- ============ 商品相关 ============

CREATE TABLE `product_category` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父分类ID(0=一级)',
    `name` varchar(64) NOT NULL COMMENT '分类名称',
    `icon` varchar(512) DEFAULT '' COMMENT '图标URL',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序(越大越前)',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 1=开启 0=关闭',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` bit(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='商品分类表';

CREATE TABLE `product_spu` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `name` varchar(128) NOT NULL COMMENT '商品名称',
    `keyword` varchar(256) DEFAULT '' COMMENT '关键词(搜索用)',
    `introduction` varchar(256) DEFAULT '' COMMENT '简介',
    `description` text COMMENT '详情(富文本)',
    `pic_url` varchar(512) NOT NULL COMMENT '主图URL',
    `slider_pic_urls` varchar(2048) DEFAULT '[]' COMMENT '轮播图JSON数组',
    `video_url` varchar(512) DEFAULT '' COMMENT '视频URL',
    `type` tinyint NOT NULL DEFAULT 1 COMMENT '类型 1=实物 2=虚拟(课程)',
    `price` int NOT NULL COMMENT '最低价格(分)',
    `market_price` int DEFAULT NULL COMMENT '划线价(分)',
    `stock` int NOT NULL DEFAULT 0 COMMENT '总库存',
    `sales_count` int NOT NULL DEFAULT 0 COMMENT '实际销量',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0=下架 1=上架',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` bit(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='商品SPU表';

CREATE TABLE `product_sku` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `spu_id` bigint NOT NULL COMMENT '商品SPU ID',
    `properties` varchar(512) DEFAULT '[]' COMMENT '属性JSON [{id,name,valueId,valueName}]',
    `price` int NOT NULL COMMENT '价格(分)',
    `market_price` int DEFAULT NULL COMMENT '划线价(分)',
    `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
    `pic_url` varchar(512) DEFAULT '' COMMENT 'SKU图片',
    `weight` double DEFAULT NULL COMMENT '重量(kg)',
    `volume` double DEFAULT NULL COMMENT '体积(m3)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` bit(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`),
    KEY `idx_spu_id` (`spu_id`)
) ENGINE=InnoDB COMMENT='商品SKU表';

-- ============ 系统相关 ============

CREATE TABLE `sys_admin_user` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(128) NOT NULL COMMENT '密码(BCrypt)',
    `nickname` varchar(64) DEFAULT '' COMMENT '昵称',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 1=正常 0=禁用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` bit(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB COMMENT='管理员用户表';

-- 插入默认管理员 (密码: admin123)
INSERT INTO `sys_admin_user` (`username`, `password`, `nickname`)
VALUES ('admin', '$2a$10$YMpimV4T/3Cq.UoFqMFJ6eOPHoGRTnr9X8tJLBXvBL7Uh3LQFX6G', '超级管理员');

-- 插入Demo分类
INSERT INTO `product_category` (`name`, `icon`, `sort`, `status`) VALUES
('农副产品', '', 100, 1),
('保健品', '', 90, 1),
('课程研学', '', 80, 1),
('组合套装', '', 70, 1);

-- ============ 内容相关 ============

CREATE TABLE `content_banner` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(128) NOT NULL COMMENT '标题',
    `pic_url` varchar(512) NOT NULL COMMENT '图片URL',
    `url` varchar(512) DEFAULT '' COMMENT '跳转链接',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 1=开启 0=关闭',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` bit(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='轮播图表';
