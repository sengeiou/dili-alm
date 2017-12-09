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
 * This file was generated on 2017-12-09 16:40:20.
 */
@Table(name = "`verify_approval`")
public interface VerifyApproval extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`ID`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`approve_id`")
    @FieldDef(label="审批编号")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getApproveId();

    void setApproveId(Long approveId);

    @Column(name = "`approver`")
    @FieldDef(label="approver")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getApprover();

    void setApprover(Long approver);

    @Column(name = "`result`")
    @FieldDef(label="result", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getResult();

    void setResult(String result);

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