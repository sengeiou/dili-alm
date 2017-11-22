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
 * ��MyBatis Generator�����Զ�����
 * 
 * This file was generated on 2017-11-22 16:02:19.
 */
@Table(name = "`task_details`")
public interface TaskDetails extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`task_id`")
    @FieldDef(label="taskId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getTaskId();

    void setTaskId(Long taskId);

    @Column(name = "`task_hour`")
    @FieldDef(label="��ʱ")
    @EditMode(editor = FieldEditor.Text, required = false)
    Short getTaskHour();

    void setTaskHour(Short taskHour);

    @Column(name = "`over_hour`")
    @FieldDef(label="�Ӱ๤ʱ")
    @EditMode(editor = FieldEditor.Text, required = false)
    Short getOverHour();

    void setOverHour(Short overHour);

    @Column(name = "`describe`")
    @FieldDef(label="��������", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDescribe();

    void setDescribe(String describe);

    @Column(name = "`task_time`")
    @FieldDef(label="��ʱ��дʱ��", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getTaskTime();

    void setTaskTime(String taskTime);

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