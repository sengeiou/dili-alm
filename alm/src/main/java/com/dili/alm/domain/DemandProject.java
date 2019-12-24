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
 * This file was generated on 2019-12-23 17:47:02.
 */
@Table(name = "`demand_project`")
public interface DemandProject extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="自增id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`demand_id`")
    @FieldDef(label="需求ID")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getDemandId();

    void setDemandId(Long demandId);

    @Column(name = "`project_id`")
    @FieldDef(label="关联项目ID")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`version_id`")
    @FieldDef(label="关联版本ID")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getVersionId();

    void setVersionId(Long versionId);

    @Column(name = "`status`")
    @FieldDef(label="需求状态")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getStatus();

    void setStatus(Long status);

    @Column(name = "`work_order_id`")
    @FieldDef(label="关联工单id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getWorkOrderId();

    void setWorkOrderId(Long workOrderId);
}