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
 * This file was generated on 2019-12-23 17:32:14.
 */
@Table(name = "`demand`")
public interface Demand extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="�������")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`serial_number`")
    @FieldDef(label="�����ţ���������ĸ+������+ʱ��+001��������", maxLength = 25)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getSerialNumber();

    void setSerialNumber(String serialNumber);

    @Column(name = "`name`")
    @FieldDef(label="��������", maxLength = 25)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getName();

    void setName(String name);

    @Column(name = "`user_id`")
    @FieldDef(label="����������")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getUserId();

    void setUserId(Long userId);

    @Column(name = "`belong_pro_id`")
    @FieldDef(label="����ϵͳ")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getBelongProId();

    void setBelongProId(Long belongProId);

    @Column(name = "`type`")
    @FieldDef(label="�������ͣ������ֵ䣩")
    @EditMode(editor = FieldEditor.Text, required = true)
    Byte getType();

    void setType(Byte type);

    @Column(name = "`status`")
    @FieldDef(label="����״̬�������ֵ䣩")
    @EditMode(editor = FieldEditor.Text, required = true)
    Byte getStatus();

    void setStatus(Byte status);

    @Column(name = "`finish_date`")
    @FieldDef(label="��������ʵ��ʱ��(������)")
    @EditMode(editor = FieldEditor.Date, required = true)
    Date getFinishDate();

    void setFinishDate(Date finishDate);

    @Column(name = "`document_url`")
    @FieldDef(label="��ظ���", maxLength = 120)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDocumentUrl();

    void setDocumentUrl(String documentUrl);

    @Column(name = "`create_date`")
    @FieldDef(label="����ʱ��")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreateDate();

    void setCreateDate(Date createDate);

    @Column(name = "`submit_date`")
    @FieldDef(label="�ύʱ�䣨�������̣�")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getSubmitDate();

    void setSubmitDate(Date submitDate);

    @Column(name = "`content`")
    @FieldDef(label="��������")
    @EditMode(editor = FieldEditor.Text, required = true)
    String getContent();

    void setContent(String content);

    @Column(name = "`reason`")
    @FieldDef(label="���󱳾�")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getReason();

    void setReason(String reason);
}