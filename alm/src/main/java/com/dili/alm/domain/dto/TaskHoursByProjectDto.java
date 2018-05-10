package com.dili.alm.domain.dto;

import java.util.Map;

import com.dili.alm.domain.UserHour;



/****
 * 查询项目下的所有员工
 * @author lijing
 *
 */
public class TaskHoursByProjectDto  {
	
	Long projectId;
    String projectName;
    int sumHour;
    Map<Long,UserHour> userHours; //userId,工时
    
    
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Map<Long, UserHour> getUserHours() {
		return userHours;
	}
	public void setUserHours(Map<Long, UserHour> userHours) {
		this.userHours = userHours;
	}
	public int getSumHour() {
		return sumHour;
	}
	public void setSumHour(int sumHour) {
		this.sumHour = sumHour;
	}
  
}
