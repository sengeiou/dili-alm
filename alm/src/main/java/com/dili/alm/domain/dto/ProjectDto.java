package com.dili.alm.domain.dto;

public class ProjectDto{
	private Long id;
	
	private String serialNumber;
	
	private String name;
	
	private String type;
	
	private String startToEndDate;
	
	private String actualStartDate;
	
	private	String projectState;
	
	private Integer taskCount;
	
	private Integer memberCount;
	
	private Integer completedProgress;
	
	private Long originator;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartToEndDate() {
		return startToEndDate;
	}

	public void setStartToEndDate(String startToEndDate) {
		this.startToEndDate = startToEndDate;
	}

	public String getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(String actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public String getProjectState() {
		return projectState;
	}

	public void setProjectState(String projectState) {
		this.projectState = projectState;
	}

	public Integer getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(Integer taskCount) {
		this.taskCount = taskCount;
	}

	public Integer getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}

	public Integer getCompletedProgress() {
		return completedProgress;
	}

	public void setCompletedProgress(Integer completedProgress) {
		this.completedProgress = completedProgress;
	}

	
	public Long getOriginator() {
		return originator;
	}

	public void setOriginator(Long originator) {
		this.originator = originator;
	}

	public ProjectDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public ProjectDto(Long id, String serialNumber, String name, String type,
			String startToEndDate, String actualStartDate, String projectState,
			Integer taskCount, Integer memberCount, Integer completedProgress,
			Long originator) {
		super();
		this.id = id;
		this.serialNumber = serialNumber;
		this.name = name;
		this.type = type;
		this.startToEndDate = startToEndDate;
		this.actualStartDate = actualStartDate;
		this.projectState = projectState;
		this.taskCount = taskCount;
		this.memberCount = memberCount;
		this.completedProgress = completedProgress;
		this.originator = originator;
	}

	@Override
	public String toString() {
		return "ProjectDto [id=" + id + ", serialNumber=" + serialNumber
				+ ", name=" + name + ", type=" + type + ", startToEndDate="
				+ startToEndDate + ", actualStartDate=" + actualStartDate
				+ ", projectState=" + projectState + ", taskCount=" + taskCount
				+ ", memberCount=" + memberCount + ", completedProgress="
				+ completedProgress + ", originator=" + originator + "]";
	}
	
	
	
}
