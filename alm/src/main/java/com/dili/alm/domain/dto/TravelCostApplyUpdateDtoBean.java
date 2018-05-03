package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.TravelCostApplyEntity;

public class TravelCostApplyUpdateDtoBean extends TravelCostApplyEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6871209286642053044L;

	private String applicantName;
	private String projectName;
	private String departmentName;
	private String rootDepartmentName;
	private List<TravelCostDtoBean> travelCost;

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public String getRootDepartmentName() {
		return rootDepartmentName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public void setRootDepartmentName(String rootDepartmentName) {
		this.rootDepartmentName = rootDepartmentName;
	}

	public List<TravelCostDtoBean> getTravelCost() {
		return travelCost;
	}

	public void setTravelCost(List<TravelCostDtoBean> travelCost) {
		this.travelCost = travelCost;
	}

}
