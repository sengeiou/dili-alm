package com.dili.alm.domain.dto;

public class NextWeeklyDto {

	private Long id;
	private String name;
	private String planTime;
	private String owner;
	private String describe;
	private String endDate;
	private int number;
	private String taskHour;
	private String overHour;
	private String surplus;// 项目剩余计划工时

	public String getSurplus() {
		return surplus;
	}

	public void setSurplus(String surplus) {
		this.surplus = surplus;
	}

	public String getOverHour() {
		return overHour;
	}

	public void setOverHour(String overHour) {
		this.overHour = overHour;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskHour() {
		return taskHour;
	}

	public void setTaskHour(String taskHour) {
		this.taskHour = taskHour;
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

	public String getPlanTime() {
		return planTime;
	}

	public void setPlanTime(String planTime) {
		this.planTime = planTime;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
