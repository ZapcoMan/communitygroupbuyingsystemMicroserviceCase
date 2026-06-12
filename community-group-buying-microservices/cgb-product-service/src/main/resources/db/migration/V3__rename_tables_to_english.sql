-- =========================================================
-- V3: 重命名商品服务数据库表（拼音 → 英文）
-- 执行前请备份数据！
-- =========================================================

-- 1. 商品表
ALTER TABLE shangpin RENAME TO product;

-- 2. 商品收藏表
ALTER TABLE shangpin_collection RENAME TO product_collection;

-- 3. 商品评价表
ALTER TABLE shangpin_comment RENAME TO product_comment;

-- 4. 商品留言表 → 商品咨询表
ALTER TABLE shangpin_liuyan RENAME TO product_inquiry;

-- 5. 商品类型表 → 商品分类表
ALTER TABLE shangpinleixing RENAME TO product_category;
