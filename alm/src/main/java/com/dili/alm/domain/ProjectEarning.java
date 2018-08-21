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
 * This file was generated on 2018-08-20 14:34:32.
 */
@Table(name = "`project_earning`")
public interface ProjectEarning extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`apply_id`")
    @FieldDef(label="申请id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplyId();

    void setApplyId(Long applyId);

    @Column(name = "`roi_id`")
    @FieldDef(label="roiId")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getRoiId();

    void setRoiId(Long roiId);

    @Column(name = "`indicator_name`")
    @FieldDef(label="指标名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getIndicatorName();

    void setIndicatorName(String indicatorName);

    @Column(name = "`indicator_current_status`")
    @FieldDef(label="指标现状", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getIndicatorCurrentStatus();

    void setIndicatorCurrentStatus(String indicatorCurrentStatus);

    @Column(name = "`project_objective`")
    @FieldDef(label="项目目标", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProjectObjective();

    void setProjectObjective(String projectObjective);

    @Column(name = "`implemetion_date`")
    @FieldDef(label="实现日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getImplemetionDate();

    void setImplemetionDate(Date implemetionDate);

    @Column(name = "`indicator_type`")
    @FieldDef(label="指标类型")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getIndicatorType();

    void setIndicatorType(Integer indicatorType);
}