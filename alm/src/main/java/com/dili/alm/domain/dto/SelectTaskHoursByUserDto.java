package com.dili.alm.domain.dto;

import java.util.Map;


/****
 * 查询项目工时中员工单位的工时
 * @author lijing
 *
 */
public class SelectTaskHoursByUserDto {
	
	String selectDate;
	Long userNo;
    String userName;
    int sumTaskHours;
    int sumOverHours;
    int totalHours;
    
    Map<String,String> projectHours;//key-项目'project'id，value-taskHour:value;overHour:value;
    
    
    
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
	public Map<String, String> getProjectHours() {
		return projectHours;
	}
	public void setProjectHours(Map<String, String> projectHours) {
		this.projectHours = projectHours;
	}
	public int getSumTaskHours() {
		return sumTaskHours;
	}
	public void setSumTaskHours(int sumTaskHours) {
		this.sumTaskHours = sumTaskHours;
	}
	public int getSumOverHours() {
		return sumOverHours;
	}
	public void setSumOverHours(int sumOverHours) {
		this.sumOverHours = sumOverHours;
	}
	public int getTotalHours() {
		return totalHours;
	}
	public void setTotalHours(int totalHours) {
		this.totalHours = totalHours;
	}

    
}
