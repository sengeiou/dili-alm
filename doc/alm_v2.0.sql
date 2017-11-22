DROP TABLE `project_apply`;
DROP TABLE `approve`;
DROP TABLE `phase`;
DROP TABLE `project`;
DROP TABLE `version`;
DROP TABLE `task`;
DROP TABLE `project_ complete`;
DROP TABLE `project_change`;
DROP TABLE `verify_approval`;
DROP TABLE `task_details`;
DROP TABLE `weekly`;

CREATE TABLE `project_apply` (
  `ID`                   BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `number`               VARCHAR(255)    NULL
  COMMENT '立项编号',
  `name`                 VARCHAR(255)    NULL
  COMMENT '项目名称',
  `type`                 INT             NULL
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
  `resource_require`     VARCHAR(255)    NULL
  COMMENT '项目资源需求',
  `description`          VARCHAR(255)    NULL
  COMMENT '项目说明',
  `goals_functions`      VARCHAR(255)    NULL
  COMMENT '目标以及功能',
  `plan`                 VARCHAR(255)    NULL
  COMMENT '概要计划',
  `roi`                  VARCHAR(255)    NULL
  COMMENT 'ROI分析',
  `impact`               VARCHAR(255)    NULL
  COMMENT '项目影响',
  `risk`                 VARCHAR(255)    NULL
  COMMENT '项目风险',
  `email`                VARCHAR(255)    NULL,
  `status`               TINYINT         NULL,
  `created`              TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`             TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id`     BIGINT          NULL,
  `modify_member_id`     BIGINT          NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `approve` (
  `id`               BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `project_apply_id` BIGINT          NULL,
  `status`           TINYINT         NULL,
  `description`      VARCHAR(255)    NULL,
  `type`             TINYINT         NULL
  COMMENT '审批类型（立项，结项）',
  `created`          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`         TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id` BIGINT          NULL,
  `modify_member_id` BIGINT          NULL,
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
  PRIMARY KEY (`id`)
);

CREATE TABLE `project_ complete` (
  `id`                BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `project_id`        BIGINT          NULL
  COMMENT '项目编号',
  `launch_date`       DATETIME        NULL
  COMMENT '实际上线日期',
  `complete_date`     DATETIME        NULL
  COMMENT '结项日期',
  `reason`            VARCHAR(255)    NULL,
  `information`       VARCHAR(255)    NULL
  COMMENT '项目资料齐备情况',
  `range`             VARCHAR(255)    NULL,
  `management_method` VARCHAR(255)    NULL
  COMMENT '项目管理方法',
  `appraise`          VARCHAR(255)    NULL
  COMMENT '项目管理过程评价',
  `raise`             VARCHAR(255)    NULL
  COMMENT '进步与提高',
  `result`            VARCHAR(255)    NULL
  COMMENT '成果',
  `product`           VARCHAR(255)    NULL
  COMMENT '产品',
  `performance`       VARCHAR(255)    NULL
  COMMENT '主要功能和性能',
  `suggest`           VARCHAR(255)    NULL
  COMMENT '建议',
  `question`          VARCHAR(255)    NULL
  COMMENT '问题',
  `members`           VARCHAR(255)    NULL
  COMMENT '成员',
  `hardware`          VARCHAR(255)    NULL
  COMMENT '硬件',
  `email`             VARCHAR(255)    NULL,
  `status`            TINYINT         NULL,
  `created`           TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `modified`          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP,
  `create_member_id`  BIGINT          NULL,
  `modify_member_id`  BIGINT          NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `project_change` (
  `id`               BIGINT ZEROFILL NOT NULL AUTO_INCREMENT,
  `name`             VARCHAR(255)    NULL
  COMMENT '变更名称',
  `project_id`       BIGINT          NULL
  COMMENT '变更项目',
  `version_id`       BIGINT          NULL
  COMMENT '版本',
  `phase_id`         BIGINT          NULL
  COMMENT '阶段',
  `type`             TINYINT         NULL,
  `working_hours`    VARCHAR(255)    NULL
  COMMENT '预估工时',
  `affects_ online`  TINYINT         NULL
  COMMENT '是否影响上线',
  `submit_date`      DATETIME        NULL
  COMMENT '提交日期',
  `content`          VARCHAR(255)    NULL,
  `effects`          VARCHAR(255)    NULL
  COMMENT '影响说明',
  `email`            VARCHAR(255)    NULL,
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

