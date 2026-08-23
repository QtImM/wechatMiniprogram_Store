-- V20260823_02__mock_pay_refund_exceptions.sql
-- 为支付异常和退款异常工作台添加演示 mock 数据
-- 用于验收测试 T-03（支付异常）和 T-04（退款异常）

-- ============================================================
-- 1. 支付异常 (pay_exception) - 2 条
-- ============================================================

-- 异常 1: PENDING_TIMEOUT - 支付单超时，微信端状态仍为 NOTPAY
INSERT INTO pay_exception (pay_order_id, pay_sn, order_id, order_sn, user_id,
    reason_code, reason, wechat_trade_state, wechat_amount, channel_trade_no,
    local_status, order_pay_status, handled, handle_result, handle_remark, last_detect_time)
VALUES (820001, 'PDEMO202608060001', 810001, 'DEMO202608060001', 810001,
    'PENDING_TIMEOUT', '支付单创建超过30分钟仍未完成，微信端状态为NOTPAY',
    'NOTPAY', NULL, '', 0, 0, 0, '', '', NOW());

-- 异常 2: AMOUNT_MISMATCH - 微信已支付但金额与本地订单不一致
INSERT INTO pay_exception (pay_order_id, pay_sn, order_id, order_sn, user_id,
    reason_code, reason, wechat_trade_state, wechat_amount, channel_trade_no,
    local_status, order_pay_status, handled, handle_result, handle_remark, last_detect_time)
VALUES (820005, 'PDEMO202608060005', 810005, 'DEMO202608060005', 810005,
    'AMOUNT_MISMATCH', '微信已支付但金额与订单不一致，微信端199.00元 vs 本地59.80元',
    'SUCCESS', 19900, 'WX20260823000001', 1, 1, 0, '', '', NOW());

-- ============================================================
-- 2. 退款异常 (trade_after_sale, status=5 退款失败) - 2 条
-- ============================================================

-- 退款异常 1: REFUND_FAILED - 退款渠道返回失败
INSERT INTO trade_after_sale (order_id, user_id, after_sale_sn, type, status,
    refund_amount, reason, apply_remark, refund_provider, provider_refund_no,
    refund_message, refund_attempt_count, refund_last_attempt_time,
    refund_last_error, refund_channel_state, refund_exception_code,
    refund_exception_message, refund_handled, refund_handle_remark,
    apply_time, audit_time)
VALUES (810005, 810005, 'RMOCK20260823001', 1, 5, 5980,
    '商品质量问题', '收到的黑芝麻丸有异味',
    'WECHAT', 'REF-WX-20260823-001',
    '微信退款渠道返回失败', 3, '2026-08-23 10:30:00',
    'REFUND_FAIL: 商户退款单已关闭', 'FAIL', 'REFUND_FAILED',
    '退款渠道返回失败：商户退款单已关闭', 0, '',
    '2026-08-22 14:00:00', '2026-08-22 14:05:00');

-- 退款异常 2: REFUND_TIMEOUT - 退款处理超时，重试次数耗尽
INSERT INTO trade_after_sale (order_id, user_id, after_sale_sn, type, status,
    refund_amount, reason, apply_remark, refund_provider, provider_refund_no,
    refund_message, refund_attempt_count, refund_last_attempt_time,
    refund_next_attempt_time,
    refund_last_error, refund_channel_state, refund_exception_code,
    refund_exception_message, refund_handled, refund_handle_remark,
    apply_time, audit_time)
VALUES (810006, 810006, 'RMOCK20260823002', 1, 5, 2990,
    '物流破损', '收到的花茶包装破损严重',
    'WECHAT', 'REF-WX-20260823-002',
    '退款超时未到账', 12, '2026-08-23 08:15:00', NULL,
    'TIMEOUT: 超过最大重试次数', 'PROCESSING', 'REFUND_TIMEOUT',
    '退款处理超时，自动重试次数已耗尽', 0, '',
    '2026-08-21 09:30:00', '2026-08-21 09:35:00');
