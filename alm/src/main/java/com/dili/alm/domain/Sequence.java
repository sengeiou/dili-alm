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
 * This file was generated on 2018-05-30 17:12:10.
 */
@Table(name = "`sequence`")
public interface Sequence extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`number`")
    @FieldDef(label="number")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getNumber();

    void setNumber(Integer number);

    @Column(name = "`type`")
    @FieldDef(label="type", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getType();

    void setType(String type);
}