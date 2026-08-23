-- V20260823_01__fix_double_encoded_utf8_demo_data.sql
-- 修复 ID >= 240100 演示种子数据的 UTF-8 双重编码问题
-- 原因：种子数据写入时 UTF-8 字节被当作 Latin-1 再转存为 UTF-8，导致中文显示为乱码
-- 方法：CONVERT(CONVERT(CONVERT(col USING latin1) USING binary) USING utf8mb4) 逆向还原

-- ============================================================
-- 1. product_spu
-- ============================================================
UPDATE product_spu
SET name        = CONVERT(CONVERT(CONVERT(name USING latin1) USING binary) USING utf8mb4),
    keyword     = CONVERT(CONVERT(CONVERT(keyword USING latin1) USING binary) USING utf8mb4),
    introduction = CONVERT(CONVERT(CONVERT(introduction USING latin1) USING binary) USING utf8mb4)
WHERE id BETWEEN 240001 AND 240099
  AND deleted = 0;

-- ============================================================
-- 2. product_category
-- ============================================================
UPDATE product_category
SET name = CONVERT(CONVERT(CONVERT(name USING latin1) USING binary) USING utf8mb4)
WHERE id BETWEEN 240100 AND 240199
  AND deleted = 0;

-- ============================================================
-- 3. content_banner
-- ============================================================
UPDATE content_banner
SET title = CONVERT(CONVERT(CONVERT(title USING latin1) USING binary) USING utf8mb4)
WHERE id BETWEEN 240200 AND 240299
  AND deleted = 0;

-- ============================================================
-- 4. content_channel
-- ============================================================
UPDATE content_channel
SET name = CONVERT(CONVERT(CONVERT(name USING latin1) USING binary) USING utf8mb4)
WHERE id BETWEEN 240200 AND 240299
  AND deleted = 0;

-- ============================================================
-- 5. content_brand
-- ============================================================
UPDATE content_brand
SET name = CONVERT(CONVERT(CONVERT(name USING latin1) USING binary) USING utf8mb4)
WHERE id BETWEEN 240200 AND 240299
  AND deleted = 0;

-- ============================================================
-- 6. content_topic
-- ============================================================
UPDATE content_topic
SET title           = CONVERT(CONVERT(CONVERT(title USING latin1) USING binary) USING utf8mb4),
    subtitle        = CONVERT(CONVERT(CONVERT(subtitle USING latin1) USING binary) USING utf8mb4),
    price_info      = CONVERT(CONVERT(CONVERT(price_info USING latin1) USING binary) USING utf8mb4)
WHERE id BETWEEN 240200 AND 240299
  AND deleted = 0;

-- ============================================================
-- 7. product_comment
-- ============================================================
UPDATE product_comment
SET content = CONVERT(CONVERT(CONVERT(content USING latin1) USING binary) USING utf8mb4)
WHERE id BETWEEN 240400 AND 240499
  AND deleted = 0;

-- ============================================================
-- 8. product_sku
-- ============================================================
UPDATE product_sku
SET properties = CONVERT(CONVERT(CONVERT(properties USING latin1) USING binary) USING utf8mb4)
WHERE id BETWEEN 240000 AND 240099
  AND deleted = 0;

-- ============================================================
-- 9. member_user (仅 240301，240302 是正确的)
-- ============================================================
UPDATE member_user
SET nickname = CONVERT(CONVERT(CONVERT(nickname USING latin1) USING binary) USING utf8mb4)
WHERE id = 240301
  AND deleted = 0;

-- ============================================================
-- 10. content_channel 240211-240213 补充修复
--     CONVERT 方式对部分记录无效，改用直接赋值
-- ============================================================
UPDATE content_channel SET name = '演示新品' WHERE id = 240211 AND deleted = 0;
UPDATE content_channel SET name = '演示热销' WHERE id = 240212 AND deleted = 0;
UPDATE content_channel SET name = '药食分类' WHERE id = 240213 AND deleted = 0;

-- ============================================================
-- 11. content_topic price_info 240231-240232 补充修复
--     CONVERT 方式对部分记录无效，改用直接赋值
-- ============================================================
UPDATE content_topic SET price_info = '39元起' WHERE id = 240231 AND deleted = 0;
UPDATE content_topic SET price_info = '46元起' WHERE id = 240232 AND deleted = 0;

-- ============================================================
-- 12. sys_role 角色名称修复（ID 2/3/4）
-- ============================================================
UPDATE sys_role SET name = '商品运营' WHERE id = 2 AND deleted = b'0';
UPDATE sys_role SET name = '订单客服' WHERE id = 3 AND deleted = b'0';
UPDATE sys_role SET name = '售后审核' WHERE id = 4 AND deleted = b'0';

-- ============================================================
-- 13. sys_permission 权限名称修复（ID 8-20）
-- ============================================================
UPDATE sys_permission SET name = '管理用户反馈' WHERE id = 8 AND deleted = b'0';
UPDATE sys_permission SET name = '管理管理员账号' WHERE id = 9 AND deleted = b'0';
UPDATE sys_permission SET name = '管理角色权限清单' WHERE id = 10 AND deleted = b'0';
UPDATE sys_permission SET name = '管理权限清单' WHERE id = 11 AND deleted = b'0';
UPDATE sys_permission SET name = '查看审计日志' WHERE id = 12 AND deleted = b'0';
UPDATE sys_permission SET name = '修改管理员密码' WHERE id = 13 AND deleted = b'0';
UPDATE sys_permission SET name = '查看订单列表' WHERE id = 14 AND deleted = b'0';
UPDATE sys_permission SET name = '查看订单详情' WHERE id = 15 AND deleted = b'0';
UPDATE sys_permission SET name = '订单发货' WHERE id = 16 AND deleted = b'0';
UPDATE sys_permission SET name = '查看订单物流' WHERE id = 17 AND deleted = b'0';
UPDATE sys_permission SET name = '查看售后订单' WHERE id = 18 AND deleted = b'0';
UPDATE sys_permission SET name = '处理售后订单' WHERE id = 19 AND deleted = b'0';
UPDATE sys_permission SET name = '管理素材库' WHERE id = 20 AND deleted = b'0';
