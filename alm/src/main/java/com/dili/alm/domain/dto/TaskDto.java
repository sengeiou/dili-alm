package com.dili.alm.domain.dto;

import java.util.Date;


/**
 * @author sgq
 */
public class TaskDto {

    private  String name;
    private  Long versionId;
    private  Long phaseId;//
    private Date startDate;
    private Date endDate;
    private Long owner;//负责人
    private Short planTime;//计划时间
    private String describe;  //备注
    private int   overHour; //加班工时
    private int   taskHour;//工时
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getVersionId() {
		return versionId;
	}
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}
	public Long getPhaseId() {
		return phaseId;
	}
	public void setPhaseId(Long phaseId) {
		this.phaseId = phaseId;
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
	public Long getOwner() {
		return owner;
	}
	public void setOwner(Long owner) {
		this.owner = owner;
	}
	public Short getPlanTime() {
		return planTime;
	}
	public void setPlanTime(Short planTime) {
		this.planTime = planTime;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public int getOverHour() {
		return overHour;
	}
	public void setOverHour(int overHour) {
		this.overHour = overHour;
	}
	public int getTaskHour() {
		return taskHour;
	}
	public void setTaskHour(int taskHour) {
		this.taskHour = taskHour;
	}


	    
}
