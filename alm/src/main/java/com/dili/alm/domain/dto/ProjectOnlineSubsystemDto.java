package com.dili.alm.domain.dto;

import com.dili.alm.domain.ProjectOnlineSubsystem;
import com.dili.ss.domain.BaseDomain;

public class ProjectOnlineSubsystemDto extends BaseDomain implements ProjectOnlineSubsystem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3476794797625312791L;
	private Long applyId;
	private String projectName;
	private Long projectId;
	private Long managerId;
	private String managerName;

	@Override
	public Long getApplyId() {
		return applyId;
	}

	@Override
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	@Override
	public Long getProjectId() {
		return projectId;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public Long getManagerId() {
		return managerId;
	}

	@Override
	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	@Override
	public String getManagerName() {
		return managerName;
	}

	@Override
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

}
