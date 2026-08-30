-- V20260830_01__comment_pic_urls.sql
-- 为 product_comment 表添加图片字段，支持评论上传图片功能

ALTER TABLE product_comment
ADD COLUMN pic_urls VARCHAR(2000) DEFAULT NULL COMMENT '评论图片URL列表，逗号分隔' AFTER content;
