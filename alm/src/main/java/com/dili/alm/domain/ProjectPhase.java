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
 * This file was generated on 2017-11-30 16:05:32.
 */
@Table(name = "`project_phase`")
public interface ProjectPhase extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`name`")
    @FieldDef(label="阶段名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getName();

    void setName(String name);

    @Column(name = "`project_id`")
    @FieldDef(label="项目id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`version_id`")
    @FieldDef(label="阶段id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getVersionId();

    void setVersionId(Long versionId);

    @Column(name = "`planned_start_date`")
    @FieldDef(label="计划开始日期")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getPlannedStartDate();

    void setPlannedStartDate(Date plannedStartDate);

    @Column(name = "`planned_end_date`")
    @FieldDef(label="计划结束日期")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getPlannedEndDate();

    void setPlannedEndDate(Date plannedEndDate);

    @Column(name = "`actual_start_date`")
    @FieldDef(label="实际开始日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getActualStartDate();

    void setActualStartDate(Date actualStartDate);

    @Column(name = "`actual_end_date`")
    @FieldDef(label="实际结束日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getActualEndDate();

    void setActualEndDate(Date actualEndDate);

    @Column(name = "`creator_id`")
    @FieldDef(label="创建人")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getCreatorId();

    void setCreatorId(Long creatorId);

    @Column(name = "`modifier_id`")
    @FieldDef(label="修改人")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getModifierId();

    void setModifierId(Long modifierId);

    @Column(name = "`created`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getModified();

    void setModified(Date modified);
}