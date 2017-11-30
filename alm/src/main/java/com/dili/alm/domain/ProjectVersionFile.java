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
 * This file was generated on 2017-11-28 16:31:47.
 */
@Table(name = "`project_version_file`")
public interface ProjectVersionFile extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`file_id`")
    @FieldDef(label="文件id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getFileId();

    void setFileId(Long fileId);

    @Column(name = "`project_version_id`")
    @FieldDef(label="项目版本id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getProjectVersionId();

    void setProjectVersionId(Long projectVersionId);
}