package com.dili.alm.domain.dto;

import java.io.Serializable;

/****
 * 查询员工工时实体类
 * 
 * @author lijing
 *
 */
public class TaskByUsersDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8385138120559278308L;

	private String selectDate;
	private Long userNo;
	private String userName;
	private Long departmentId;
	private String departmentName;
	private Integer totalHour;
	private Integer taskHours;
	private Integer overHours;
	private Integer workOrderTaskHours;

	public String getSelectDate() {
		return selectDate;
	}

	public Long getUserNo() {
		return userNo;
	}

	public String getUserName() {
		return userName;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public Integer getTotalHour() {
		return totalHour;
	}

	public Integer getTaskHours() {
		return taskHours;
	}

	public Integer getOverHours() {
		return overHours;
	}

	public Integer getWorkOrderTaskHours() {
		return workOrderTaskHours;
	}

	public void setSelectDate(String selectDate) {
		this.selectDate = selectDate;
	}

	public void setUserNo(Long userNo) {
		this.userNo = userNo;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public void setTotalHour(Integer totalHour) {
		this.totalHour = totalHour;
	}

	public void setTaskHours(Integer taskHours) {
		this.taskHours = taskHours;
	}

	public void setOverHours(Integer overHours) {
		this.overHours = overHours;
	}

	public void setWorkOrderTaskHours(Integer workOrderTaskHours) {
		this.workOrderTaskHours = workOrderTaskHours;
	}

}
