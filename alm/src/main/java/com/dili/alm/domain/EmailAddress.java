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
 * This file was generated on 2018-03-13 15:29:09.
 */
@Table(name = "`email_address`")
public interface EmailAddress extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`email_address`")
    @FieldDef(label="邮件地址", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getEmailAddress();

    void setEmailAddress(String emailAddress);

    @Column(name = "`apply_id`")
    @FieldDef(label="申请id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplyId();

    void setApplyId(Long applyId);

    @Column(name = "`apply_type`")
    @FieldDef(label="申请类型")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getApplyType();

    void setApplyType(Integer applyType);
}