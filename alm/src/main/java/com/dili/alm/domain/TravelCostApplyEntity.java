package com.dili.alm.domain;

import java.util.Date;

import com.dili.ss.domain.BaseDomain;

public class TravelCostApplyEntity extends BaseDomain implements TravelCostApply {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7206572201554776075L;

	private Long projectId;
	private Long applicantId;
	private Long rootDepartemntId;
	private Long departmentId;
	private Integer applyState;
	private Long totalAmount;
	private Integer travelDayAmount;
	private Date submitDate;

	private String serialNumber;

	private Date modificationTime;

	private Date creationTime;

	@Override
	public Long getProjectId() {
		return projectId;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public Long getApplicantId() {
		return applicantId;
	}

	@Override
	public void setApplicantId(Long applicantId) {
		this.applicantId = applicantId;
	}

	@Override
	public Long getRootDepartemntId() {
		return rootDepartemntId;
	}

	@Override
	public void setRootDepartemntId(Long rootDepartemntId) {
		this.rootDepartemntId = rootDepartemntId;
	}

	@Override
	public Long getDepartmentId() {
		return departmentId;
	}

	@Override
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	@Override
	public Integer getApplyState() {
		return applyState;
	}

	@Override
	public void setApplyState(Integer applyState) {
		this.applyState = applyState;
	}

	@Override
	public Long getTotalAmount() {
		return totalAmount;
	}

	@Override
	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public Integer getTravelDayAmount() {
		return travelDayAmount;
	}

	@Override
	public void setTravelDayAmount(Integer travelDayAmount) {
		this.travelDayAmount = travelDayAmount;
	}

	@Override
	public Date getSubmitDate() {
		return submitDate;
	}

	@Override
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	@Override
	public String getSerialNumber() {
		return serialNumber;
	}

	@Override
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	@Override
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	@Override
	public Date getModificationTime() {
		return modificationTime;
	}

	@Override
	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}

}
