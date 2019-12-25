package com.dili.alm.domain;

import java.util.List;

public class ProjectVersionFormDto extends ProjectVersionEntity	 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 235858742114177411L;

	private List<Long> fileIds;
	
	private List<String> demandIds;
	
	public List<Long> getFileIds() {
		return fileIds;
	}

	public void setFileIds(List<Long> fileIds) {
		this.fileIds = fileIds;
	}

	public List<String> getDemandIds() {
		return demandIds;
	}

	public void setDemandIds(List<String> demandIds) {
		this.demandIds = demandIds;
	}

}
