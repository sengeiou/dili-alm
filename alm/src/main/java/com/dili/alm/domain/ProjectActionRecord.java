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
 * This file was generated on 2018-06-12 14:36:05.
 */
@Table(name = "`project_action_record`")
public interface ProjectActionRecord extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`action_code`")
    @FieldDef(label="操作名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getActionCode();

    void setActionCode(String actionCode);

    @Column(name = "`action_start_date`")
    @FieldDef(label="事件开始时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getActionStartDate();

    void setActionStartDate(Date actionStartDate);

    @Column(name = "`action_end_date`")
    @FieldDef(label="事件结束时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getActionEndDate();

    void setActionEndDate(Date actionEndDate);

    @Column(name = "`action_date`")
    @FieldDef(label="事件发生时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getActionDate();

    void setActionDate(Date actionDate);

    @Column(name = "`action_date_type`")
    @FieldDef(label="事件时间类型，时间段（period）或时间点（point）")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getActionDateType();

    void setActionDateType(Integer actionDateType);

    @Column(name = "`project_id`")
    @FieldDef(label="项目id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`version_Id`")
    @FieldDef(label="版本id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getVersionId();

    void setVersionId(Long versionId);

    @Column(name = "`action_type`")
    @FieldDef(label="类型，1项目，2版本")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getActionType();

    void setActionType(Integer actionType);
}