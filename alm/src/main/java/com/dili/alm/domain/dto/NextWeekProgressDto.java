package com.dili.alm.domain.dto;


/**
 * 
 * @author sgq
 *下周工作计划
 */
public class NextWeekProgressDto {
	private  String  serialNumber;//序号
	private  String  projectStage;//项目阶段
	private  String  taskName;//任务名称
	private  String  personLiable;//责任人
	private  String  planHour;//下周计划工时
	private  String  planDate;//计划完成日期
	private  String Remarks;//备注
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getProjectStage() {
		return projectStage;
	}
	public void setProjectStage(String projectStage) {
		this.projectStage = projectStage;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getPersonLiable() {
		return personLiable;
	}
	public void setPersonLiable(String personLiable) {
		this.personLiable = personLiable;
	}
	public String getPlanHour() {
		return planHour;
	}
	public void setPlanHour(String planHour) {
		this.planHour = planHour;
	}
	public String getPlanDate() {
		return planDate;
	}
	public void setPlanDate(String planDate) {
		this.planDate = planDate;
	}
	public String getRemarks() {
		return Remarks;
	}
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}





}
