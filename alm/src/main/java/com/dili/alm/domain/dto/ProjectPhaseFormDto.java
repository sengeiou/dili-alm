package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.ProjectPhaseEntity;

public class ProjectPhaseFormDto extends ProjectPhaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7447773047679091414L;

	private List<Long> fileIds;

	public List<Long> getFileIds() {
		return fileIds;
	}

	public void setFileIds(List<Long> fileIds) {
		this.fileIds = fileIds;
	}
}
