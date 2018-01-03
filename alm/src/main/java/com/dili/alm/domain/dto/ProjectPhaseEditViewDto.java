package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.Files;

public class ProjectPhaseEditViewDto extends ProjectPhaseAddViewDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -717131917218773496L;

	private List<Files> files;

	public List<Files> getFiles() {
		return files;
	}

	public void setFiles(List<Files> files) {
		this.files = files;
	}

}
