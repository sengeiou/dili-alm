package com.dili.alm.domain.dto;

import java.util.Date;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectEntity;


public class ProjectProgressDto  extends ProjectEntity{
	private Date estimateLaunchDate;
	
	private Date startDate;
	
	private Date endDate;
	
	private Integer dateProgress;
	
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

	public Integer getDateProgress() {
		return dateProgress;
	}

	public void setDateProgress(Integer dateProgress) {
		this.dateProgress = dateProgress;
	}

	public String getLaunchTime() {
		return launchTime;
	}

	public void setLaunchTime(String launchTime) {
		this.launchTime = launchTime;
	}

	
}
