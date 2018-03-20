package com.dili.alm.domain;



/****
 * 查询员工工时实体类
 * @author lijing
 *
 */
public class TaskByUsersDto  {
	
	Long userNo;
    String userName;
    Long departmentId;
    String departmentName;
    int totalHour;
    int taskHour;
    int overHour;
    
    
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
	public int getTaskHour() {
		return taskHour;
	}
	public void setTaskHour(int taskHour) {
		this.taskHour = taskHour;
	}
	public int getOverHour() {
		return overHour;
	}
	public void setOverHour(int overHour) {
		this.overHour = overHour;
	}
    
    
}
