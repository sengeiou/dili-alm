package com.dili.alm.domain.dto;

import java.util.Date;


/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-11-24 17:50:14.
 */

public class  ProjectWeeklyDto {
	private String  projectName;//项目名称
	private String serialNumber;//项目编号
	private String userName ;//项目经理
	
	private String projectType ;//项目类型
	private String projectInDept ;//项目所在部门
	private String businessParty ;//业务方
	private String planDate ;//计划上线日期
	private String projectId ;//项目id
	private String stageMan ;//阶段人 
	private String created ;//创建时间
	private String  completedProgress;//总体进展
	private String  beginAndEndTime;//本周起止日期
	private String id ;//周报id
	private Integer  completedProgressInt;//总体进展转换
	

	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	
	public Integer getCompletedProgressInt() {
		return completedProgressInt;
	}
	public void setCompletedProgressInt(Integer completedProgressInt) {
		this.completedProgressInt = completedProgressInt;
	}
	public String getCompletedProgress() {
		return completedProgress;
	}
	public void setCompletedProgress(String completedProgress) {
		this.completedProgress = completedProgress;
	}
	public String getStageMan() {
		return stageMan;
	}
	public void setStageMan(String stageMan) {
		this.stageMan = stageMan;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getBeginAndEndTime() {
		return beginAndEndTime;
	}
	public void setBeginAndEndTime(String beginAndEndTime) {
		this.beginAndEndTime = beginAndEndTime;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getProjectInDept() {
		return projectInDept;
	}
	public void setProjectInDept(String projectInDept) {
		this.projectInDept = projectInDept;
	}
	public String getBusinessParty() {
		return businessParty;
	}
	public void setBusinessParty(String businessParty) {
		this.businessParty = businessParty;
	}
	public String getPlanDate() {
		return planDate;
	}
	public void setPlanDate(String planDate) {
		this.planDate = planDate;
	}
	
	
	
}