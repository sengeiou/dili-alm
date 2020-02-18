package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2019-12-24 18:39:07.
 */
@Table(name = "`demand_project`")
public class DemandProject extends BaseDomain {
    /**
     * 自增id
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 需求ID
     */
    @Column(name = "`demand_id`")
    private Long demandId;

    /**
     * 关联项目编号
     */
    @Column(name = "`project_number`")
    private String projectNumber;

    /**
     * 关联版本ID
     */
    @Column(name = "`version_id`")
    private Long versionId;

    /**
     * 需求状态状态{data:[{value:2,text:"已关联"},{value:1,text:"未关联"}]}
     */
    @Column(name = "`status`")
    private int status;

    /**
     * 关联工单id
     */
    @Column(name = "`work_order_id`")
    private Long workOrderId;

    /**
     * 获取自增id
     *
     * @return id - 自增id
     */
    @FieldDef(label="自增id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * 设置自增id
     *
     * @param id 自增id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取需求ID
     *
     * @return demand_id - 需求ID
     */
    @FieldDef(label="需求ID")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getDemandId() {
        return demandId;
    }

    /**
     * 设置需求ID
     *
     * @param demandId 需求ID
     */
    public void setDemandId(Long demandId) {
        this.demandId = demandId;
    }

    /**
     * 获取关联项目编号
     *
     * @return project_number - 关联项目编号
     */
    @FieldDef(label="关联项目编号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getProjectNumber() {
        return projectNumber;
    }

    /**
     * 设置关联项目编号
     *
     * @param projectNumber 关联项目编号
     */
    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    /**
     * 获取关联版本ID
     *
     * @return version_id - 关联版本ID
     */
    @FieldDef(label="关联版本ID")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getVersionId() {
        return versionId;
    }

    /**
     * 设置关联版本ID
     *
     * @param versionId 关联版本ID
     */
    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    /**
     * 获取需求状态状态{data:[{value:2,text:"已关联"},{value:1,text:"未关联"}]}
     *
     * @return status - 需求状态状态{data:[{value:2,text:"已关联"},{value:1,text:"未关联"}]}
     */
    @FieldDef(label="需求状态状态{data:[{value:2,text:'已关联'},{value:1,text:'未关联'}]}")
    @EditMode(editor = FieldEditor.Number, required = true)
    public int getStatus() {
        return status;
    }

    /**
     * 设置需求状态状态{data:[{value:2,text:"已关联"},{value:1,text:"未关联"}]}
     *
     * @param status 需求状态状态{data:[{value:2,text:"已关联"},{value:1,text:"未关联"}]}
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获取关联工单id
     *
     * @return work_order_id - 关联工单id
     */
    @FieldDef(label="关联工单id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getWorkOrderId() {
        return workOrderId;
    }

    /**
     * 设置关联工单id
     *
     * @param workOrderId 关联工单id
     */
    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }
}