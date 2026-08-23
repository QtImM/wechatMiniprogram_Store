-- V20260823_04__mock_member_feedback.sql
-- 为用户反馈工作台添加演示 mock 数据
-- 用于验收测试 M-02（用户反馈）

-- 反馈 1: 商品相关 - 待处理
INSERT INTO member_feedback (user_id, type, content, mobile, status, handle_remark, create_time)
VALUES (810001, 1, '九蒸九晒黑芝麻丸口感很好，但包装盒在运输过程中被压扁了，建议加强外包装硬度',
    '13800008101', 0, '', '2026-08-22 15:30:00');

-- 反馈 2: 产品建议 - 处理中
INSERT INTO member_feedback (user_id, type, content, mobile, status, handler_admin_id, handle_remark, handle_time, create_time)
VALUES (810002, 6, '希望能增加药膳食谱推荐功能，根据季节和体质推荐适合的滋补方案',
    '13800008102', 1, 1, '已提交产品团队评估，纳入下季度迭代计划', '2026-08-21 10:00:00', '2026-08-20 09:15:00');

-- 反馈 3: 客户服务 - 已完成
INSERT INTO member_feedback (user_id, type, content, mobile, status, handler_admin_id, handle_remark, handle_time, create_time)
VALUES (110101, 3, '咨询退款进度时客服响应很快，态度也很好，问题已解决',
    '13800011101', 2, 1, '已核实退款到账，用户确认收到', '2026-08-19 16:20:00', '2026-08-18 11:45:00');
