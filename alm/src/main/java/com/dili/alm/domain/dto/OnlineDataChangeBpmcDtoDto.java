package com.dili.alm.domain.dto;

import java.util.Date;

import com.dili.alm.component.ProcessHandleInfoDto;
import com.dili.alm.domain.OnlineDataChange;

public    class OnlineDataChangeBpmcDtoDto extends  OnlineDataChange implements  ProcessHandleInfoDto{

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private Long id;

    private Long applyUserId;
    
    private Byte isSynchronization;
    
    private Byte isSubmit;
   
    private String applyMarketId;

    private Long projectId;

    private Long versionId;

    private Date applyDate;

    private Date updateDate;

    private String dbaName;

    private String sqlScript;

    private String sqlFileId;
    
    private Byte dataStatus;
	
    private String processInstanceId;
    
    private   Boolean handleProcess;
    
    private  String formKey;
    
    private  String taskId;
    
    private  Boolean isNeedClaim;
    
	@Override
	public String getProcessDefinitionId() {
		return processInstanceId;
	}

	@Override
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processInstanceId=processDefinitionId;
		
	}

	@Override
	public Boolean getIsHandleProcess() {

		return handleProcess;
	}

	@Override
	public void setIsHandleProcess(Boolean isHandleProcess) {
		this.handleProcess=isHandleProcess;
		
	}

	@Override
	public String getFormKey() {
	
		return formKey;
	}

	@Override
	public void setFormKey(String formKey) {
		this.formKey=formKey;
		
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public void setTaskId(String taskId) {
	 this.taskId=taskId;
		
	}

	@Override
	public Boolean getIsNeedClaim() {
		return isNeedClaim;
	}

	@Override
	public void setIsNeedClaim(Boolean isNeedClaim) {
		this.isNeedClaim=isNeedClaim;
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getApplyUserId() {
		return applyUserId;
	}

	public void setApplyUserId(Long applyUserId) {
		this.applyUserId = applyUserId;
	}

	public Byte getIsSynchronization() {
		return isSynchronization;
	}

	public void setIsSynchronization(Byte isSynchronization) {
		this.isSynchronization = isSynchronization;
	}

	public Byte getIsSubmit() {
		return isSubmit;
	}

	public void setIsSubmit(Byte isSubmit) {
		this.isSubmit = isSubmit;
	}

	public String getApplyMarketId() {
		return applyMarketId;
	}

	public void setApplyMarketId(String applyMarketId) {
		this.applyMarketId = applyMarketId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDbaName() {
		return dbaName;
	}

	public void setDbaName(String dbaName) {
		this.dbaName = dbaName;
	}

	public String getSqlScript() {
		return sqlScript;
	}

	public void setSqlScript(String sqlScript) {
		this.sqlScript = sqlScript;
	}

	public String getSqlFileId() {
		return sqlFileId;
	}

	public void setSqlFileId(String sqlFileId) {
		this.sqlFileId = sqlFileId;
	}

	public Byte getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(Byte dataStatus) {
		this.dataStatus = dataStatus;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public Boolean getHandleProcess() {
		return handleProcess;
	}

	public void setHandleProcess(Boolean handleProcess) {
		this.handleProcess = handleProcess;
	}

	
}
