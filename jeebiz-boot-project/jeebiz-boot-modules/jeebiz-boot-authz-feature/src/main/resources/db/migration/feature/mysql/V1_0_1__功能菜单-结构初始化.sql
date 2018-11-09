
/* 菜单核心表：功能菜单信息表、功能操作信息表、功能菜单-功能操作关系表*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for SYS_FEATURE_LIST
-- ----------------------------
DROP TABLE IF EXISTS `SYS_FEATURE_LIST`;
CREATE TABLE `SYS_FEATURE_LIST` (
  `F_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '功能菜单ID',
  `F_NAME` varchar(50) NOT NULL COMMENT '功能菜单名称',
  `F_ABB` varchar(50) DEFAULT NULL COMMENT '功能菜单简称',
  `F_CODE` varchar(50) NOT NULL COMMENT '功能菜单编码：用于与功能操作代码组合出权限标记以及作为前段判断的依据',
  `F_URL` varchar(255) NOT NULL DEFAULT '#' COMMENT '功能菜单URL',
  `F_TYPE` int(1) NOT NULL DEFAULT '1' COMMENT '菜单类型(1:原生|2:自定义)',
  `F_ICON` varchar(255) DEFAULT NULL COMMENT '菜单样式或菜单图标路径',
  `F_ORDER` int(3) NOT NULL DEFAULT '1' COMMENT '菜单显示顺序',
  `F_PARENT` int(11) NOT NULL COMMENT '父级功能菜单ID',
  `F_VISIBLE` int(1) NOT NULL DEFAULT '1' COMMENT '菜单是否可见(1:可见|0:不可见)',
  PRIMARY KEY (`F_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='功能菜单信息表';

-- ----------------------------
-- Table structure for SYS_FEATURE_OPTS
-- ----------------------------
DROP TABLE IF EXISTS `SYS_FEATURE_OPTS`;
CREATE TABLE `SYS_FEATURE_OPTS` (
  `F_ID` int(11) NOT NULL COMMENT '功能菜单ID',
  `OPT_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '功能操作信息表ID',
  `OPT_NAME` varchar(50) NOT NULL COMMENT '功能操作名称',
  `OPT_ICON` varchar(100) DEFAULT NULL COMMENT '功能操作图标样式',
  `OPT_ORDER` int(2) NOT NULL COMMENT '显示顺序',
  `OPT_VISIBLE` int(1) NOT NULL COMMENT '是否可见(1:可见|0:不可见)',
  `OPT_PERMS` varchar(50) NOT NULL COMMENT '权限标记',
  PRIMARY KEY (`OPT_ID`),
  UNIQUE KEY (`F_ID`,`OPT_PERMS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='功能操作信息表';
