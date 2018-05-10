package com.dili.alm.domain.dto;

public class ProjectStatusCountDto {
	
	private String projectType;
	
	private Integer projectState;
	
	private int stateCount;

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public Integer getProjectState() {
		return projectState;
	}

	public void setProjectState(Integer projectState) {
		this.projectState = projectState;
	}

	public int getStateCount() {
		return stateCount;
	}

	public void setStateCount(int stateCount) {
		this.stateCount = stateCount;
	}
	
}
