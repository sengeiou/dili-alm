package com.dili.alm.domain;

import com.dili.ss.domain.annotation.Like;
import com.dili.ss.domain.annotation.Operator;
import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

import javax.persistence.*;
import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-12-18 14:59:21.
 */
@Table(name = "`project_apply`")
public interface ProjectApply extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`ID`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`number`")
    @FieldDef(label="立项编号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNumber();

    void setNumber(String number);

    @Like(value = Like.BOTH)
    @Column(name = "`name`")
    @FieldDef(label="项目名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getName();

    void setName(String name);

    @Column(name = "`type`")
    @FieldDef(label="项目类型", maxLength = 10)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getType();

    void setType(String type);

    @Column(name = "`pid`")
    @FieldDef(label="父项目")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getPid();

    void setPid(Long pid);

    @Column(name = "`project_leader`")
    @FieldDef(label="项目负责人")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectLeader();

    void setProjectLeader(Long projectLeader);

    @Column(name = "`product_manager`")
    @FieldDef(label="产品经理")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProductManager();

    void setProductManager(Long productManager);

    @Column(name = "`development_manager`")
    @FieldDef(label="研发经理")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getDevelopmentManager();

    void setDevelopmentManager(Long developmentManager);

    @Column(name = "`test_manager`")
    @FieldDef(label="测试经理")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getTestManager();

    void setTestManager(Long testManager);

    @Column(name = "`business_owner`")
    @FieldDef(label="业务负责人")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getBusinessOwner();

    void setBusinessOwner(Long businessOwner);

    @Column(name = "`dep`")
    @FieldDef(label="所属部门")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getDep();

    void setDep(Long dep);

    @Column(name = "`expected_launch_date`")
    @FieldDef(label="期望上线日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getExpectedLaunchDate();

    void setExpectedLaunchDate(Date expectedLaunchDate);

    @Column(name = "`estimate_launch_date`")
    @FieldDef(label="预估上线日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getEstimateLaunchDate();

    void setEstimateLaunchDate(Date estimateLaunchDate);

    @Column(name = "`start_date`")
    @FieldDef(label="计划开始日期")
    @Operator(Operator.GREAT_EQUAL_THAN)
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getStartDate();

    void setStartDate(Date startDate);

    @Column(name = "`end_date`")
    @Operator(Operator.LITTLE_EQUAL_THAN)
    @FieldDef(label="计划结束日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getEndDate();

    void setEndDate(Date endDate);

    @Column(name = "`resource_require`")
    @FieldDef(label="项目资源需求", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getResourceRequire();

    void setResourceRequire(String resourceRequire);

    @Column(name = "`description`")
    @FieldDef(label="项目说明", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDescription();

    void setDescription(String description);

    @Column(name = "`goals_functions`")
    @FieldDef(label="目标以及功能", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getGoalsFunctions();

    void setGoalsFunctions(String goalsFunctions);

    @Column(name = "`plan`")
    @FieldDef(label="概要计划", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getPlan();

    void setPlan(String plan);

    @Column(name = "`roi`")
    @FieldDef(label="ROI分析", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getRoi();

    void setRoi(String roi);

    @Column(name = "`impact`")
    @FieldDef(label="项目影响", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getImpact();

    void setImpact(String impact);

    @Column(name = "`risk`")
    @FieldDef(label="项目风险", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getRisk();

    void setRisk(String risk);

    @Column(name = "`email`")
    @FieldDef(label="email", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getEmail();

    void setEmail(String email);

    @Column(name = "`status`")
    @FieldDef(label="status")
    @EditMode(editor = FieldEditor.Text, required = false)
    Integer getStatus();

    void setStatus(Integer status);


    @Operator(Operator.LITTLE_EQUAL_THAN)
    @Column(name = "`created`")
    @FieldDef(label="created")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getCreated();

    void setCreated(Date created);

    @Operator(Operator.GREAT_EQUAL_THAN)
    @Column(name = "`created`")
    Date getCreatedStart();
    void setCreatedStart(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="modified")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getModified();

    void setModified(Date modified);

    @Column(name = "`create_member_id`")
    @FieldDef(label="createMemberId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getCreateMemberId();

    void setCreateMemberId(Long createMemberId);

    @Column(name = "`modify_member_id`")
    @FieldDef(label="modifyMemberId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getModifyMemberId();

    void setModifyMemberId(Long modifyMemberId);

    @Column(name = "`reStatus`")
    @FieldDef(label="restatus")
    @EditMode(editor = FieldEditor.Text, required = false)
    Integer getRestatus();

    void setRestatus(Integer restatus);
}