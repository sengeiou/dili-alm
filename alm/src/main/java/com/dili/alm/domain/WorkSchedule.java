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
 * This file was generated on 2017-11-22 16:28:58.
 */
@Table(name = "`work_schedule`")
public interface WorkSchedule extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="����")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`schedule_date`")
    @FieldDef(label="�༭ʱ��")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getScheduleDate();

    void setScheduleDate(Date scheduleDate);

    @Column(name = "`schedule_text`")
    @FieldDef(label="�༭����", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getScheduleText();

    void setScheduleText(String scheduleText);

    @Column(name = "`user_id`")
    @FieldDef(label="�û�id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getUserId();

    void setUserId(Long userId);
}