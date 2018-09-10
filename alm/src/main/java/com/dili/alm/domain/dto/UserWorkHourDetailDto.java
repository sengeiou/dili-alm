package com.dili.alm.domain.dto;

import java.io.Serializable;

public class UserWorkHourDetailDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6881856031817469192L;

	private String taskDate;
	private Integer taskHours;
	private Integer overtimeHours;
	private String workContent;

	public String getTaskDate() {
		return taskDate;
	}

	public Integer getTaskHours() {
		return taskHours;
	}

	public Integer getOvertimeHours() {
		return overtimeHours;
	}

	public String getWorkContent() {
		return workContent;
	}

	public void setTaskDate(String taskDate) {
		this.taskDate = taskDate;
	}

	public void setTaskHours(Integer taskHours) {
		this.taskHours = taskHours;
	}

	public void setOvertimeHours(Integer overtimeHours) {
		this.overtimeHours = overtimeHours;
	}

	public void setWorkContent(String workContent) {
		this.workContent = workContent;
	}

}
