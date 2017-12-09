package com.dili.alm.domain.dto;

import java.util.Date;

import com.dili.alm.domain.Task;

/*
 * 任务查询的DTO
 */
public class TaskSelectDto {

	long id;// ID

	String name;// 任务名称

	String projectName;// 所属项目名称

	String versionStr;// 版本名称

	String phaseStr;// 项目阶段

	String flowStr = "正常流程";// 流程

	String statusStr = "未开始";// 状态

	int status;

	String planDays;// 计划周期

	int progress = 0;// 进度

	String ownerName = "";

	/* 修改数据 */
	long projectId;

	long versionId;

	Long phaseId;

	Date startDate;

	Date endDate;

	Long beforeTask;

	Integer type;

	Long owner;

	Boolean flow;

	String describe;

	Short planTime;

	public TaskSelectDto(Task task) {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getVersionStr() {
		return versionStr;
	}

	public void setVersionStr(String versionStr) {
		this.versionStr = versionStr;
	}

	public String getPhaseStr() {
		return phaseStr;
	}

	public void setPhaseStr(String phaseStr) {
		this.phaseStr = phaseStr;
	}

	public String getFlowStr() {
		return flowStr;
	}

	public void setFlowStr(Boolean flow) {

		this.flowStr = flow ? "正常流程" : "修改流程";
	}

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(int status) {

		String statusToStr = "未开始";

		if (status == 0) {
			statusToStr = "未开始";
		} else if (status == 1) {
			statusToStr = "已开始";
		} else if (status == 2) {
			statusToStr = "暂停";
		} else if (status == 3) {
			statusToStr = "已完成";
		}
		this.statusStr = statusToStr;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPlanDays() {
		return planDays;
	}

	public void setPlanDays(String planDays) {
		this.planDays = planDays;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		if (this.statusStr.equals("未开始")) {
			progress = 0;
		}
		this.progress = progress;
	}

	public Long getOwner() {
		return owner;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public long getVersionId() {
		return versionId;
	}

	public void setVersionId(long versionId) {
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

	public Long getBeforeTask() {
		return beforeTask;
	}

	public void setBeforeTask(Long beforeTask) {
		this.beforeTask = beforeTask;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getFlow() {
		return flow;
	}

	public void setFlow(Boolean flow) {
		this.flow = flow;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public Short getPlanTime() {
		return planTime;
	}

	public void setPlanTime(Short planTime) {
		this.planTime = planTime;
	}

	public void setFlowStr(String flowStr) {
		this.flowStr = flowStr;
	}

}