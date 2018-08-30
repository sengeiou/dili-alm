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
 * This file was generated on 2018-08-27 15:33:50.
 */
@Table(name = "`work_order_execution_record`")
public interface WorkOrderExecutionRecord extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`work_order_id`")
    @FieldDef(label="工单id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getWorkOrderId();

    void setWorkOrderId(Long workOrderId);

    @Column(name = "`task_hours`")
    @FieldDef(label="任务工时")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getTaskHours();

    void setTaskHours(Integer taskHours);

    @Column(name = "`overtime_hours`")
    @FieldDef(label="加班工时")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getOvertimeHours();

    void setOvertimeHours(Integer overtimeHours);

    @Column(name = "`work_content`")
    @FieldDef(label="工作内容", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getWorkContent();

    void setWorkContent(String workContent);

    @Column(name = "`start_date`")
    @FieldDef(label="开始时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getStartDate();

    void setStartDate(Date startDate);

    @Column(name = "`end_date`")
    @FieldDef(label="结束时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getEndDate();

    void setEndDate(Date endDate);
}