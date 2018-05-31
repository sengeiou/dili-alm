package com.dili.alm.domain.dto;

/****
 * 查询员工工时实体类
 * 
 * @author lijing
 *
 */
public class TaskByUsersDto {

	String selectDate;
	Long userNo;
	String userName;
	Long departmentId;
	String departmentName;
	int totalHour;
	int taskHours;
	int overHours;

	public String getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(String selectDate) {
		this.selectDate = selectDate;
	}

	public Long getUserNo() {
		return userNo;
	}

	public void setUserNo(Long userNo) {
		this.userNo = userNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public int getTotalHour() {
		return totalHour;
	}

	public void setTotalHour(int totalHour) {
		this.totalHour = totalHour;
	}

	public int getTaskHours() {
		return taskHours;
	}

	public void setTaskHours(int taskHours) {
		this.taskHours = taskHours;
	}

	public int getOverHours() {
		return overHours;
	}

	public void setOverHours(int overHours) {
		this.overHours = overHours;
	}

}
