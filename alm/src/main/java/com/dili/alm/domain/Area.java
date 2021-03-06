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
 * This file was generated on 2018-04-27 09:44:45.
 */
@Table(name = "`area`")
public interface Area extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="区域主键")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`area_name`")
    @FieldDef(label="区域名称", maxLength = 16)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getAreaName();

    void setAreaName(String areaName);

    @Column(name = "`area_code`")
    @FieldDef(label="区域代码", maxLength = 128)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getAreaCode();

    void setAreaCode(String areaCode);

    @Column(name = "`parent_id`")
    @FieldDef(label="上级主键")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getParentId();

    void setParentId(Long parentId);
}