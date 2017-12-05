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
 * This file was generated on 2017-12-05 15:56:01.
 */
@Table(name = "`role`")
public interface Role extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="主键")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`role_name`")
    @FieldDef(label="角色名", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getRoleName();

    void setRoleName(String roleName);

    @Column(name = "`description`")
    @FieldDef(label="角色描述", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getDescription();

    void setDescription(String description);

    @Column(name = "`created`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getModified();

    void setModified(Date modified);

    @Column(name = "`rank`")
    @FieldDef(label="职级", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getRank();

    void setRank(String rank);
}