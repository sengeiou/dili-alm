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
 * ��MyBatis Generator�����Զ�����
 * 
 * This file was generated on 2017-11-22 16:02:20.
 */
@Table(name = "`task_execution_info`")
public interface TaskExecutionInfo extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`task_id`")
    @FieldDef(label="����id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getTaskId();

    void setTaskId(Long taskId);

    @Column(name = "`work_hours`")
    @FieldDef(label="����ʱ")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getWorkHours();

    void setWorkHours(Integer workHours);

    @Column(name = "`overtime_work_hours`")
    @FieldDef(label="�Ӱ๤ʱ")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getOvertimeWorkHours();

    void setOvertimeWorkHours(Integer overtimeWorkHours);

    @Column(name = "`work_content`")
    @FieldDef(label="��������", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getWorkContent();

    void setWorkContent(String workContent);
}