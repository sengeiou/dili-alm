package com.dili.alm.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-12-05 20:20:16.
 */
@Table(name = "`weekly_details`")
public interface WeeklyDetails extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`overall_progress`")
    @FieldDef(label="总体进展", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getOverallProgress();

    void setOverallProgress(String overallProgress);

    @Column(name = "`project_description`")
    @FieldDef(label="项目总体情况描述", maxLength = 500)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProjectDescription();

    void setProjectDescription(String projectDescription);

    @Column(name = "`expected_deviation`")
    @FieldDef(label="预期偏差", maxLength = 100)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getExpectedDeviation();

    void setExpectedDeviation(String expectedDeviation);

    @Column(name = "`other`")
    @FieldDef(label="其他", maxLength = 100)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getOther();

    void setOther(String other);

    @Column(name = "`weekly_id`")
    @FieldDef(label="weeklyId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getWeeklyId();

    void setWeeklyId(Long weeklyId);
}