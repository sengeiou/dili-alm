package com.dili.alm.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

import javax.persistence.*;
import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-11-28 10:09:19.
 */
@Table(name = "`department`")
public interface Department extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`name`")
    @FieldDef(label="部门名", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getName();

    void setName(String name);

    @Column(name = "`code`")
    @FieldDef(label="编号", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCode();

    void setCode(String code);

    @Column(name = "`operator_id`")
    @FieldDef(label="操作员id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getOperatorId();

    void setOperatorId(Long operatorId);

    @Column(name = "`notes`")
    @FieldDef(label="备注", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNotes();

    void setNotes(String notes);

    @Column(name = "`parent_id`")
    @FieldDef(label="parentId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getParentId();

    void setParentId(Long parentId);

    @Column(name = "`created`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="操作时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getModified();

    void setModified(Date modified);
}