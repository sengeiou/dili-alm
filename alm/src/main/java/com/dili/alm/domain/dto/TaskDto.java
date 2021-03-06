package com.dili.alm.domain.dto;

import java.util.Date;

/**
 * @author sgq
 */
public class TaskDto {

	private Long id;
	private String name;
	private String versionId;
	private Date startDate;
	private Date endDate;
	private String endDateStr;
	private String owner;// 负责人
	private Short planTime;// 计划时间
	private String describe; // 备注
	private int overHour; // 加班工时
	private int taskHour;// 工时
	private String status;// 完成状态

	private int number;// 序号
	private String weekHour;// 本周工时
	private String realHour;// 实际工时
	private String hourDeviation;// 工时偏差
	private String fackEndDate;// 项目实际完成时间
	private String projectId;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getEndDateStr() {
		return endDateStr;
	}

	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}

	public String getFackEndDate() {
		return fackEndDate;
	}

	public void setFackEndDate(String fackEndDate) {
		this.fackEndDate = fackEndDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHourDeviation() {
		return hourDeviation;
	}

	public void setHourDeviation(String hourDeviation) {
		this.hourDeviation = hourDeviation;
	}

	public String getRealHour() {
		return realHour;
	}

	public void setRealHour(String realHour) {
		this.realHour = realHour;
	}

	public String getWeekHour() {
		return weekHour;
	}

	public void setWeekHour(String weekHour) {
		this.weekHour = weekHour;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
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
