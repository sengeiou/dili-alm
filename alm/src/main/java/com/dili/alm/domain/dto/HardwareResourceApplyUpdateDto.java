package com.dili.alm.domain.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.dili.alm.domain.HardwareResourceApply;
import com.dili.ss.domain.BaseDomain;

public class HardwareResourceApplyUpdateDto extends BaseDomain implements HardwareResourceApply {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8910167565987578290L;

	// 申请人id
	private Long applicantId;
	// 申请部门id
	private Long applicationDepartmentId;
	// 申请日期
	private Date applyDate;
	// 申请原因
	private String applyReason;
	private Integer applyState;
	// 配置要求
	private List<HardwareResourceRequirementDto> configurationRequirement;
	private Date created;
	// 其他说明
	private String otherDescription;
	// 项目id
	private Long projectId;
	private Long projectManagerId;
	private String projectName;
	private String projectSerialNumber;
	private String serviceEnvironment;
	// 使用环境
	private Set<String> serviceEnvironments;
	private Date submitTime;

	@Override
	public Long getApplicantId() {
		return applicantId;
	}

	@Override
	public Long getApplicationDepartmentId() {
		return applicationDepartmentId;
	}

	@Override
	public Date getApplyDate() {
		return applyDate;
	}

	@Override
	public String getApplyReason() {
		return applyReason;
	}

	@Override
	public Integer getApplyState() {
		return applyState;
	}

	public List<HardwareResourceRequirementDto> getConfigurationRequirement() {
		return configurationRequirement;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public String getOtherDescription() {
		return otherDescription;
	}

	@Override
	public Long getProjectId() {
		return projectId;
	}

	@Override
	public Long getProjectManagerId() {
		return projectManagerId;
	}

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public String getProjectSerialNumber() {
		return projectSerialNumber;
	}

	@Override
	public String getServiceEnvironment() {
		return serviceEnvironment;
	}

	public Set<String> getServiceEnvironments() {
		return serviceEnvironments;
	}

	@Override
	public Date getSubmitTime() {
		return submitTime;
	}

	@Override
	public void setApplicantId(Long applicantId) {
		this.applicantId = applicantId;
	}

	@Override
	public void setApplicationDepartmentId(Long applicationDepartmentId) {
		this.applicationDepartmentId = applicationDepartmentId;
	}

	@Override
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	@Override
	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}

	@Override
	public void setApplyState(Integer applyState) {
		this.applyState = applyState;
	}

	public void setConfigurationRequirement(List<HardwareResourceRequirementDto> configurationRequirement) {
		this.configurationRequirement = configurationRequirement;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public void setOtherDescription(String otherDescription) {
		this.otherDescription = otherDescription;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public void setProjectManagerId(Long projectManagerId) {
		this.projectManagerId = projectManagerId;
	}

	@Override
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public void setProjectSerialNumber(String projectSerialNumber) {
		this.projectSerialNumber = projectSerialNumber;
	}

	@Override
	public void setServiceEnvironment(String serviceEnvironment) {
		this.serviceEnvironment = serviceEnvironment;
	}

	public void setServiceEnvironments(Set<String> serviceEnvironments) {
		this.serviceEnvironments = serviceEnvironments;
	}

	@Override
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

}
