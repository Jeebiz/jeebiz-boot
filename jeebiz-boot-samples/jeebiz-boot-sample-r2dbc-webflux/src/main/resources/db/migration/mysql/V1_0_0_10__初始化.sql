SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for jeebiz_demo
-- ----------------------------
CREATE TABLE `jeebiz_demo` (
 `id` bigint(20) NOT NULL COMMENT 'ID',
 `name` varchar(50) NOT NULL COMMENT '名称',
 `intro` varchar(150) NOT NULL COMMENT '简述',
 `order_by` int(3) DEFAULT 999 COMMENT '显示顺序',
 `status` tinyint(2) DEFAULT 0 COMMENT '状态（0:禁用|1:可用）',
 `is_deleted` tinyint(2) NOT NULL DEFAULT 0 COMMENT '是否删除（0:未删除,1:已删除）',
 `creator` bigint(12) DEFAULT 0 COMMENT '创建人ID',
 `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `modifyer` bigint(12) DEFAULT NULL COMMENT '修改人ID',
 `modify_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
 PRIMARY KEY (`id`) USING BTREE,
 KEY `idx_full` (`id`,`is_deleted`) USING BTREE COMMENT 'Demo索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Demo示例表';