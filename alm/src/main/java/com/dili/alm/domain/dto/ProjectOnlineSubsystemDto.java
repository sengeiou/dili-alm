package com.dili.alm.domain.dto;

import com.dili.alm.domain.ProjectOnlineSubsystem;
import com.dili.ss.domain.BaseDomain;

public class ProjectOnlineSubsystemDto extends BaseDomain implements ProjectOnlineSubsystem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3476794797625312791L;
	private Long applyId;
	private Long sqlFileId;
	private String git;
	private Long startupScriptFileId;
	private String dependencySystem;
	private String otherDescription;
	private String projectName;
	private Long projectId;
	private Long managerId;
	private String branch;
	private Long dependencySystemFileId;

	@Override
	public Long getApplyId() {
		return applyId;
	}

	@Override
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	@Override
	public String getGit() {
		return git;
	}

	@Override
	public void setGit(String git) {
		this.git = git;
	}

	@Override
	public Long getSqlFileId() {
		return sqlFileId;
	}

	@Override
	public void setSqlFileId(Long sqlFileId) {
		this.sqlFileId = sqlFileId;
	}

	@Override
	public Long getStartupScriptFileId() {
		return startupScriptFileId;
	}

	@Override
	public void setStartupScriptFileId(Long startupScriptFileId) {
		this.startupScriptFileId = startupScriptFileId;
	}

	@Override
	public String getDependencySystem() {
		return dependencySystem;
	}

	@Override
	public void setDependencySystem(String dependencySystem) {
		this.dependencySystem = dependencySystem;
	}

	@Override
	public String getOtherDescription() {
		return otherDescription;
	}

	@Override
	public void setOtherDescription(String otherDescription) {
		this.otherDescription = otherDescription;
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
	public String getBranch() {
		return branch;
	}

	@Override
	public void setBranch(String branch) {
		this.branch = branch;
	}

	@Override
	public Long getDependencySystemFileId() {
		return dependencySystemFileId;
	}

	@Override
	public void setDependencySystemFileId(Long dependencySystemFileId) {
		this.dependencySystemFileId = dependencySystemFileId;
	}

}
