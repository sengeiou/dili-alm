package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;

/**
 * ��MyBatis Generator�����Զ�����
 * 
 * This file was generated on 2019-12-24 10:21:19.
 */
@Table(name = "`demand_project`")
public class DemandProject extends BaseDomain {
    /**
     * ����id
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ����ID
     */
    @Column(name = "`demand_id`")
    private Long demandId;

    /**
     * ������ĿID
     */
    @Column(name = "`project_id`")
    private Long projectId;

    /**
     * �����汾ID
     */
    @Column(name = "`version_id`")
    private Long versionId;

    /**
     * ����״̬
     */
    @Column(name = "`status`")
    private Long status;

    /**
     * ��������id
     */
    @Column(name = "`work_order_id`")
    private Long workOrderId;

    /**
     * ��ȡ����id
     *
     * @return id - ����id
     */
    @FieldDef(label="����id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * ��������id
     *
     * @param id ����id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * ��ȡ����ID
     *
     * @return demand_id - ����ID
     */
    @FieldDef(label="����ID")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getDemandId() {
        return demandId;
    }

    /**
     * ��������ID
     *
     * @param demandId ����ID
     */
    public void setDemandId(Long demandId) {
        this.demandId = demandId;
    }

    /**
     * ��ȡ������ĿID
     *
     * @return project_id - ������ĿID
     */
    @FieldDef(label="������ĿID")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getProjectId() {
        return projectId;
    }

    /**
     * ���ù�����ĿID
     *
     * @param projectId ������ĿID
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * ��ȡ�����汾ID
     *
     * @return version_id - �����汾ID
     */
    @FieldDef(label="�����汾ID")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getVersionId() {
        return versionId;
    }

    /**
     * ���ù����汾ID
     *
     * @param versionId �����汾ID
     */
    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    /**
     * ��ȡ����״̬
     *
     * @return status - ����״̬
     */
    @FieldDef(label="����״̬")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getStatus() {
        return status;
    }

    /**
     * ��������״̬
     *
     * @param status ����״̬
     */
    public void setStatus(Long status) {
        this.status = status;
    }

    /**
     * ��ȡ��������id
     *
     * @return work_order_id - ��������id
     */
    @FieldDef(label="��������id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getWorkOrderId() {
        return workOrderId;
    }

    /**
     * ���ù�������id
     *
     * @param workOrderId ��������id
     */
    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }
}