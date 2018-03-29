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
 * This file was generated on 2018-03-28 16:43:08.
 */
@Table(name = "`project_online_subsystem`")
public interface ProjectOnlineSubsystem extends IBaseDomain {
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

    @Column(name = "`project_id`")
    @FieldDef(label="上线子系统id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`project_name`")
    @FieldDef(label="上线子系统名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getProjectName();

    void setProjectName(String projectName);

    @Column(name = "`manager_id`")
    @FieldDef(label="负责人id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getManagerId();

    void setManagerId(Long managerId);

    @Column(name = "`manager_name`")
    @FieldDef(label="负责人姓名", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getManagerName();

    void setManagerName(String managerName);
}