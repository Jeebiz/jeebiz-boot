/* 
 * 权限核心表：
 * 2、用户信息表、用户详情表、角色信息表、用户-角色关系表、角色-权限关系表（角色-菜单-按钮）、
 */

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for SYS_AUTHZ_ROLE_LIST
-- ----------------------------
DROP TABLE IF EXISTS `SYS_AUTHZ_ROLE_LIST`;
CREATE TABLE `SYS_AUTHZ_ROLE_LIST` (
  `R_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `R_NAME` varchar(50) NOT NULL COMMENT '角色名称',
  `R_TYPE` int(1) NOT NULL COMMENT '角色类型（1:原生|2:继承|3:复制|4:自定义）',
  `R_INTRO` varchar(1000) NOT NULL COMMENT '角色简介',
  `R_STATUS` int(1) NOT NULL DEFAULT '1' COMMENT '角色状态（0:禁用|1:可用）',
  `R_TIME24` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '初始化时间',
  PRIMARY KEY (`R_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色信息表';

-- ----------------------------
-- Table structure for SYS_AUTHZ_ROLE_PERMITS
-- ----------------------------
DROP TABLE IF EXISTS `SYS_AUTHZ_ROLE_PERMS`;
CREATE TABLE `SYS_AUTHZ_ROLE_PERMS` (
  `R_ID` int(11) NOT NULL COMMENT '角色ID',
  `PERMS` varchar(50) NOT NULL COMMENT '权限标记：(等同SYS_FEATURE_OPTS.OPT_PERMS)',
  PRIMARY KEY (`R_ID`,`PERMS`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色-权限关系表（角色-菜单-按钮）';


-- ----------------------------
-- Table structure for SYS_AUTHZ_USER_LIST
-- ----------------------------
DROP TABLE IF EXISTS `SYS_AUTHZ_USER_LIST`;
CREATE TABLE `SYS_AUTHZ_USER_LIST` (
  `U_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `U_USERNAME` varchar(100) NOT NULL COMMENT '用户名',
  `U_PASSWORD` varchar(100) NOT NULL COMMENT '用户密码',
  `U_ALIAS` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `U_AVATAR` varchar(300) DEFAULT NULL COMMENT '用户头像：图片路径或图标样式',
  `U_SALT` varchar(64) DEFAULT NULL COMMENT '用户密码盐：用于密码加解密',
  `U_SECRET` varchar(128) DEFAULT NULL COMMENT '用户秘钥：用于用户JWT加解密',
  `U_PHONE` varchar(11) DEFAULT NULL COMMENT '手机号码',
  `U_EMAIL` varchar(100) DEFAULT NULL COMMENT '邮箱地址',
  `U_REMARK` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `U_STATUS` int(1) DEFAULT NULL COMMENT '用户状态（0:禁用|1:可用|2:锁定）',
  `U_TIME24` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '初始化时间',
  PRIMARY KEY (`U_ID`),
  UNIQUE KEY `U_USERNAME` (`U_USERNAME`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Table structure for SYS_AUTHZ_USER_DETAIL
-- ----------------------------
DROP TABLE IF EXISTS `SYS_AUTHZ_USER_DETAIL`;
CREATE TABLE `SYS_AUTHZ_USER_DETAIL` (
  `U_ID` int(11) NOT NULL COMMENT '用户ID',
  `D_ID` int(11) NOT NULL COMMENT '用户详情表ID',
  `D_BIRTHDAY` varchar(20) DEFAULT NULL COMMENT '出生日期',
  `D_GENDER` varchar(20) DEFAULT NULL COMMENT '性别：（male：男，female：女）',
  `D_IDCARD` varchar(20) DEFAULT NULL COMMENT '身份证号码',
  PRIMARY KEY (`D_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户详情表';

-- ----------------------------
-- Table structure for SYS_AUTHZ_USER_ROLE_RELATION
-- ----------------------------
DROP TABLE IF EXISTS `SYS_AUTHZ_USER_ROLE_RELATION`;
CREATE TABLE `SYS_AUTHZ_USER_ROLE_RELATION` (
  `U_ID` int(11) NOT NULL COMMENT '用户ID',
  `R_ID` int(11) NOT NULL COMMENT '角色ID',
  `R_PRTY` int(2) NOT NULL DEFAULT 0 COMMENT '优先级：用于默认登录角色',
  PRIMARY KEY (`U_ID`,`R_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户-角色关系表';
