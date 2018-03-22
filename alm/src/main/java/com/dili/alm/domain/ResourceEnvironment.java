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
 * This file was generated on 2018-03-21 11:40:02.
 */
@Table(name = "`resource_environment`")
public interface ResourceEnvironment extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`resource_id`")
    @FieldDef(label="硬件资源id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getResourceId();

    void setResourceId(Long resourceId);

    @Column(name = "`environment`")
    @FieldDef(label="环境")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getEnvironment();

    void setEnvironment(Integer environment);
}