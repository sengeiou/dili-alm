/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/10/18 ������ 16:06:55                      */
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
   type                 varchar(20) not null comment '����Ȩ�޷���##����ɾ����ҵ��ϵͳȷ��',
   data_id              varchar(50) not null comment '����Ȩ��ID##ҵ����',
   name                 varchar(50) not null comment '����',
   parent_data_id       bigint(20) not null default 0 comment '��������ID##�����������������Ȩ��',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=34768 DEFAULT CHARSET=utf8;

alter table data_auth comment '����Ȩ��';

/*==============================================================*/
/* Table: data_dictionary                                       */
/*==============================================================*/
create table data_dictionary
(
   id                   bigint not null auto_increment,
   code                 varchar(50),
   name                 varchar(50),
   notes                varchar(255),
   created              timestamp default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   yn                   integer(4) comment '�Ƿ����##{provider:"YnProvider"}',
   primary key (id)
);

alter table data_dictionary comment '�����ֵ�
��:
ϵͳͼƬ:IMAGE_CODE';

/*==============================================================*/
/* Table: data_dictionary_value                                 */
/*==============================================================*/
create table data_dictionary_value
(
   id                   bigint not null auto_increment,
   parent_id            bigint comment '�ϼ�id',
   dd_id                bigint comment '�����ֵ�id',
   order_number         int comment '�����',
   code                 varchar(255) comment '����',
   value                varchar(30) comment 'ֵ',
   notes                varchar(255) comment '��ע',
   period_begin         timestamp comment '����ʱ��',
   period_end           timestamp comment 'ͣ��ʱ��',
   created              timestamp default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   yn                   integer(4) comment '�Ƿ����##{provider:"YnProvider", data:[{value:0,text:"������"},{value:1, text:"����"}]}',
   primary key (id)
);

alter table data_dictionary_value comment '�����ֵ�ֵ';

/*==============================================================*/
/* Table: department                                            */
/*==============================================================*/
create table department
(
   id                   bigint not null auto_increment,
   name                 varchar(20) comment '������',
   code                 varchar(20) comment '���',
   operator_id          bigint comment '����Աid',
   notes                varchar(255) comment '��ע',
   created              timestamp default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '����ʱ��',
   primary key (id)
);

alter table department comment '����';

/*==============================================================*/
/* Table: files                                                 */
/*==============================================================*/
create table files
(
   id                   bigint not null comment 'ID',
   name                 varchar(200) comment '�ļ���',
   suffix               varchar(10) comment '�ļ���׺',
   length               bigint comment '�ļ���С',
   url                  varchar(120) comment '�ļ���ַ',
   milestones_id        bigint comment '��̱�id',
   created              timestamp default CURRENT_TIMESTAMP comment '����ʱ��',
   create_member_id     bigint comment '������id##{provider:"memberProvider"}',
   primary key (id)
);

alter table files comment '�����ĵ������������װ�ļ��������ļ���';

/*==============================================================*/
/* Table: member                                                */
/*==============================================================*/
create table member
(
   id                   bigint not null comment 'ID',
   name                 varchar(20) comment '����',
   password             varchar(20) comment '����',
   email                varchar(40) comment '�����ַ',
   phone_number         varchar(20) comment '�ֻ���',
   type                 varchar(20) comment '��Ա����',
   created              timestamp default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   primary key (id)
);

alter table member comment '�μ�Ȩ��ϵͳ���û���';

/*==============================================================*/
/* Table: menu                                                  */
/*==============================================================*/
create table menu
(
   id                   bigint(20) not null auto_increment comment '����',
   parent_id            bigint(20) not null comment '�����˵�id',
   order_number         integer(11) not null comment '�����',
   menu_url             varchar(255) comment '�˵�url',
   name                 varchar(255) not null comment '����',
   description          varchar(255) not null comment '����',
   target               integer(4) comment '�����ӵ�λ��##{data:[{value:0, text:"��ǰ����"},{value:1, text:"�¿�����"}]}',
   created              timestamp not null default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   yn                   integer(4) not null comment '��Ч��##������Ч�ԣ������߼�ɾ��##{data:[{value:0, text:"��Ч"},{value:1, text:"��Ч"}]}',
   type                 integer(4) not null default 0 comment '����##{data:[{value:0, text:"Ŀ¼"},{value:1, text:"����"}]}',
   icon_cls             varchar(40) comment '�˵�ͼ��',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=345 DEFAULT CHARSET=utf8;

alter table menu comment '�˵�';

/*==============================================================*/
/* Table: milestones                                            */
/*==============================================================*/
create table milestones
(
   id                   bigint not null,
   code                 varchar(40) comment '��Ŀ�������##ʾ��:HD-001##',
   project_id           bigint comment '��Ŀid##{provider:"projectProvider"}',
   parent_id            bigint comment '�ϼ�id',
   git                  varchar(255) comment 'git��ַ',
   doc_url              varchar(255) comment 'redmine�ĵ���ַ',
   version              varchar(40) comment '�汾��',
   market               varchar(40) comment '�����г�##{table:"data_dictionary_value", valueField:"value", textField:"code", queryParams:{yn:1, dd_id:1}, orderByClause: "order_number asc"}',
   project_phase        varchar(40) comment '��Ŀ�׶�##���У���ƣ����������ԣ��������ߣ�ά��##{table:"data_dictionary_value", valueField:"value", textField:"code", queryParams:{yn:1, dd_id:2}, orderByClause: "order_number asc"}',
   notes                varchar(255) comment '��ע##����Ŀ��sql��client##',
   publish_member_id    bigint comment '������id##{provider:"memberProvider"}',
   modify_member_id     bigint comment '�޸���id##{provider:"memberProvider"}',
   created              timestamp default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   release_time         timestamp comment '����ʱ��',
   email_notice         int comment '�Ƿ�����֪ͨ##{provider:"emailNoticeProvider", data:[{value:0,text:"֪ͨ"},{value:1, text:"��֪ͨ"}]}',
   host                 varchar(120) comment '����',
   port                 int comment '�˿�',
   visit_url            varchar(120) comment '���ʵ�ַ',
   primary key (id)
);

alter table milestones comment '��̱�(�汾�ƻ�)';

/*==============================================================*/
/* Table: project                                               */
/*==============================================================*/
create table project
(
   id                   bigint not null auto_increment comment 'ID',
   parent_id            bigint comment '�ϼ���Ŀid',
   name                 varchar(20) comment '��Ŀ����',
   type                 varchar(10) comment '��Ŀ����##��������Ŀ,�ƶ���app,ũ��Ʒ��Ŀ##{table:"data_dictionary_value", valueField:"value", textField:"code", queryParams:{yn:1, dd_id:3}, orderByClause: "order_number asc"}',
   project_manager      bigint comment '����������##{provider:"memberProvider"}',
   test_manager         bigint comment '���Ը�����##{provider:"memberProvider"}',
   product_manager      bigint comment '��Ʒ������##{provider:"memberProvider"}',
   created              timestamp default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   primary key (id)
);

alter table project comment '��Ŀ';

/*==============================================================*/
/* Table: resource                                              */
/*==============================================================*/
create table resource
(
   id                   bigint(20) not null auto_increment comment '����',
   name                 varchar(255) not null comment '����',
   description          varchar(255) comment '����',
   menu_id              bigint(20) not null comment '���������menu��',
   code                 varchar(255) comment '����##ԭresource URL',
   status               integer(4) not null comment '״̬##{data:[{value:1, text:"����"},{value:0, text:"ͣ��"}]}',
   created              timestamp not null default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   yn                   integer(4) not null comment '��Ч��##������Ч�ԣ������߼�ɾ��##{data:[{value:0, text:"��Ч"},{value:1, text:"��Ч"}]}',
   primary key (id),
   key FK_RESOURCE_NAVBAR (menu_id)
)
ENGINE=InnoDB AUTO_INCREMENT=11623 DEFAULT CHARSET=utf8;

alter table resource comment '��Դ';

/*==============================================================*/
/* Table: role                                                  */
/*==============================================================*/
create table role
(
   id                   bigint(20) not null auto_increment comment '����',
   role_name            varchar(255) not null comment '��ɫ��',
   description          varchar(255) not null comment '��ɫ����',
   created              timestamp not null default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;

alter table role comment '��ɫ';

/*==============================================================*/
/* Table: role_data_auth                                        */
/*==============================================================*/
create table role_data_auth
(
   id                   bigint(20) not null,
   data_auth_id         bigint(20) not null comment '����Ȩ�ޱ�id',
   role_id              bigint(20) not null comment 'role_id',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=34768 DEFAULT CHARSET=utf8;

alter table role_data_auth comment '��ɫ����Ȩ�޹�ϵ';

/*==============================================================*/
/* Table: role_menu                                             */
/*==============================================================*/
create table role_menu
(
   id                   bigint(20) not null comment '����',
   role_id              bigint(20) not null comment '��ɫID##���',
   menu_id              bigint(20) not null comment '�˵�ID##���',
   primary key (id),
   key FK_ROLEMENU_ROLE (role_id),
   key FK_MENUROLE_MENU (menu_id)
)
ENGINE=InnoDB AUTO_INCREMENT=39236 DEFAULT CHARSET=utf8;

alter table role_menu comment '��ɫ�˵���ϵ';

/*==============================================================*/
/* Table: role_resource                                         */
/*==============================================================*/
create table role_resource
(
   id                   bigint(20) not null comment '����',
   role_id              bigint(20) not null comment '��ɫID##���',
   resource_id          bigint(20) not null comment '��ԴID##���',
   primary key (id),
   key FK_ROLEPERMISSION_ROLE (role_id),
   key FK_ROLEPERMISSION_RESOURCE (resource_id)
)
ENGINE=InnoDB AUTO_INCREMENT=1136 DEFAULT CHARSET=utf8;

alter table role_resource comment '��ɫ��Դ��ϵ';

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
   job_status           int comment '�Ƿ���������,1:������,0:ֹͣ',
   job_data             varchar(1000) comment 'json',
   cron_expression      varchar(40),
   repeat_interval      int comment '�򵥵��ȣ�Ĭ������Ϊ��λ',
   start_delay          int comment '�����������󣬶����뿪ʼִ�е���',
   description          varchar(200) comment '����������',
   bean_class           varchar(100) comment '����ִ��ʱ�������ȫ�������ڷ���',
   spring_id            varchar(40) comment 'spring��beanId��ֱ�Ӵ�spring�л�ȡ',
   url                  varchar(100) comment '֧��Զ�̵���restful url',
   is_concurrent        int comment '1������; 0:ͬ��',
   method_name          varchar(40) comment 'bean_class��spring_id��Ҫ���÷�����',
   primary key (id)
);

alter table schedule_job comment '�������

enum JobStatus {
        NONE(0,"��")';

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
   ����ʱ��                 timestamp default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   yn                   integer(4) comment '�Ƿ����##{provider:"YnProvider"}',
   primary key (id)
);

alter table system_config comment '����ϵͳ����,';

/*==============================================================*/
/* Table: team                                                  */
/*==============================================================*/
create table team
(
   id                   bigint not null comment 'ID',
   project_id           bigint comment '������Ŀid##{provider:"projectProvider"}',
   member_id            bigint comment '������Աid##{provider:"memberProvider"}',
   type                 varchar(10) comment '�Ŷ�����##��������Ʒ������##{provider:"teamTypeProvider", data:[{value:1,text:"��Ʒ"},{value:2, text:"����"},{value:3, text:"����"}]}',
   member_state         int comment '״̬##��Ա״̬:����|�뿪##{provider:"MemberStateProvider", data:[{value:0,text:"�뿪"},{value:1, text:"����"}]}',
   join_time            timestamp comment '����ʱ��',
   leave_time           timestamp comment '�뿪ʱ��',
   primary key (id)
);

alter table team comment '�Ŷ�';

/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
   id                   bigint(20) not null auto_increment comment '����',
   user_name            varchar(50) not null comment '�û���',
   password             varchar(128) not null comment '����',
   last_login_ip        varchar(20) comment '����¼ip',
   last_login_time      timestamp comment '����¼ʱ��',
   created              timestamp not null default CURRENT_TIMESTAMP comment '����ʱ��',
   modified             timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '�޸�ʱ��',
   status               integer(4) not null default 1 comment '״̬##״̬##{data:[{value:1,text:"����"},{value:0,text:"ͣ��"}]}',
   yn                   integer(4) not null comment '��Ч��##������Ч�ԣ������߼�ɾ��##{data:[{value:0, text:"��Ч"},{value:1, text:"��Ч"}]}',
   real_name            varchar(64) not null default 'system_default' comment '��ʵ����',
   serial_number        varchar(128) not null default '000' comment '�û����',
   fixed_line_telephone varchar(24) not null comment '�̶��绰',
   cellphone            varchar(24) not null comment '�ֻ�����',
   email                varchar(64) not null comment '����',
   valid_time_begin     timestamp default CURRENT_TIMESTAMP comment '��Чʱ�俪ʼ��',
   valid_time_end       timestamp comment '��Чʱ�������',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=299 DEFAULT CHARSET=utf8;

alter table user comment '�û�';

/*==============================================================*/
/* Table: user_data_auth                                        */
/*==============================================================*/
create table user_data_auth
(
   id                   bigint(20) not null,
   data_auth_id         bigint(20) not null comment '����Ȩ��id',
   user_id              bigint(20) not null comment '�û�id',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=34768 DEFAULT CHARSET=utf8;

alter table user_data_auth comment '�û�����Ȩ�޹�ϵ';

/*==============================================================*/
/* Table: user_department                                       */
/*==============================================================*/
create table user_department
(
   id                   bigint not null auto_increment,
   department_id        bigint(20) not null comment '����id',
   user_id              bigint(20) not null comment '�û�id',
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=34768 DEFAULT CHARSET=utf8;

alter table user_department comment '�û����Ź�ϵ';

/*==============================================================*/
/* Table: user_role                                             */
/*==============================================================*/
create table user_role
(
   id                   bigint(20) not null comment '����',
   user_id              bigint(20) not null comment '�û�id##���',
   role_id              bigint(20) not null comment '��ɫid##���',
   primary key (id),
   key FK_USERROLE_USER (user_id),
   key FK_USERROLE_ROLE (role_id)
)
ENGINE=InnoDB AUTO_INCREMENT=3277 DEFAULT CHARSET=utf8;

alter table user_role comment '�û���ɫ��ϵ';

INSERT INTO `data_dictionary` (`id`, `code`, `name`, `notes`, `created`, `modified`, `yn`) VALUES (1, 'market', '�г�', '�г�', now(), now(), 1);
INSERT INTO `data_dictionary` (`id`, `code`, `name`, `notes`, `created`, `modified`, `yn`) VALUES (2, 'project_phase', '��Ŀ�׶�', '��Ŀ�׶�', now(), now(), 1);
INSERT INTO `data_dictionary` (`id`, `code`, `name`, `notes`, `created`, `modified`, `yn`) VALUES (3, 'project_type', '��Ŀ����', '��Ŀ����', now(), now(), 1);
