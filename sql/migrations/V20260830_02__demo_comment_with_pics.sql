-- V20260830_02__demo_comment_with_pics.sql
-- 为东阿阿胶糕(id=1)添加2条带图评论，用于演示"有图"筛选功能

INSERT INTO product_comment(user_id, order_id, spu_id, content, pic_urls, status)
VALUES
(240301, NULL, 1, '礼盒包装精美，切开能看到玫瑰花瓣和阿胶，口感软糯，送礼自用都合适。',
 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=400,https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400',
 1),
(810001, NULL, 1, '回购第三次了，品质稳定，每天吃两片气色好多了，推荐给朋友们。',
 'https://images.unsplash.com/photo-1571167530141-97b48e8e7d8d?w=400,https://images.unsplash.com/photo-1606312619070-d48b4c652a52?w=400,https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=400',
 1)
ON DUPLICATE KEY UPDATE pic_urls=VALUES(pic_urls);
