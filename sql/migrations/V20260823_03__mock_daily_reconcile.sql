-- V20260823_03__mock_daily_reconcile.sql
-- 为日终对账工作台添加演示 mock 数据
-- 用于验收测试 T-05（日终对账）

-- ============================================================
-- 1. 对账批次 (trade_reconcile_batch) - 3 条
-- ============================================================

-- 批次 1: 2026-08-20 完全一致（平账）
INSERT INTO trade_reconcile_batch (reconcile_date, status, source,
    local_pay_count, local_pay_amount, local_refund_count, local_refund_amount, local_net_amount,
    wechat_pay_count, wechat_pay_amount, wechat_refund_count, wechat_refund_amount, wechat_net_amount,
    fee_amount, difference_count, trigger_type, trigger_admin_id, message, start_time, finish_time)
VALUES ('2026-08-20', 1, 'LOCAL_SNAPSHOT',
    5, 14950, 1, 2990, 11960,
    5, 14950, 1, 2990, 11960,
    0, 0, 'SCHEDULED', NULL,
    '本地与渠道快照汇总一致', '2026-08-21 02:30:00', '2026-08-21 02:30:05');

-- 批次 2: 2026-08-21 有金额差异
INSERT INTO trade_reconcile_batch (reconcile_date, status, source,
    local_pay_count, local_pay_amount, local_refund_count, local_refund_amount, local_net_amount,
    wechat_pay_count, wechat_pay_amount, wechat_refund_count, wechat_refund_amount, wechat_net_amount,
    fee_amount, difference_count, trigger_type, trigger_admin_id, message, start_time, finish_time)
VALUES ('2026-08-21', 1, 'LOCAL_SNAPSHOT',
    8, 23920, 2, 5980, 17940,
    8, 24020, 2, 5980, 18040,
    100, 1, 'SCHEDULED', NULL,
    '微信支付未启用，使用本地与已同步渠道状态生成对账结果', '2026-08-22 02:30:00', '2026-08-22 02:30:08');

-- 批次 3: 2026-08-22 有状态差异（手动触发）
INSERT INTO trade_reconcile_batch (reconcile_date, status, source,
    local_pay_count, local_pay_amount, local_refund_count, local_refund_amount, local_net_amount,
    wechat_pay_count, wechat_pay_amount, wechat_refund_count, wechat_refund_amount, wechat_net_amount,
    fee_amount, difference_count, trigger_type, trigger_admin_id, message, start_time, finish_time)
VALUES ('2026-08-22', 1, 'LOCAL_SNAPSHOT',
    6, 17940, 1, 2990, 14950,
    6, 17940, 1, 2990, 14950,
    0, 1, 'MANUAL', 1,
    '手动触发对账', '2026-08-23 10:15:00', '2026-08-23 10:15:12');

-- ============================================================
-- 2. 对账差异 (trade_reconcile_difference) - 2 条
-- ============================================================

-- 批次 2 差异: 支付金额与渠道快照不一致
INSERT INTO trade_reconcile_difference (batch_id, reconcile_date, diff_type, business_type,
    business_sn, order_sn, local_amount, channel_amount, local_status, channel_status, reason, handled)
VALUES (2, '2026-08-21', 'AMOUNT_MISMATCH', 'PAY',
    'PDEMO202608060003', 'DEMO202608060003', 5480, 5580, '1', 'SUCCESS',
    '支付金额与渠道快照不一致：本地54.80元，渠道55.80元', 0);

-- 批次 3 差异: 本地已支付但渠道状态未成功
INSERT INTO trade_reconcile_difference (batch_id, reconcile_date, diff_type, business_type,
    business_sn, order_sn, local_amount, channel_amount, local_status, channel_status, reason, handled)
VALUES (3, '2026-08-22', 'STATUS_MISMATCH', 'PAY',
    'PDEMO202608060001', 'DEMO202608060001', 2990, 2990, '1', 'NOTPAY',
    '本地已支付但渠道状态未成功', 0);
