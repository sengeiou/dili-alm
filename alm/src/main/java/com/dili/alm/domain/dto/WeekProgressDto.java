package com.dili.alm.domain.dto;

//本周进展情况  sgq
public class WeekProgressDto {
	
	private  String  weekVersion;//本周项目版本
	private  String  projectStage;//项目阶段
	private  String  serialNumber;//序号
	private  String  taskName;//任务名称
	private  String  version;//任务名称
	private  String  stage;//阶段
	private  String  personLiable;//责任人
	private  String  completion; //完成情况
	private  String workingHours;//本周工时
	private  String planWorkingHours;//预计工时
	private  String realWorkingHours;//实际工时
	private  String workingTimeDeviation;//工时偏差%
	private  String Remarks;//备注
	
	
	public String getWeekVersion() {
		return weekVersion;
	}
	public void setWeekVersion(String weekVersion) {
		this.weekVersion = weekVersion;
	}
	public String getProjectStage() {
		return projectStage;
	}
	public void setProjectStage(String projectStage) {
		this.projectStage = projectStage;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public String getPersonLiable() {
		return personLiable;
	}
	public void setPersonLiable(String personLiable) {
		this.personLiable = personLiable;
	}
	public String getCompletion() {
		return completion;
	}
	public void setCompletion(String completion) {
		this.completion = completion;
	}
	public String getWorkingHours() {
		return workingHours;
	}
	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}
	public String getPlanWorkingHours() {
		return planWorkingHours;
	}
	public void setPlanWorkingHours(String planWorkingHours) {
		this.planWorkingHours = planWorkingHours;
	}
	public String getRealWorkingHours() {
		return realWorkingHours;
	}
	public void setRealWorkingHours(String realWorkingHours) {
		this.realWorkingHours = realWorkingHours;
	}
	public String getWorkingTimeDeviation() {
		return workingTimeDeviation;
	}
	public void setWorkingTimeDeviation(String workingTimeDeviation) {
		this.workingTimeDeviation = workingTimeDeviation;
	}
	public String getRemarks() {
		return Remarks;
	}
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}




	
}
