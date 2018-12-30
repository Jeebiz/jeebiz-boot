
/* 菜单核心表：功能菜单信息表、功能操作信息表、功能菜单-功能操作关系表*/

-- Create table
create table SYS_FEATURE_LIST (
	F_ID   		VARCHAR2(32) default sys_guid() not null,
  	F_NAME   	VARCHAR2(50) not null,
  	F_ABB 		VARCHAR2(50),
  	F_CODE 		VARCHAR2(50),
  	F_URL     	VARCHAR2(100),
  	F_TYPE  	VARCHAR2(1) default '0',
  	F_ICON   	VARCHAR2(200),
  	F_ORDER   	VARCHAR2(3),
  	F_PARENT   	VARCHAR2(32) not null,
  	F_VISIBLE	VARCHAR2(1) default '1',
  	CONSTRAINT PK_FID PRIMARY KEY(F_ID)
);
-- Add comments to the table 
comment on table SYS_FEATURE_LIST  is '功能菜单信息表';
-- Add comments to the columns 
comment on column SYS_FEATURE_LIST.F_ID  is '功能菜单ID';
comment on column SYS_FEATURE_LIST.F_NAME  is '功能菜单名称';
comment on column SYS_FEATURE_LIST.F_ABB  is '功能菜单简称';
comment on column SYS_FEATURE_LIST.F_CODE  is '功能菜单编码：用于与功能操作代码组合出权限标记以及作为前段判断的依据';
comment on column SYS_FEATURE_LIST.F_URL  is '功能菜单URL';
comment on column SYS_FEATURE_LIST.F_TYPE  is '菜单类型(1:原生|2:自定义)';
comment on column SYS_FEATURE_LIST.F_ICON  is '菜单样式或菜单图标路径'; 
comment on column SYS_FEATURE_LIST.F_ORDER  is '菜单显示顺序';
comment on column SYS_FEATURE_LIST.F_PARENT  is '父级功能菜单ID';
comment on column SYS_FEATURE_LIST.F_VISIBLE  is '菜单是否可见(1:可见|0:不可见)';

-- Create table
create table SYS_FEATURE_OPTS
(
	F_ID    		VARCHAR2(32) not null,
	OPT_ID   		VARCHAR2(32) default sys_guid() not null,
  	OPT_NAME 		VARCHAR2(30) not null,
  	OPT_ICON		VARCHAR2(60),
  	OPT_ORDER 		VARCHAR2(2),
  	OPT_VISIBLE 	VARCHAR2(1) default '0' not null,
  	OPT_PERMS 		VARCHAR2(50),
  	CONSTRAINT UNIQUE_FID_OPT_PERMS UNIQUE(F_ID, OPT_PERMS),
  	CONSTRAINT PK_OPT_ID PRIMARY KEY(OPT_ID)
);
-- Add comments to the table 
comment on table SYS_FEATURE_OPTS  is '功能操作信息表';
-- Add comments to the columns 
comment on column SYS_FEATURE_OPTS.F_ID  is '功能菜单ID';
comment on column SYS_FEATURE_OPTS.OPT_ID  is '功能操作信息表ID';
comment on column SYS_FEATURE_OPTS.OPT_NAME  is '功能操作名称';
comment on column SYS_FEATURE_OPTS.OPT_ICON  is '功能操作图标样式';
comment on column SYS_FEATURE_OPTS.OPT_ORDER is '显示顺序';
comment on column SYS_FEATURE_OPTS.OPT_VISIBLE  is '是否可见(1:可见|0:不可见)';
comment on column SYS_FEATURE_OPTS.OPT_PERMS is '权限标记';
