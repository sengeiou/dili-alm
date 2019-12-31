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
 * This file was generated on 2017-12-05 14:55:38.
 */
@Table(name = "`approve`")
public interface Approve extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`project_apply_id`")
    @FieldDef(label="projectApplyId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectApplyId();

    void setProjectApplyId(Long projectApplyId);

    @Column(name = "`number`")
    @FieldDef(label="项目编号", maxLength = 255)
    @Like(Like.BOTH)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNumber();

    void setNumber(String number);

    @Column(name = "`name`")
    @Like(Like.BOTH)
    @FieldDef(label="项目名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getName();

    void setName(String name);

    @Column(name = "`project_type`")
    @FieldDef(label="项目类型", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProjectType();

    void setProjectType(String projectType);

    @Column(name = "`project_leader`")
    @FieldDef(label="项目负责人")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectLeader();

    void setProjectLeader(Long projectLeader);

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

    @Column(name = "`start_date`")
    @FieldDef(label="计划开始日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getStartDate();

    void setStartDate(Date startDate);

    @Column(name = "`end_date`")
    @FieldDef(label="计划结束日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getEndDate();

    void setEndDate(Date endDate);

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

    @Column(name = "`status`")
    @FieldDef(label="status")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getStatus();

    void setStatus(Integer status);

    @Column(name = "`description`")
    @FieldDef(label="description", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDescription();

    void setDescription(String description);

    @Column(name = "`extend`")
    @FieldDef(label="extend", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getExtend();

    void setExtend(String extend);

    @Column(name = "`type`")
    @FieldDef(label="审批类型（立项，结项）", maxLength = 10)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getType();

    void setType(String type);

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
    
    
}