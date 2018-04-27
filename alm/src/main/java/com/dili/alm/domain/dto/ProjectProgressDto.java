package com.dili.alm.domain.dto;

import java.util.Date;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectEntity;


public class ProjectProgressDto  extends ProjectEntity{
	private Date estimateLaunchDate;
	
	private Date startDate;
	
	private Date endDate;
	
	private String dateProgress;
	
	private String projectProgress;
	
	private String launchTime;


	public Date getEstimateLaunchDate() {
		return estimateLaunchDate;
	}

	public void setEstimateLaunchDate(Date estimateLaunchDate) {
		this.estimateLaunchDate = estimateLaunchDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



	public String getDateProgress() {
		return dateProgress;
	}

	public void setDateProgress(String dateProgress) {
		this.dateProgress = dateProgress;
	}

	public String getLaunchTime() {
		return launchTime;
	}

	public void setLaunchTime(String launchTime) {
		this.launchTime = launchTime;
	}

	public String getProjectProgress() {
		return projectProgress;
	}

	public void setProjectProgress(String projectProgress) {
		this.projectProgress = projectProgress;
	}

	
}
