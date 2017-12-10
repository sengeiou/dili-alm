package com.dili.alm.domain;

import java.util.Date;

import com.dili.ss.domain.BaseDomain;

public class ProjectPhaseEntity extends BaseDomain implements ProjectPhase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9196755655359775223L;

	private String name;
	private Long projectId;
	private Long versionId;
	private Date plannedStartDate;
	private Date plannedEndDate;
	private Date actualStartDate;
	private Date actualEndDate;
	private Long creatorId;
	private Long modifierId;
	private Integer completedProgress = 0;
	private Date created;
	private Date modified;

	private Integer phaseState;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Long getProjectId() {
		return this.projectId;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public Long getVersionId() {
		return this.versionId;
	}

	@Override
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	@Override
	public Date getPlannedStartDate() {
		return this.plannedStartDate;
	}

	@Override
	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	@Override
	public Date getPlannedEndDate() {
		return this.plannedEndDate;
	}

	@Override
	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	@Override
	public Date getActualStartDate() {
		return this.actualStartDate;
	}

	@Override
	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	@Override
	public Date getActualEndDate() {
		return this.actualEndDate;
	}

	@Override
	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	@Override
	public Long getCreatorId() {
		return this.creatorId;
	}

	@Override
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	@Override
	public Long getModifierId() {
		return this.modifierId;
	}

	@Override
	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}

	@Override
	public Integer getCompletedProgress() {
		return this.completedProgress;
	}

	@Override
	public void setCompletedProgress(Integer completedProgress) {
		this.completedProgress = completedProgress;
	}

	@Override
	public Date getCreated() {
		return this.created;
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
	public Integer getPhaseState() {
		return phaseState;
	}

	@Override
	public void setPhaseState(Integer phaseState) {
		this.phaseState = phaseState;
	}
}
