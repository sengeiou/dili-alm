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
 * This file was generated on 2017-12-09 15:41:26.
 */
@Table(name = "`alarm_config`")
public interface AlarmConfig extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`project_id`")
    @FieldDef(label="项目id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`type`")
    @FieldDef(label="告警类型", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getType();

    void setType(String type);

    @Column(name = "`threshold`")
    @FieldDef(label="阀值")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getThreshold();

    void setThreshold(Integer threshold);

    @Column(name = "`planned_end_date`")
    @FieldDef(label="计划结束日期")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getPlannedEndDate();

    void setPlannedEndDate(Date plannedEndDate);
}