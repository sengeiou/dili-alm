package com.dili.alm.domain.dto;

import java.util.Date;

import com.dili.alm.domain.ProjectEntity;

public class ProjectProgressDto extends ProjectEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5169425767099501919L;

	private Date estimateLaunchDate;

	private Date startDate;

	private Date endDate;

	private String dateProgress;

	private String projectProgress;

	private String launchTime;

	@Override
	public Date getEstimateLaunchDate() {
		return estimateLaunchDate;
	}

	@Override
	public void setEstimateLaunchDate(Date estimateLaunchDate) {
		this.estimateLaunchDate = estimateLaunchDate;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
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
