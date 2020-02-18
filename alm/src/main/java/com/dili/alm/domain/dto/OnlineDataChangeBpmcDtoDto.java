package com.dili.alm.domain.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.dili.alm.component.ProcessHandleInfoDto;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.Project;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

public    class OnlineDataChangeBpmcDtoDto extends  OnlineDataChange implements  ProcessHandleInfoDto{

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private   Boolean handleProcess;
    
    private  String formKey;
    
    private  String taskId;
    
    private  Boolean isNeedClaim;
    
    private Long projectManager;
    
    private Long testManager;
  
    private List<Long> dbaManager;	

    private List<Long> onlineManager;	
    
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

	public Boolean getHandleProcess() {
		return handleProcess;
	}

	public void setHandleProcess(Boolean handleProcess) {
		this.handleProcess = handleProcess;
	}

	public Long getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(Long projectManager) {
		this.projectManager = projectManager;
	}

	

	public Long getTestManager() {
		return testManager;
	}

	public void setTestManager(Long testManager) {
		this.testManager = testManager;
	}

	public List<Long> getDbaManager() {
		return dbaManager;
	}

	public void setDbaManager(List<Long> dbaManager) {
		this.dbaManager = dbaManager;
	}

	public List<Long> getOnlineManager() {
		return onlineManager;
	}

	public void setOnlineManager(List<Long> onlineManager) {
		this.onlineManager = onlineManager;
	}


	
}
