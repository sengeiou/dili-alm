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
 * This file was generated on 2018-03-13 15:30:51.
 */
@Table(name = "`online_system`")
public interface OnlineSystem extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`apply_id`")
    @FieldDef(label="上线申请id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplyId();

    void setApplyId(Long applyId);

    @Column(name = "`git`")
    @FieldDef(label="git地址", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getGit();

    void setGit(String git);

    @Column(name = "`sql_file_id`")
    @FieldDef(label="sql脚本文件id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getSqlFileId();

    void setSqlFileId(Long sqlFileId);

    @Column(name = "`startup_script_file_id`")
    @FieldDef(label="启动脚本文件id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getStartupScriptFileId();

    void setStartupScriptFileId(Long startupScriptFileId);

    @Column(name = "`dependency_system`")
    @FieldDef(label="依赖系统", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDependencySystem();

    void setDependencySystem(String dependencySystem);

    @Column(name = "`other_description`")
    @FieldDef(label="其他说明", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getOtherDescription();

    void setOtherDescription(String otherDescription);
}