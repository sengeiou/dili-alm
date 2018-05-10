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
 * This file was generated on 2018-03-22 14:52:13.
 */
@Table(name = "`work_day`")
public interface WorkDay extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`word_day_week`")
    @FieldDef(label="工作日每年度周数")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getWordDayWeek();

    void setWordDayWeek(Integer wordDayWeek);

    @Column(name = "`work_day_year`")
    @FieldDef(label="工作日年度", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getWorkDayYear();

    void setWorkDayYear(String workDayYear);

    @Column(name = "`work_start_time`")
    @FieldDef(label="工作日开始日期")
    @EditMode(editor = FieldEditor.Date, required = false)
    Date getWorkStartTime();

    void setWorkStartTime(Date workStartTime);

    @Column(name = "`work_end_time`")
    @FieldDef(label="工作日结束日期")
    @EditMode(editor = FieldEditor.Date, required = false)
    Date getWorkEndTime();

    void setWorkEndTime(Date workEndTime);
}