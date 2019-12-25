package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;

/**
 * ��MyBatis Generator�����Զ�����
 * 
 * This file was generated on 2019-12-24 18:39:07.
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
     * ������Ŀ���
     */
    @Column(name = "`project_number`")
    private String projectNumber;

    /**
     * �����汾ID
     */
    @Column(name = "`version_id`")
    private Long versionId;

    /**
     * ����״̬״̬{data:[{value:2,text:"�ѹ���"},{value:1,text:"δ����"}]}
     */
    @Column(name = "`status`")
    private Integer status;

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
     * ��ȡ������Ŀ���
     *
     * @return project_number - ������Ŀ���
     */
    @FieldDef(label="������Ŀ���", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getProjectNumber() {
        return projectNumber;
    }

    /**
     * ���ù�����Ŀ���
     *
     * @param projectNumber ������Ŀ���
     */
    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
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
     * ��ȡ����״̬״̬{data:[{value:2,text:"�ѹ���"},{value:1,text:"δ����"}]}
     *
     * @return status - ����״̬״̬{data:[{value:2,text:"�ѹ���"},{value:1,text:"δ����"}]}
     */
    @FieldDef(label="����״̬״̬{data:[{value:2,text:'�ѹ���'},{value:1,text:'δ����'}]}")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Integer getStatus() {
        return status;
    }

    /**
     * ��������״̬״̬{data:[{value:2,text:"�ѹ���"},{value:1,text:"δ����"}]}
     *
     * @param status ����״̬״̬{data:[{value:2,text:"�ѹ���"},{value:1,text:"δ����"}]}
     */
    public void setStatus(Integer status) {
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