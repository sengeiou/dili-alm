package com.dili.alm.domain.dto;

import java.util.List;

public class ProjectTypeCountDTO {
	private String type;
	
	private int typeCount;
	
	private int notStartCount;
	
	private int ongoingConut;
	
	private int completeCount;
	
	private int suspendedCount;
	
	private int shutCount;
	
	private int projectTypeProgress;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTypeCount() {
		return typeCount;
	}

	public void setTypeCount(int typeCount) {
		this.typeCount = typeCount;
	}

	public int getNotStartCount() {
		return notStartCount;
	}

	public void setNotStartCount(int notStartCount) {
		this.notStartCount = notStartCount;
	}

	public int getOngoingConut() {
		return ongoingConut;
	}

	public void setOngoingConut(int ongoingConut) {
		this.ongoingConut = ongoingConut;
	}

	public int getCompleteCount() {
		return completeCount;
	}

	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
	}

	public int getSuspendedCount() {
		return suspendedCount;
	}

	public void setSuspendedCount(int suspendedCount) {
		this.suspendedCount = suspendedCount;
	}

	public int getShutCount() {
		return shutCount;
	}

	public void setShutCount(int shutCount) {
		this.shutCount = shutCount;
	}

	public int getProjectTypeProgress() {
		return projectTypeProgress;
	}

	public void setProjectTypeProgress(int projectTypeProgress) {
		this.projectTypeProgress = projectTypeProgress;
	}

	
}
