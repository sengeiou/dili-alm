package com.dili.alm.domain.dto;

public class ProjectActionRecordExportDto {

	private String actionName;
	private String startDate;
	private String endDate;
	private String actualStartDate;
	private String actualEndDate;
	private String overDays;

	public String getActionName() {
		return actionName;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getActualStartDate() {
		return actualStartDate;
	}

	public String getActualEndDate() {
		return actualEndDate;
	}

	public String getOverDays() {
		return overDays;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setActualStartDate(String actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public void setActualEndDate(String actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	public void setOverDays(String overDays) {
		this.overDays = overDays;
	}
}
