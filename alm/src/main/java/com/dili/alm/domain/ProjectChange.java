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
 * This file was generated on 2018-09-05 15:23:12.
 */
@Table(name = "`project_change`")
public interface ProjectChange extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`name`")
    @FieldDef(label="变更名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getName();

    void setName(String name);

    @Column(name = "`number`")
    @FieldDef(label="变更编号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNumber();

    void setNumber(String number);

    @Column(name = "`project_name`")
    @FieldDef(label="项目名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProjectName();

    void setProjectName(String projectName);

    @Column(name = "`project_id`")
    @FieldDef(label="变更项目")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`type`")
    @FieldDef(label="type", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getType();

    void setType(String type);

    @Column(name = "`working_hours`")
    @FieldDef(label="预估工时", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getWorkingHours();

    void setWorkingHours(String workingHours);

    @Column(name = "`affects_online`")
    @FieldDef(label="是否影响上线")
    @EditMode(editor = FieldEditor.Text, required = false)
    Integer getAffectsOnline();

    void setAffectsOnline(Integer affectsOnline);

    @Column(name = "`submit_date`")
    @FieldDef(label="提交日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getSubmitDate();

    void setSubmitDate(Date submitDate);

    @Column(name = "`content`")
    @FieldDef(label="content", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getContent();

    void setContent(String content);

    @Column(name = "`effects`")
    @FieldDef(label="影响说明", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getEffects();

    void setEffects(String effects);

    @Column(name = "`email`")
    @FieldDef(label="email", maxLength = 1000)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getEmail();

    void setEmail(String email);

    @Column(name = "`status`")
    @FieldDef(label="status")
    @EditMode(editor = FieldEditor.Text, required = false)
    Integer getStatus();

    void setStatus(Integer status);

    @Column(name = "`created`")
    @FieldDef(label="created")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="modified")
    @EditMode(editor = FieldEditor.Datetime, required = true)
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

    @Column(name = "`estimate_launch_date`")
    @FieldDef(label="estimateLaunchDate")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getEstimateLaunchDate();

    void setEstimateLaunchDate(Date estimateLaunchDate);

    @Column(name = "`reStatus`")
    @FieldDef(label="restatus")
    @EditMode(editor = FieldEditor.Text, required = false)
    Integer getRestatus();

    void setRestatus(Integer restatus);

    @Column(name = "`end_date`")
    @FieldDef(label="endDate")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getEndDate();

    void setEndDate(Date endDate);
}