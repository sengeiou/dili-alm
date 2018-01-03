/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/10/18 星期三 16:06:55                      */
/*==============================================================*/


drop table if exists data_auth;

drop table if exists data_dictionary;

drop table if exists data_dictionary_value;

drop table if exists department;

drop table if exists files;

drop table if exists member;

drop table if exists menu;

drop table if exists milestones;

drop table if exists project;

drop table if exists resource;

drop table if exists role;

drop table if exists role_data_auth;

drop table if exists role_menu;

drop table if exists role_resource;

drop table if exists schedule_job;

drop table if exists system_config;

drop table if exists team;

drop table if exists user;

drop table if exists user_data_auth;

drop table if exists user_department;

drop table if exists user_role;

/*==============================================================*/
/* Table: data_auth                                             */
/*==============================================================*/
create table data_auth
(
   id                   bigint(20) not null,
   type                 varchar(20) not null comment '数据权限分类##这个由具体的业务系统确定',
   data_id              varchar(50) not null comment '数据权限ID##业务编号',
   name                 varchar(50) not null comment '名称',
   parent_data_id       bigint(20) not null default 0 comment '父级数据ID##外键，关联父级数据权限',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=34768 DEFAULT CHARSET=utf8;

alter table data_auth comment '数据权限';

/*==============================================================*/
/* Table: data_dictionary                                       */
/*==============================================================*/
create table data_dictionary
(
   id                   bigint not null auto_increment,
   code                 varchar(50),
   name                 varchar(50),
   notes                varchar(255),
   created              timestamp default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   yn                   integer(4) comment '是否可用##{provider:"YnProvider"}',
   primary key (id)
);

alter table data_dictionary comment '数据字典
例:
系统图片:IMAGE_CODE';

/*==============================================================*/
/* Table: data_dictionary_value                                 */
/*==============================================================*/
create table data_dictionary_value
(
   id                   bigint not null auto_increment,
   parent_id            bigint comment '上级id',
   dd_id                bigint comment '数据字典id',
   order_number         int comment '排序号',
   code                 varchar(255) comment '编码',
   value                varchar(30) comment '值',
   notes                varchar(255) comment '备注',
   period_begin         timestamp comment '启用时间',
   period_end           timestamp comment '停用时间',
   created              timestamp default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   yn                   integer(4) comment '是否可用##{provider:"YnProvider", data:[{value:0,text:"不可用"},{value:1, text:"可用"}]}',
   primary key (id)
);

alter table data_dictionary_value comment '数据字典值';

/*==============================================================*/
/* Table: department                                            */
/*==============================================================*/
create table department
(
   id                   bigint not null auto_increment,
   name                 varchar(20) comment '部门名',
   code                 varchar(20) comment '编号',
   operator_id          bigint comment '操作员id',
   notes                varchar(255) comment '备注',
   created              timestamp default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '操作时间',
   primary key (id)
);

alter table department comment '部门';

/*==============================================================*/
/* Table: files                                                 */
/*==============================================================*/
create table files
(
   id                   bigint not null comment 'ID',
   name                 varchar(200) comment '文件名',
   suffix               varchar(10) comment '文件后缀',
   length               bigint comment '文件大小',
   url                  varchar(120) comment '文件地址',
   milestones_id        bigint comment '里程碑id',
   created              timestamp default CURRENT_TIMESTAMP comment '创建时间',
   create_member_id     bigint comment '创建人id##{provider:"memberProvider"}',
   primary key (id)
);

alter table files comment '需求文档，部署包，安装文件，配置文件等';

/*==============================================================*/
/* Table: member                                                */
/*==============================================================*/
create table member
(
   id                   bigint not null comment 'ID',
   name                 varchar(20) comment '姓名',
   password             varchar(20) comment '密码',
   email                varchar(40) comment '邮箱地址',
   phone_number         varchar(20) comment '手机号',
   type                 varchar(20) comment '成员类型',
   created              timestamp default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   primary key (id)
);

alter table member comment '参见权限系统的用户表';

/*==============================================================*/
/* Table: menu                                                  */
/*==============================================================*/
create table menu
(
   id                   bigint(20) not null auto_increment comment '主键',
   parent_id            bigint(20) not null comment '父级菜单id',
   order_number         integer(11) not null comment '排序号',
   menu_url             varchar(255) comment '菜单url',
   name                 varchar(255) not null comment '名称',
   description          varchar(255) not null comment '描述',
   target               integer(4) comment '打开链接的位置##{data:[{value:0, text:"当前窗口"},{value:1, text:"新开窗口"}]}',
   created              timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   yn                   integer(4) not null comment '有效性##数据有效性，用于逻辑删除##{data:[{value:0, text:"无效"},{value:1, text:"有效"}]}',
   type                 integer(4) not null default 0 comment '类型##{data:[{value:0, text:"目录"},{value:1, text:"链接"}]}',
   icon_cls             varchar(40) comment '菜单图标',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=345 DEFAULT CHARSET=utf8;

alter table menu comment '菜单';

/*==============================================================*/
/* Table: milestones                                            */
/*==============================================================*/
create table milestones
(
   id                   bigint not null,
   code                 varchar(40) comment '项目发布编号##示例:HD-001##',
   project_id           bigint comment '项目id##{provider:"projectProvider"}',
   parent_id            bigint comment '上级id',
   git                  varchar(255) comment 'git地址',
   doc_url              varchar(255) comment 'redmine文档地址',
   version              varchar(40) comment '版本号',
   market               varchar(40) comment '所属市场##{table:"data_dictionary_value", valueField:"value", textField:"code", queryParams:{yn:1, dd_id:1}, orderByClause: "order_number asc"}',
   project_phase        varchar(40) comment '项目阶段##调研，设计，开发，测试，部署，上线，维护##{table:"data_dictionary_value", valueField:"value", textField:"code", queryParams:{yn:1, dd_id:2}, orderByClause: "order_number asc"}',
   notes                varchar(255) comment '备注##主项目，sql，client##',
   publish_member_id    bigint comment '发布人id##{provider:"memberProvider"}',
   modify_member_id     bigint comment '修改人id##{provider:"memberProvider"}',
   created              timestamp default CURRENT_TIMESTAMP comment '发布时间',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   release_time         timestamp comment '上线时间',
   email_notice         int comment '是否上线通知##{provider:"emailNoticeProvider", data:[{value:0,text:"通知"},{value:1, text:"不通知"}]}',
   host                 varchar(120) comment '主机',
   port                 int comment '端口',
   visit_url            varchar(120) comment '访问地址',
   primary key (id)
);

alter table milestones comment '里程碑(版本计划)';

/*==============================================================*/
/* Table: project                                               */
/*==============================================================*/
create table project
(
   id                   bigint not null auto_increment comment 'ID',
   parent_id            bigint comment '上级项目id',
   name                 varchar(20) comment '项目名称',
   type                 varchar(10) comment '项目类型##互联网项目,移动端app,农产品项目##{table:"data_dictionary_value", valueField:"value", textField:"code", queryParams:{yn:1, dd_id:3}, orderByClause: "order_number asc"}',
   project_manager      bigint comment '开发负责人##{provider:"memberProvider"}',
   test_manager         bigint comment '测试负责人##{provider:"memberProvider"}',
   product_manager      bigint comment '产品负责人##{provider:"memberProvider"}',
   created              timestamp default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   primary key (id)
);

alter table project comment '项目';

/*==============================================================*/
/* Table: resource                                              */
/*==============================================================*/
create table resource
(
   id                   bigint(20) not null auto_increment comment '主键',
   name                 varchar(255) not null comment '名称',
   description          varchar(255) comment '描述',
   menu_id              bigint(20) not null comment '外键，关联menu表',
   code                 varchar(255) comment '编码##原resource URL',
   status               integer(4) not null comment '状态##{data:[{value:1, text:"启用"},{value:0, text:"停用"}]}',
   created              timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   yn                   integer(4) not null comment '有效性##数据有效性，用于逻辑删除##{data:[{value:0, text:"无效"},{value:1, text:"有效"}]}',
   primary key (id),
   key FK_RESOURCE_NAVBAR (menu_id)
)
ENGINE=InnoDB AUTO_INCREMENT=11623 DEFAULT CHARSET=utf8;

alter table resource comment '资源';

/*==============================================================*/
/* Table: role                                                  */
/*==============================================================*/
create table role
(
   id                   bigint(20) not null auto_increment comment '主键',
   role_name            varchar(255) not null comment '角色名',
   description          varchar(255) not null comment '角色描述',
   created              timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;

alter table role comment '角色';

/*==============================================================*/
/* Table: role_data_auth                                        */
/*==============================================================*/
create table role_data_auth
(
   id                   bigint(20) not null,
   data_auth_id         bigint(20) not null comment '数据权限表id',
   role_id              bigint(20) not null comment 'role_id',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=34768 DEFAULT CHARSET=utf8;

alter table role_data_auth comment '角色数据权限关系';

/*==============================================================*/
/* Table: role_menu                                             */
/*==============================================================*/
create table role_menu
(
   id                   bigint(20) not null comment '主键',
   role_id              bigint(20) not null comment '角色ID##外键',
   menu_id              bigint(20) not null comment '菜单ID##外键',
   primary key (id),
   key FK_ROLEMENU_ROLE (role_id),
   key FK_MENUROLE_MENU (menu_id)
)
ENGINE=InnoDB AUTO_INCREMENT=39236 DEFAULT CHARSET=utf8;

alter table role_menu comment '角色菜单关系';

/*==============================================================*/
/* Table: role_resource                                         */
/*==============================================================*/
create table role_resource
(
   id                   bigint(20) not null comment '主键',
   role_id              bigint(20) not null comment '角色ID##外键',
   resource_id          bigint(20) not null comment '资源ID##外键',
   primary key (id),
   key FK_ROLEPERMISSION_ROLE (role_id),
   key FK_ROLEPERMISSION_RESOURCE (resource_id)
)
ENGINE=InnoDB AUTO_INCREMENT=1136 DEFAULT CHARSET=utf8;

alter table role_resource comment '角色资源关系';

/*==============================================================*/
/* Table: schedule_job                                          */
/*==============================================================*/
create table schedule_job
(
   id                   bigint not null auto_increment,
   created              datetime default CURRENT_TIMESTAMP,
   modified             datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   job_name             varchar(40),
   job_group            varchar(40),
   job_status           int comment '是否启动任务,1:运行中,0:停止',
   job_data             varchar(1000) comment 'json',
   cron_expression      varchar(40),
   repeat_interval      int comment '简单调度，默认以秒为单位',
   start_delay          int comment '启动调度器后，多少秒开始执行调度',
   description          varchar(200) comment '调度器描述',
   bean_class           varchar(100) comment '任务执行时调用类的全名，用于反射',
   spring_id            varchar(40) comment 'spring的beanId，直接从spring中获取',
   url                  varchar(100) comment '支持远程调用restful url',
   is_concurrent        int comment '1：并发; 0:同步',
   method_name          varchar(40) comment 'bean_class和spring_id需要配置方法名',
   primary key (id)
);

alter table schedule_job comment '任务调度

enum JobStatus {
        NONE(0,"无")';

/*==============================================================*/
/* Table: system_config                                         */
/*==============================================================*/
create table system_config
(
   id                   bigint not null auto_increment,
   name                 varchar(100),
   code                 varchar(100),
   value                varchar(100),
   notes                varchar(255),
   创建时间                 timestamp default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   yn                   integer(4) comment '是否可用##{provider:"YnProvider"}',
   primary key (id)
);

alter table system_config comment '配置系统变量,';

/*==============================================================*/
/* Table: team                                                  */
/*==============================================================*/
create table team
(
   id                   bigint not null comment 'ID',
   project_id           bigint comment '所属项目id##{provider:"projectProvider"}',
   member_id            bigint comment '所属成员id##{provider:"memberProvider"}',
   type                 varchar(10) comment '团队类型##开发，产品，测试##{provider:"teamTypeProvider", data:[{value:1,text:"产品"},{value:2, text:"开发"},{value:3, text:"测试"}]}',
   member_state         int comment '状态##成员状态:加入|离开##{provider:"MemberStateProvider", data:[{value:0,text:"离开"},{value:1, text:"加入"}]}',
   join_time            timestamp comment '加入时间',
   leave_time           timestamp comment '离开时间',
   primary key (id)
);

alter table team comment '团队';

/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
   id                   bigint(20) not null auto_increment comment '主键',
   user_name            varchar(50) not null comment '用户名',
   password             varchar(128) not null comment '密码',
   last_login_ip        varchar(20) comment '最后登录ip',
   last_login_time      timestamp comment '最后登录时间',
   created              timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   modified             timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   status               integer(4) not null default 1 comment '状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}',
   yn                   integer(4) not null comment '有效性##数据有效性，用于逻辑删除##{data:[{value:0, text:"无效"},{value:1, text:"有效"}]}',
   real_name            varchar(64) not null default 'system_default' comment '真实姓名',
   serial_number        varchar(128) not null default '000' comment '用户编号',
   fixed_line_telephone varchar(24) not null comment '固定电话',
   cellphone            varchar(24) not null comment '手机号码',
   email                varchar(64) not null comment '邮箱',
   valid_time_begin     timestamp default CURRENT_TIMESTAMP comment '有效时间开始点',
   valid_time_end       timestamp comment '有效时间结束点',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=299 DEFAULT CHARSET=utf8;

alter table user comment '用户';

/*==============================================================*/
/* Table: user_data_auth                                        */
/*==============================================================*/
create table user_data_auth
(
   id                   bigint(20) not null,
   data_auth_id         bigint(20) not null comment '数据权限id',
   user_id              bigint(20) not null comment '用户id',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=34768 DEFAULT CHARSET=utf8;

alter table user_data_auth comment '用户数据权限关系';

/*==============================================================*/
/* Table: user_department                                       */
/*==============================================================*/
create table user_department
(
   id                   bigint not null auto_increment,
   department_id        bigint(20) not null comment '部门id',
   user_id              bigint(20) not null comment '用户id',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=34768 DEFAULT CHARSET=utf8;

alter table user_department comment '用户部门关系';

/*==============================================================*/
/* Table: user_role                                             */
/*==============================================================*/
create table user_role
(
   id                   bigint(20) not null comment '主键',
   user_id              bigint(20) not null comment '用户id##外键',
   role_id              bigint(20) not null comment '角色id##外键',
   primary key (id),
   key FK_USERROLE_USER (user_id),
   key FK_USERROLE_ROLE (role_id)
)
ENGINE=InnoDB AUTO_INCREMENT=3277 DEFAULT CHARSET=utf8;

alter table user_role comment '用户角色关系';

INSERT INTO `data_dictionary` (`id`, `code`, `name`, `notes`, `created`, `modified`, `yn`) VALUES (1, 'market', '市场', '市场', now(), now(), 1);
INSERT INTO `data_dictionary` (`id`, `code`, `name`, `notes`, `created`, `modified`, `yn`) VALUES (2, 'project_phase', '项目阶段', '项目阶段', now(), now(), 1);
INSERT INTO `data_dictionary` (`id`, `code`, `name`, `notes`, `created`, `modified`, `yn`) VALUES (3, 'project_type', '项目类型', '项目类型', now(), now(), 1);
