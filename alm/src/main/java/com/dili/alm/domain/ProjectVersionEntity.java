package com.dili.alm.domain;

import java.util.Date;

import com.dili.ss.domain.BaseDomain;

public class ProjectVersionEntity extends BaseDomain implements ProjectVersion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1695980742915659348L;

	private String code;
	private Long projectId;
	private String git;
	private String redmineUrl;
	private String version;
	private String notes;
	private Long creatorId;
	private Long modifierId;
	private Date created;
	private Date modified;
	private Date releaseTime;
	private Integer emailNotice;
	private String host;
	private Integer port;
	private String visitUrl;
	private Integer versionState = ProjectState.NOT_START.getValue();
	private Date plannedStartDate;
	private Date plannedEndDate;
	private Date plannedOnlineDate;
	private Date actualStartDate;
	private Date actualEndDate;
	private Integer completedProgress = 0;

	private Boolean online;

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Long getProjectId() {
		return projectId;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public String getGit() {
		return git;
	}

	@Override
	public void setGit(String git) {
		this.git = git;
	}

	@Override
	public String getRedmineUrl() {
		return redmineUrl;
	}

	@Override
	public void setRedmineUrl(String redmineUrl) {
		this.redmineUrl = redmineUrl;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getNotes() {
		return notes;
	}

	@Override
	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public Long getCreatorId() {
		return creatorId;
	}

	@Override
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	@Override
	public Long getModifierId() {
		return modifierId;
	}

	@Override
	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public Date getModified() {
		return modified;
	}

	@Override
	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Override
	public Date getReleaseTime() {
		return releaseTime;
	}

	@Override
	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	@Override
	public Integer getEmailNotice() {
		return emailNotice;
	}

	@Override
	public void setEmailNotice(Integer emailNotice) {
		this.emailNotice = emailNotice;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public Integer getPort() {
		return port;
	}

	@Override
	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	public String getVisitUrl() {
		return visitUrl;
	}

	@Override
	public void setVisitUrl(String visitUrl) {
		this.visitUrl = visitUrl;
	}

	@Override
	public Integer getVersionState() {
		return versionState;
	}

	@Override
	public void setVersionState(Integer versionState) {
		this.versionState = versionState;
	}

	@Override
	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	@Override
	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	@Override
	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	@Override
	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	@Override
	public Date getPlannedOnlineDate() {
		return plannedOnlineDate;
	}

	@Override
	public void setPlannedOnlineDate(Date plannedOnlineDate) {
		this.plannedOnlineDate = plannedOnlineDate;
	}

	@Override
	public Date getActualStartDate() {
		return actualStartDate;
	}

	@Override
	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	@Override
	public Date getActualEndDate() {
		return actualEndDate;
	}

	@Override
	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	@Override
	public Integer getCompletedProgress() {
		return completedProgress;
	}

	@Override
	public void setCompletedProgress(Integer completedProgress) {
		this.completedProgress = completedProgress;
	}

	@Override
	public Boolean getOnline() {
		return online;
	}

	@Override
	public void setOnline(Boolean online) {
		this.online = online;
	}

}
