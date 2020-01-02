package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;

/**
 * ��MyBatis Generator�����Զ�����
 * 
 * This file was generated on 2019-12-25 18:22:44.
 */
@Table(name = "`online_data_change`")
public class OnlineDataChange extends BaseDomain {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`apply_user_id`")
    private Long applyUserId;
    
    @Column(name = "`is_synchronization`")
    private Byte isSynchronization;
    

    /**
     * �г�id
     */
    @Column(name = "`apply_market_id`")
    private String applyMarketId;

    /**
     * ��Ŀid
     */
    @Column(name = "`project_id`")
    private Long projectId;

    @Column(name = "`version_id`")
    private Long versionId;

    @Column(name = "`apply_date`")
    private Date applyDate;

    @Column(name = "`update_date`")
    private Date updateDate;

    /**
     * ���ݿ���
     */
    @Column(name = "`dba_name`")
    private String dbaName;

    @Column(name = "`sql_script`")
    private String sqlScript;

    @Column(name = "`sql_file_id`")
    private String sqlFileId;

    /**
     * @return id
     */
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return apply_user_id
     */
    @FieldDef(label="applyUserId")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getApplyUserId() {
        return applyUserId;
    }

    /**
     * @param applyUserId
     */
    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    /**
     * ��ȡ�г�id
     *
     * @return apply_market_id - �г�id
     */
    @FieldDef(label="�г�id")
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getApplyMarketId() {
        return applyMarketId;
    }

    /**
     * �����г�id
     *
     * @param applyMarketId �г�id
     */
    public void setApplyMarketId(String applyMarketId) {
        this.applyMarketId = applyMarketId;
    }

    /**
     * ��ȡ��Ŀid
     *
     * @return project_id - ��Ŀid
     */
    @FieldDef(label="��Ŀid")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getProjectId() {
        return projectId;
    }

    /**
     * ������Ŀid
     *
     * @param projectId ��Ŀid
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * @return version_id
     */
    @FieldDef(label="versionId")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getVersionId() {
        return versionId;
    }

    /**
     * @param versionId
     */
    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    /**
     * @return apply_date
     */
    @FieldDef(label="applyDate")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public Date getApplyDate() {
        return applyDate;
    }

    /**
     * @param applyDate
     */
    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    /**
     * @return update_date
     */
    @FieldDef(label="updateDate")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * ��ȡ���ݿ���
     *
     * @return dba_name - ���ݿ���
     */
    @FieldDef(label="���ݿ���", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getDbaName() {
        return dbaName;
    }

    /**
     * �������ݿ���
     *
     * @param dbaName ���ݿ���
     */
    public void setDbaName(String dbaName) {
        this.dbaName = dbaName;
    }

    /**
     * @return sql_script
     */
    @FieldDef(label="sqlScript", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getSqlScript() {
        return sqlScript;
    }

    /**
     * @param sqlScript
     */
    public void setSqlScript(String sqlScript) {
        this.sqlScript = sqlScript;
    }

    /**
     * @return sql_file_id
     */
    @FieldDef(label="sqlFileId", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getSqlFileId() {
        return sqlFileId;
    }

    /**
     * @param sqlFileId
     */
    public void setSqlFileId(String sqlFileId) {
        this.sqlFileId = sqlFileId;
    }
    
    /**
     * @return is_synchronization
     */
    @FieldDef(label="isSynchronization", maxLength = 2)
    @EditMode(editor = FieldEditor.Number ,required = false)
    public Byte getIsSynchronization() {
        return isSynchronization;
    }

    /**
     * @param isSynchronization
     */
    public void setIsSynchronization(Byte isSynchronization) {
        this.isSynchronization = isSynchronization;
    }
    

}