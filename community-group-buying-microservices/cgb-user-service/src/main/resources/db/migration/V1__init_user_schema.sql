-- =========================================================
-- 用户服务数据库初始化脚本
-- 数据库名: cgb_user
-- =========================================================

-- 用户表（买家）
CREATE TABLE IF NOT EXISTS `yonghu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `zhanghao` VARCHAR(50) NOT NULL COMMENT '账号（唯一）',
  `mima` VARCHAR(200) NOT NULL COMMENT '密码（BCrypt加密）',
  `xingming` VARCHAR(50) DEFAULT NULL COMMENT '姓名',
  `xingbie` VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `shouji` VARCHAR(20) DEFAULT NULL COMMENT '手机',
  `youxiang` VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
  `touxiang` VARCHAR(200) DEFAULT NULL COMMENT '头像',
  `jifen` DOUBLE DEFAULT 0 COMMENT '积分',
  `yue` DOUBLE DEFAULT 0 COMMENT '余额',
  `status` TINYINT DEFAULT 0 COMMENT '账号状态 0正常 1禁用',
  `addtime` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatetime` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isdelete` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_zhanghao` (`zhanghao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 管理员表
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
  `password` VARCHAR(200) NOT NULL COMMENT '密码（BCrypt加密）',
  `role` VARCHAR(20) DEFAULT 'admin' COMMENT '角色',
  `avatar` VARCHAR(200) DEFAULT NULL COMMENT '头像',
  `addtime` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatetime` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isdelete` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入默认管理员 (密码: abo → BCrypt 加密)
INSERT INTO `users` (`username`, `password`, `role`) VALUES
('abo', '$2a$10$N.zmdr9k7uOCQb376c3UVu9feXl0JHKsP6c4iE.uj4c8J8yMq3kL2', 'admin');