package com.dili.alm.domain.dto;

import java.io.Serializable;

public class ProjectTaskHourCountDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5014962042807554815L;

	// 项目id
	private Long projectId;
	// 项目常规加班总工时
	private Long totalOverHour;
	// 项目常规总工时
	private Long totalTaskHour;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProjectTaskHourCountDto other = (ProjectTaskHourCountDto) obj;
		if (projectId == null) {
			if (other.projectId != null) {
				return false;
			}
		} else if (!projectId.equals(other.projectId)) {
			return false;
		}
		return true;
	}

	public Long getProjectId() {
		return projectId;
	}

	public Long getTotalOverHour() {
		return totalOverHour;
	}

	public Long getTotalTaskHour() {
		return totalTaskHour;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
		return result;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setTotalOverHour(Long totalOverHour) {
		this.totalOverHour = totalOverHour;
	}

	public void setTotalTaskHour(Long totalTaskHour) {
		this.totalTaskHour = totalTaskHour;
	}
}
