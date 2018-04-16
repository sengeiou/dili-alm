package com.dili.alm.domain.dto;

/****
 * 查询项目工时中员工-项目总工时
 * @author lijing
 *
 */
public class SelectTaskHoursByUserProjectDto {
	
	Long ownerId;
    Long projectId;
    int sumUPTaskHours;
    int sumUPOverHours;
    
    String projectName;
    
    

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public int getSumUPTaskHours() {
		return sumUPTaskHours;
	}
	public void setSumUPTaskHours(int sumUPTaskHours) {
		this.sumUPTaskHours = sumUPTaskHours;
	}
	public int getSumUPOverHours() {
		return sumUPOverHours;
	}
	public void setSumUPOverHours(int sumUPOverHours) {
		this.sumUPOverHours = sumUPOverHours;
	}
    
    

  
    
}
