DROP TABLE `project_apply`;
DROP TABLE `approve`;
DROP TABLE `phase`;
DROP TABLE `project`;
DROP TABLE `version`;
DROP TABLE `task`;
DROP TABLE project_complete;
DROP TABLE `project_change`;
DROP TABLE `verify_approval`;
DROP TABLE `task_details`;
DROP TABLE `weekly`;
DROP TABLE `log`;
DROP TABLE `work_schedule`;
DROP TABLE `message`;


CREATE TABLE `project_apply` (
  `ID`                   BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `number`               VARCHAR(255)    NULL
  COMMENT '立项编号',
  `name`                 VARCHAR(255)    NULL
  COMMENT '项目名称',
  `type`                 VARCHAR(10)     NULL
  COMMENT '项目类型',
  `pid`                  BIGINT          NULL
  COMMENT '父项目',
  `project_leader`       BIGINT          NULL
  COMMENT '项目负责人',
  `product_manager`      BIGINT          NULL
  COMMENT '产品经理',
  `development_manager`  BIGINT          NULL
  COMMENT '研发经理',
  `test_manager`         BIGINT          NULL
  COMMENT '测试经理',
  `business_owner`       BIGINT          NULL
  COMMENT '业务负责人',
  `dep`                  BIGINT          NULL
  COMMENT '所属部门',
  `expected_launch_date` DATETIME        NULL
  COMMENT '期望上线日期',
  `estimate_launch_date` DATETIME        NULL
  COMMENT '预估上线日期',
  `start_date`           DATETIME        NULL
  COMMENT '计划开始日期',
  `end_date`             DATETIME        NULL
  COMMENT '计划结束日期',
  `resource_require`     VARCHAR(1000)   NULL
  COMMENT '项目资源需求',
  `description`          VARCHAR(1000)   NULL
  COMMENT '项目说明',
  `goals_functions`      VARCHAR(1000)   NULL
  COMMENT '目标以及功能',
  `plan`                 VARCHAR(1000)   NULL
  COMMENT '概要计划',
  `roi`                  VARCHAR(1000)   NULL
  COMMENT 'ROI分析',
  `impact`               VARCHAR(1000)   NULL
  COMMENT '项目影响',
  `risk`                 VARCHAR(1000)   NULL
  COMMENT '项目风险',
  `email`                VARCHAR(255)    NULL,
  `status`               TINYINT         NULL,
  `reStatus`             TINYINT         NULL,
  `created`              TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`             TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id`     BIGINT          NULL,
  `modify_member_id`     BIGINT          NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `approve` (
  `id`                   BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `project_apply_id`     BIGINT          NULL,
  `number`               VARCHAR(255)    NULL
  COMMENT '项目编号',
  `name`                 VARCHAR(255)    NULL
  COMMENT '项目名称',
  `project_type`         VARCHAR(255)    NULL
  COMMENT '项目类型',
  `project_leader`       BIGINT          NULL
  COMMENT '项目负责人',
  `business_owner`       BIGINT          NULL
  COMMENT '业务负责人',
  `dep`                  BIGINT          NULL
  COMMENT '所属部门',
  `start_date`           DATETIME        NULL
  COMMENT '计划开始日期',
  `end_date`             DATETIME        NULL
  COMMENT '计划结束日期',
  `expected_launch_date` DATETIME        NULL
  COMMENT '期望上线日期',
  `estimate_launch_date` DATETIME        NULL
  COMMENT '预估上线日期',
  `status`               INT             NULL,
  `description`          VARCHAR(1000)   NULL,
  `extend`               VARCHAR(1000)   NULL,
  `type`                 VARCHAR(10)     NULL
  COMMENT '审批类型（立项，结项）',
  `created`              TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`             TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id`     BIGINT          NULL,
  `modify_member_id`     BIGINT          NULL,
  PRIMARY KEY (`id`)
);

# CREATE TABLE `phase` (
#   `id`               BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
#   `phase`            BIGINT          NULL,
#   `version_id`       BIGINT          NULL,
#   `start_date`       DATETIME     NULL,
#   `end_date`         DATETIME     NULL,
#   `created`          TIMESTAMP    NULL     DEFAULT CURRENT_TIMESTAMP,
#   `modified`         TIMESTAMP    NULL     DEFAULT CURRENT_TIMESTAMP,
#   `create_member_id` BIGINT          NULL,
#   `modify_member_id` BIGINT          NULL,
#   PRIMARY KEY (`id`)
# );

CREATE TABLE `project` (
  `id`                  BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `project_apply_id`    BIGINT          NULL
  COMMENT '立项编号',
  `number`              VARCHAR(255)    NULL
  COMMENT '项目编号',
  `type`                INT             NULL
  COMMENT '项目类型',
  `project_leader`      BIGINT          NULL
  COMMENT '项目负责人',
  `business_owner`       BIGINT          NULL
  COMMENT '业务负责人',
  `dep`      BIGINT          NULL
  COMMENT '所属部门',
  `product_manager`     BIGINT          NULL
  COMMENT '产品经理',
  `development_manager` BIGINT          NULL
  COMMENT '研发经理',
  `test_manager`        BIGINT          NULL
  COMMENT '测试经理',
  `start_date`          DATETIME        NULL
  COMMENT '计划开始日期',
  `end_date`            DATETIME        NULL
  COMMENT '计划结束日期',
  `estimate_launch_date` DATETIME        NULL
  COMMENT '预估上线日期',
  `progress`            VARCHAR(255)    NULL
  COMMENT '进度',
  `status`              TINYINT         NULL,
  `created`             TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`            TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id`    BIGINT          NULL,
  `modify_member_id`    BIGINT          NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `version` (
  `id`               BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `name`             VARCHAR(255)    NULL
  COMMENT '版本名称',
  `online`           TINYINT         NULL
  COMMENT '是否上线',
  `launch_date`      DATETIME        NULL
  COMMENT '计划上线日期',
  `start_date`       DATETIME        NULL
  COMMENT '计划开始日期',
  `end_date`         DATETIME        NULL
  COMMENT '计划结束日期',
  `IP`               VARCHAR(255)    NULL,
  `port`             VARCHAR(255)    NULL,
  `git`              VARCHAR(255)    NULL,
  `doc_url`          VARCHAR(255)    NULL,
  `url`              VARCHAR(255)    NULL,
  `described`        VARCHAR(255)    NULL,
  `progress`         VARCHAR(255)    NULL
  COMMENT '进度',
  `status`           TINYINT(255)    NULL,
  `created`          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`         TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id` BIGINT          NULL,
  `modify_member_id` BIGINT          NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `task` (
  `id`               BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `name`             VARCHAR(255)    NULL,
  `project_id`       BIGINT          NULL,
  `version_id`       BIGINT          NULL,
  `phase_id`         BIGINT          NULL,
  `start_date`       DATETIME        NULL,
  `end_date`         DATETIME        NULL,
  `before_task`      BIGINT          NULL
  COMMENT '前置任务',
  `type`             INT             NULL
  COMMENT '任务类型',
  `owner`            BIGINT          NULL
  COMMENT '负责人',
  `plan_time`        SMALLINT        NULL
  COMMENT '计划工时',
  `flow`             BIT             NULL
  COMMENT '流程',
  `change_id`        BIGINT          NULL,
  `describe`         VARCHAR(255)    NULL
  COMMENT '描述',
  `status`           TINYINT         NULL,
  `created`          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`         TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id` BIGINT          NULL,
  `modify_member_id` BIGINT          NULL,
  `fact_begin_date` datetime DEFAULT NULL COMMENT '任务实际开始执行时间',
  `fact_end_date` datetime DEFAULT NULL COMMENT '任务实际完成时间',
  PRIMARY KEY (`id`)
);

CREATE TABLE `project_complete` (
  `id`                   BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `project_id`           BIGINT          NULL
  COMMENT '项目Id',
  `number`               VARCHAR(255)    NULL
  COMMENT '项目编号',
  `name`                 VARCHAR(255)    NULL
  COMMENT '项目名称',
  `type`                 VARCHAR(255)    NULL
  COMMENT '项目类型',
  `start_date`           DATETIME        NULL
  COMMENT '计划开始日期',
  `end_date`             DATETIME        NULL
  COMMENT '计划结束日期',
  `launch_date`          DATETIME        NULL
  COMMENT '实际上线日期',
  `complete_date`        DATETIME        NULL
  COMMENT '结项日期',
  `reason`               VARCHAR(255)    NULL,
  `information`          VARCHAR(255)    NULL
  COMMENT '项目资料齐备情况',
  `range`                VARCHAR(255)    NULL,
  `management_method`    VARCHAR(255)    NULL
  COMMENT '项目管理方法',
  `appraise`             VARCHAR(255)    NULL
  COMMENT '项目管理过程评价',
  `technical_evaluation` VARCHAR(255)    NULL
  COMMENT '技术方法评价',
  `raise`                VARCHAR(255)    NULL
  COMMENT '进步与提高',
  `result`               VARCHAR(255)    NULL
  COMMENT '成果',
  `product`              VARCHAR(255)    NULL
  COMMENT '产品',
  `performance`          VARCHAR(255)    NULL
  COMMENT '主要功能和性能',
  `suggest`              VARCHAR(255)    NULL
  COMMENT '建议',
  `question`             VARCHAR(255)    NULL
  COMMENT '问题',
  `members`              VARCHAR(255)    NULL
  COMMENT '成员',
  `hardware`             VARCHAR(255)    NULL
  COMMENT '硬件',
  `email`                VARCHAR(255)    NULL,
  `status`               TINYINT         NULL,
  `reStatus`             TINYINT         NULL,
  `created`              TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`             TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id`     BIGINT          NULL,
  `modify_member_id`     BIGINT          NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `project_change` (
  `id`               BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `name`             VARCHAR(255)    NULL
  COMMENT '变更名称',
  `number`           VARCHAR(255)    NULL
  COMMENT '变更编号',
  `project_name`     VARCHAR(255)    NULL
  COMMENT '项目名称',
  `project_id`       BIGINT          NULL
  COMMENT '变更项目',
  `version_id`       BIGINT          NULL
  COMMENT '版本',
  `phase_id`         BIGINT          NULL
  COMMENT '阶段',
  `type`             VARCHAR(50)     NULL,
  `working_hours`    VARCHAR(255)    NULL
  COMMENT '预估工时',
  `estimate_launch_date` DATETIME        NULL
  COMMENT '预估上线日期',
  `affects_online`  TINYINT         NULL
  COMMENT '是否影响上线',
  `submit_date`      DATETIME        NULL
  COMMENT '提交日期',
  `content`          VARCHAR(255)    NULL,
  `effects`          VARCHAR(255)    NULL
  COMMENT '影响说明',
  `email`            VARCHAR(255)    NULL,
  `status`           TINYINT         NULL,
  `reStatus`         TINYINT         NULL,
  `created`          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`         TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id` BIGINT          NULL,
  `modify_member_id` BIGINT          NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `verify_approval` (
  `ID`               BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `approve_id`       BIGINT          NULL
  COMMENT '审批编号',
  `approver`         BIGINT          NULL,
  `result`           VARCHAR(255)    NULL,
  `created`          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`         TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id` BIGINT          NULL,
  `modify_member_id` BIGINT          NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `task_details` (
  `id`               BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `task_id`          BIGINT          NULL,
  `task_hour`        SMALLINT        NULL
  COMMENT '工时',
  `over_hour`        SMALLINT        NULL
  COMMENT '加班工时',
  `describe`         VARCHAR(255)    NULL
  COMMENT '工作描述',
  `task_time`        VARCHAR(255)    NULL
  COMMENT '工时填写时间',
  `created`          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`         TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id` BIGINT          NULL,
  `modify_member_id` BIGINT          NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `weekly` (
  `id`               BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `project_id`       BIGINT          NULL,
  `start_date`       DATETIME        NULL,
  `end_date`         DATETIME        NULL,
  `risk`             VARCHAR(255)    NULL
  COMMENT '风险',
  `question`         VARCHAR(255)    NULL
  COMMENT '问题',
  `progress`         VARCHAR(255)    NULL
  COMMENT '进度',
  `created`          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`         TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id` BIGINT          NULL,
  `modify_member_id` BIGINT          NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作员',
  `ip` varchar(20) DEFAULT NULL COMMENT 'IP',
  `content` text COMMENT '内容',
  PRIMARY KEY (`id`)
)

CREATE TABLE `work_schedule` (
  `id`            BIGINT(20) NOT NULL AUTO_INCREMENT
  COMMENT '主键',
  `schedule_date` TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
  COMMENT '编辑时间',
  `schedule_text` VARCHAR(255)        DEFAULT NULL
  COMMENT '编辑内容',
  `user_id`       BIGINT(20)          DEFAULT NULL
  COMMENT '用户id',
  PRIMARY KEY (`id`)
);

CREATE TABLE `sequence`
(
  number INT NULL
);

CREATE TABLE `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(50) DEFAULT NULL COMMENT '跳转Url',
  `sender` bigint(20) DEFAULT NULL COMMENT '发送人Id',
  `recipient` bigint(20) DEFAULT NULL COMMENT '接收人Id',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `type` int(2) DEFAULT NULL COMMENT '消息类型',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  PRIMARY KEY (`id`)
) 
