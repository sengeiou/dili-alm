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
 * This file was generated on 2017-11-22 16:28:57.
 */
@Table(name = "`log`")
public interface Log extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`CREATED`")
    @FieldDef(label="����ʱ��")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`MODIFIED`")
    @FieldDef(label="�޸�ʱ��")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getModified();

    void setModified(Date modified);

    @Column(name = "`operator_id`")
    @FieldDef(label="����Ա")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getOperatorId();

    void setOperatorId(Long operatorId);

    @Column(name = "`operator_name`")
    @FieldDef(label="����Ա����", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getOperatorName();

    void setOperatorName(String operatorName);

    @Column(name = "`ip`")
    @FieldDef(label="IP", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getIp();

    void setIp(String ip);

    @Column(name = "`content`")
    @FieldDef(label="����")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getContent();

    void setContent(String content);
}