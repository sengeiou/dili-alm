package com.dili.alm.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-07-09 11:26:38.
 */
@Table(name = "`data_auth_ref`")
public interface DataAuthRef extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`name`")
    @FieldDef(label="权限名称", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getName();

    void setName(String name);

    @Column(name = "`code`")
    @FieldDef(label="权限编码", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCode();

    void setCode(String code);

    @Column(name = "`spring_id`")
    @FieldDef(label="获取数据的springBeanId，实现xxx接口", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getSpringId();

    void setSpringId(String springId);

    @Column(name = "`param`")
    @FieldDef(label="json参数", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getParam();

    void setParam(String param);
}