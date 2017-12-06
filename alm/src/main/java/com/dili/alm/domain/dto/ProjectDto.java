package com.dili.alm.domain.dto;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectEntity;


public class ProjectDto  extends ProjectEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6324120234599416628L;
	
	private String startToEndDate;
	
	private String actualStartTime;
	
	public String getStartToEndDate() {
		return startToEndDate;
	}

	public void setStartToEndDate(String startToEndDate) {
		this.startToEndDate = startToEndDate;
	}

	public String getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(String actualStartTime) {
		this.actualStartTime = actualStartTime;
	}
}
