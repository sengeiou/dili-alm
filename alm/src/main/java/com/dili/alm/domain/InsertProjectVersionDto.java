package com.dili.alm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class InsertProjectVersionDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 235858742114177411L;

	private Long projectId;
	private String git;
	private String redmineUrl;
	private String version;
	private String notes;
	private String host;
	private Integer port;
	private String visitUrl;
	private Date plannedStartDate;
	private Date plannedEndDate;
	private Date plannedOnlineDate;
	private Integer online;
	private List<Long> fileIds;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getGit() {
		return git;
	}

	public void setGit(String git) {
		this.git = git;
	}

	public String getRedmineUrl() {
		return redmineUrl;
	}

	public void setRedmineUrl(String redmineUrl) {
		this.redmineUrl = redmineUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getVisitUrl() {
		return visitUrl;
	}

	public void setVisitUrl(String visitUrl) {
		this.visitUrl = visitUrl;
	}

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	public Date getPlannedOnlineDate() {
		return plannedOnlineDate;
	}

	public void setPlannedOnlineDate(Date plannedOnlineDate) {
		this.plannedOnlineDate = plannedOnlineDate;
	}

	public Integer getOnline() {
		return online;
	}

	public void setOnline(Integer online) {
		this.online = online;
	}

	public List<Long> getFileIds() {
		return fileIds;
	}

	public void setFileIds(List<Long> fileIds) {
		this.fileIds = fileIds;
	}

}
