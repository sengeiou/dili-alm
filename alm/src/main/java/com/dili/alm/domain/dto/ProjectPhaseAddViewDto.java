package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectPhaseEntity;
import com.dili.alm.domain.ProjectVersion;

public class ProjectPhaseAddViewDto extends ProjectPhaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6386241797257032207L;

	private Project project;
	private List<ProjectVersion> allVersions;
	private List<DataDictionaryValueDto> phaseNames;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<ProjectVersion> getAllVersions() {
		return allVersions;
	}

	public void setAllVersions(List<ProjectVersion> allVersions) {
		this.allVersions = allVersions;
	}

	public List<DataDictionaryValueDto> getPhaseNames() {
		return phaseNames;
	}

	public void setPhaseNames(List<DataDictionaryValueDto> phaseNames) {
		this.phaseNames = phaseNames;
	}

}
