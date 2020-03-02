package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2019-12-25 18:22:44.
 */
@Table(name = "`online_data_change`")
public class OnlineDataChange extends BaseDomain {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`apply_user_id`")
    private Long applyUserId;
    
    @Column(name = "`is_synchronization`")
    private Byte isSynchronization;
    
    @Column(name = "is_submit")
    private Byte isSubmit;
    

    /**
     * 市场id
     */
    @Column(name = "`apply_market_id`")
    private String applyMarketId;

    /**
     * 项目id
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
     *
     */
    @Column(name = "`dba_name`")
    private String dbaName;

    @Column(name = "`sql_script`")
    private String sqlScript;

    @Column(name = "`sql_file_id`")
    private String sqlFileId;
    
    @Column(name = "data_status")
    private Byte dataStatus;
    
    @Column(name = "`process_definition_id`")
    private String processDefinitionId;
    
    
    @Column(name = "`process_instance_id`")
    private String processInstanceId;
    
    @Column(name = "`create_date`")
    private Date createDate;
    
    
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
     * 市场id
     *
     * @return apply_market_id - �г�id
     */
    @FieldDef(label="�г�id")
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getApplyMarketId() {
        return applyMarketId;
    }

    /**
     * 市场id
     *
     * @param applyMarketId 市场id
     */
    public void setApplyMarketId(String applyMarketId) {
        this.applyMarketId = applyMarketId;
    }

    /**
     * 项目id
     *
     * @return project_id -项目Ŀid
     */
    @FieldDef(label="项目id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getProjectId() {
        return projectId;
    }

    /**
     * 项目id
     *
     * @param projectId 项目id
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
     * 数据库名字
     *
     * @return dba_name - 数据库名字
     */
    @FieldDef(label="数据库名字", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getDbaName() {
        return dbaName;
    }

    /**
     * 数据库名字
     *
     * @param dbaName数据库名字
     */
    public void setDbaName(String dbaName) {
        this.dbaName = dbaName;
    }

    /**
     * @return sql_script
     */
    @FieldDef(label="sqlScript", maxLength = 250)
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
    /**
     * @return is_submit
     */
    @FieldDef(label="isSubmit", maxLength = 2)
    @EditMode(editor = FieldEditor.Number ,required = false)
    public Byte getIsSubmit() {
        return isSubmit;
    }

    /**
     * @param isSubmit
     */
    public void setIsSubmit(Byte isSubmit) {
        this.isSubmit = isSubmit;
    }
    
    /**
     * @return data_status
     */
    @FieldDef(label="dataStatus", maxLength = 2)
    @EditMode(editor = FieldEditor.Number ,required = false)
    public Byte getDataStatus() {
        return dataStatus;
    }

    /**
     * @param dataStatus
     */
    public void setDataStatus(Byte dataStatus) {
        this.dataStatus = dataStatus;
    }

    
    /**
     * 
     *
     *
     */
    @FieldDef(label="流程定义id", maxLength = 80)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    /**
     *
     */
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    
    /**
     * 
     * @return processInstanceId 
     */
    @FieldDef(label="流程实例id", maxLength = 80)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /**
 
     */
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    
    /**
     * @return createDate
     */
    @FieldDef(label="createDate")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    
    
}