/* 
 * 权限核心表：
 * 2、用户信息表、用户详情表、角色信息表、用户-角色关系表、角色-权限关系表（角色-菜单-按钮）、
 */

-- Create table
create table SYS_AUTHZ_ROLE_LIST (
  R_ID   		VARCHAR2(32) not null default sys_guid(),
  R_NAME   		VARCHAR2(50) not null,
  R_TYPE   		VARCHAR2(2) default 1,
  R_INTRO  		VARCHAR2(1000),
  R_STATUS		VARCHAR2(2) default 1,
  R_TIME24		VARCHAR2(32) default to_char(sysdate ,'yyyy-mm-dd hh24:mi:ss'),
  primary key (R_ID)
);
-- Add comments to the table 
comment on table SYS_AUTHZ_ROLE_LIST  is '角色信息表';
-- Add comments to the columns 
comment on column SYS_AUTHZ_ROLE_LIST.R_ID  is '角色ID';
comment on column SYS_AUTHZ_ROLE_LIST.R_NAME  is '角色名称';
comment on column SYS_AUTHZ_ROLE_LIST.R_TYPE  is '角色类型（1:原生|2:继承|3:复制|4:自定义）';
comment on column SYS_AUTHZ_ROLE_LIST.R_INTRO  is '角色简介';
comment on column SYS_AUTHZ_ROLE_LIST.R_STATUS  is '角色状态（0:禁用|1:可用）';
comment on column SYS_AUTHZ_ROLE_LIST.R_TIME24  is '初始化时间';

-- Create table
create table SYS_AUTHZ_ROLE_PERMS (
  R_ID   		VARCHAR2(32) not null,
  PERMS 		VARCHAR2(50) not null,
  primary key (R_ID, PERMS)
);
-- Add comments to the table 
comment on table SYS_AUTHZ_ROLE_PERMS  is '角色-权限关系表（角色-菜单-按钮）';
-- Add comments to the columns 
comment on column SYS_AUTHZ_ROLE_PERMS.R_ID  is '角色ID';
comment on column SYS_AUTHZ_ROLE_PERMS.PERMS  is '权限标记：(等同SYS_FEATURE_OPTS.OPT_PERMS)';

-- Create table
create table SYS_AUTHZ_USER_LIST (
  U_ID   			VARCHAR2(32) default sys_guid(),
  U_USERNAME 		VARCHAR2(100) not null,
  U_PASSWORD 		VARCHAR2(100) not null,
  U_ALIAS			VARCHAR2(50) not null,
  U_AVATAR			VARCHAR2(300),
  U_SALT			VARCHAR2(64),
  U_SECRET			VARCHAR2(128),
  U_PHONE			VARCHAR2(11),
  U_EMAIL			VARCHAR2(100),
  U_REMARK			VARCHAR2(255),
  U_STATUS			VARCHAR2(1),
  U_TIME24			VARCHAR2(32) default to_char(sysdate ,'yyyy-mm-dd hh24:mi:ss'),
  primary key (U_ID)
);
-- Add comments to the table 
comment on table SYS_AUTHZ_USER_LIST  is '用户信息表';
-- Add comments to the columns 
comment on column SYS_AUTHZ_USER_LIST.U_ID  is '用户ID';
comment on column SYS_AUTHZ_USER_LIST.U_USERNAME  is '用户名';
comment on column SYS_AUTHZ_USER_LIST.U_PASSWORD  is '用户密码';
comment on column SYS_AUTHZ_USER_LIST.U_ALIAS  is '用户昵称';
comment on column SYS_AUTHZ_USER_LIST.U_AVATAR  is '用户头像：图片路径或图标样式';
comment on column SYS_AUTHZ_USER_LIST.U_SALT  is '用户密码盐：用于密码加解密';
comment on column SYS_AUTHZ_USER_LIST.U_SECRET  is '用户秘钥：用于用户JWT加解密';
comment on column SYS_AUTHZ_USER_LIST.U_PHONE  is '手机号码';
comment on column SYS_AUTHZ_USER_LIST.U_EMAIL  is '邮箱地址';
comment on column SYS_AUTHZ_USER_LIST.U_REMARK  is '备注信息';
comment on column SYS_AUTHZ_USER_LIST.U_STATUS  is '用户状态（0:禁用|1:可用|2:锁定）';
comment on column SYS_AUTHZ_USER_LIST.U_TIME24  is '初始化时间';

-- Create table
create table SYS_AUTHZ_USER_DETAIL (
  U_ID   			VARCHAR2(32) not null,
  D_ID   			VARCHAR2(32) default sys_guid(),
  D_BIRTHDAY		VARCHAR2(20),
  D_GENDER			VARCHAR2(10),
  D_IDCARD			VARCHAR2(20),
  primary key (D_ID)
);
-- Add comments to the table 
comment on table SYS_AUTHZ_USER_DETAIL  is '用户详情表';
-- Add comments to the columns 
comment on column SYS_AUTHZ_USER_DETAIL.D_ID  is '用户详情表ID';
comment on column SYS_AUTHZ_USER_DETAIL.U_ID  is '用户ID';
comment on column SYS_AUTHZ_USER_DETAIL.D_BIRTHDAY  is '出生日期';
comment on column SYS_AUTHZ_USER_DETAIL.D_GENDER  is '性别：（male：男，female：女）';
comment on column SYS_AUTHZ_USER_DETAIL.D_IDCARD  is '身份证号码';

-- Create table
create table SYS_AUTHZ_USER_ROLE_RELATION (
  U_ID   			VARCHAR2(32) not null,
  R_ID   			VARCHAR2(32) not null,
  R_PRTY			VARCHAR2(2) not null,
  primary key (U_ID, R_ID)
);
-- Add comments to the table 
comment on table SYS_AUTHZ_USER_ROLE_RELATION  is '用户-角色关系表';
-- Add comments to the columns 
comment on column SYS_AUTHZ_USER_ROLE_RELATION.U_ID  is '用户ID';
comment on column SYS_AUTHZ_USER_ROLE_RELATION.R_ID  is '角色ID';
comment on column SYS_AUTHZ_USER_ROLE_RELATION.R_PRTY  is '优先级：用于默认登录角色';

