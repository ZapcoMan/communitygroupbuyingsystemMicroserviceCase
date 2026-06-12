-- =========================================================
-- V3: 重命名团购服务数据库表（拼音 → 英文）
-- 执行前请备份数据！
-- =========================================================

-- 1. 团长表 → 团购槽位表（记录每个团购活动/槽位）
ALTER TABLE tuanwei RENAME TO group_slot;

-- 2. 参团记录表 → 团购信息表（记录每个用户的参团情况）
ALTER TABLE tuanxinxi RENAME TO group_info;

-- 3. 团购评论表
ALTER TABLE tuan_comment RENAME TO group_comment;
