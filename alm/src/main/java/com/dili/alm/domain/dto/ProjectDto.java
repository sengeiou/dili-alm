package com.dili.alm.domain.dto;

import java.util.Date;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectEntity;


public class ProjectDto  extends ProjectEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6324120234599416628L;
	
	private String startToEndDate;
	
	private Boolean manager;
	
	public String getStartToEndDate() {
		return startToEndDate;
	}

	public void setStartToEndDate(String startToEndDate) {
		this.startToEndDate = startToEndDate;
	}

	public Boolean getManager() {
		return manager;
	}

	public void setManager(Boolean manager) {
		this.manager = manager;
	}

}
