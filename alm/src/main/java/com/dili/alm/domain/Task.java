package com.dili.alm.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-11-23 10:23:05.
 */
@Table(name = "`task`")
public interface Task extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`name`")
    @FieldDef(label="name", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getName();

    void setName(String name);

    @Column(name = "`project_id`")
    @FieldDef(label="projectId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`version_id`")
    @FieldDef(label="versionId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getVersionId();

    void setVersionId(Long versionId);

    @Column(name = "`phase_id`")
    @FieldDef(label="phaseId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getPhaseId();

    void setPhaseId(Long phaseId);

    @Column(name = "`start_date`")
    @FieldDef(label="startDate")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getStartDate();

    void setStartDate(Date startDate);

    @Column(name = "`end_date`")
    @FieldDef(label="endDate")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getEndDate();

    void setEndDate(Date endDate);

    @Column(name = "`before_task`")
    @FieldDef(label="前置任务")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getBeforeTask();

    void setBeforeTask(Long beforeTask);

    @Column(name = "`type`")
    @FieldDef(label="任务类型")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getType();

    void setType(Integer type);

    @Column(name = "`owner`")
    @FieldDef(label="负责人")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getOwner();

    void setOwner(Long owner);

    @Column(name = "`plan_time`")
    @FieldDef(label="计划工时")
    @EditMode(editor = FieldEditor.Text, required = false)
    Short getPlanTime();

    void setPlanTime(Short planTime);

    @Column(name = "`flow`")
    @FieldDef(label="流程")
    @EditMode(editor = FieldEditor.Number, required = false)
    Boolean getFlow();

    void setFlow(Boolean flow);

    @Column(name = "`change_id`")
    @FieldDef(label="changeId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getChangeId();

    void setChangeId(Long changeId);

    @Column(name = "`describe`")
    @FieldDef(label="描述", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDescribe();

    void setDescribe(String describe);

    @Column(name = "`status`")
    @FieldDef(label="status")
    @EditMode(editor = FieldEditor.Text, required = false)
    Byte getStatus();

    void setStatus(Byte status);

    @Column(name = "`created`")
    @FieldDef(label="created")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getCreated();

    void setCreated(Date created);

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