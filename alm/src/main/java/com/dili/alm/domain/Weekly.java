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
 * This file was generated on 2017-11-30 12:37:16.
 */
@Table(name = "`weekly`")
public interface Weekly extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`project_id`")
    @FieldDef(label="projectId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectId();

    void setProjectId(Long projectId);

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

    @Column(name = "`risk`")
    @FieldDef(label="风险")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getRisk();

    void setRisk(String risk);
    
    @Column(name = "`next_week`")
    @FieldDef(label="下周")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNextWeek();

    void setNextWeek(String nextWeek);
    
    
    @Column(name = "`current_week`")
    @FieldDef(label="本周")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCurrentWeek();
    void setCurrentWeek(String currentWeek);
    

    @Column(name = "`question`")
    @FieldDef(label="问题")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getQuestion();

    void setQuestion(String question);

    @Column(name = "`progress`")
    @FieldDef(label="进度", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProgress();

    void setProgress(String progress);

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