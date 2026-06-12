-- 商品类型表
CREATE TABLE IF NOT EXISTS `shangpinleixing` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `shangpinleixing` VARCHAR(100) NOT NULL COMMENT '商品类型名称',
  `addtime` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updatetime` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isdelete` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品类型表';

-- 初始数据
INSERT INTO `shangpinleixing` (`shangpinleixing`) VALUES ('水果'), ('蔬菜'), ('肉类'), ('海鲜'), ('粮油');
