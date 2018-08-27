package com.dili.alm.domain.dto;

import java.io.Serializable;

public class ProjectCostStatisticDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 306303079228685909L;

	private Long projectId;
	private String projectName;
	private String planHumanCost;
	private String planSoftwareCost;
	private String planHardwareCost;
	private String planTravelCost;
	private String planOtherCost;
	private String planTotalCost;
	private String actualHumanCost;
	private String actualSoftwareCost;
	private String actualHardwareCost;
	private String actualTravelCost;
	private String actualOtherCost;
	private String actualTotalCost;

	public Long getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getPlanHumanCost() {
		return planHumanCost;
	}

	public String getPlanSoftwareCost() {
		return planSoftwareCost;
	}

	public String getPlanHardwareCost() {
		return planHardwareCost;
	}

	public String getPlanTravelCost() {
		return planTravelCost;
	}

	public String getPlanOtherCost() {
		return planOtherCost;
	}

	public String getPlanTotalCost() {
		return planTotalCost;
	}

	public String getActualHumanCost() {
		return actualHumanCost;
	}

	public String getActualSoftwareCost() {
		return actualSoftwareCost;
	}

	public String getActualHardwareCost() {
		return actualHardwareCost;
	}

	public String getActualTravelCost() {
		return actualTravelCost;
	}

	public String getActualOtherCost() {
		return actualOtherCost;
	}

	public String getActualTotalCost() {
		return actualTotalCost;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setPlanHumanCost(String planHumanCost) {
		this.planHumanCost = planHumanCost;
	}

	public void setPlanSoftwareCost(String planSoftwareCost) {
		this.planSoftwareCost = planSoftwareCost;
	}

	public void setPlanHardwareCost(String planHardwareCost) {
		this.planHardwareCost = planHardwareCost;
	}

	public void setPlanTravelCost(String planTravelCost) {
		this.planTravelCost = planTravelCost;
	}

	public void setPlanOtherCost(String planOtherCost) {
		this.planOtherCost = planOtherCost;
	}

	public void setPlanTotalCost(String planTotalCost) {
		this.planTotalCost = planTotalCost;
	}

	public void setActualHumanCost(String actualHumanCost) {
		this.actualHumanCost = actualHumanCost;
	}

	public void setActualSoftwareCost(String actualSoftwareCost) {
		this.actualSoftwareCost = actualSoftwareCost;
	}

	public void setActualHardwareCost(String actualHardwareCost) {
		this.actualHardwareCost = actualHardwareCost;
	}

	public void setActualTravelCost(String actualTravelCost) {
		this.actualTravelCost = actualTravelCost;
	}

	public void setActualOtherCost(String actualOtherCost) {
		this.actualOtherCost = actualOtherCost;
	}

	public void setActualTotalCost(String actualTotalCost) {
		this.actualTotalCost = actualTotalCost;
	}

}
