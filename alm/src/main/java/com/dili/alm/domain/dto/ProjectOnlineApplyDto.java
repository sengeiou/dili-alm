package com.dili.alm.domain.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.ss.domain.BaseDomain;

public class ProjectOnlineApplyDto extends BaseDomain implements ProjectOnlineApply {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8354171445080544962L;

	private String projectName;
	@NotNull(message = "项目不能为空")
	private Long projectId;
	private String projectSerialNumber;
	@NotNull(message = "版本不能为空")
	private Long versionId;
	private String version;
	private Long projectManagerId;
	private Long productManagerId;
	private Long testManagerId;

	private Long developmentManagerId;

	@NotNull(message = "申请上线日期不能为空")
	private Date onlineDate;

	private String dependencyDescription;

	private String scopeDescription;

	private Long applicantId;

	private Date created;

	private Integer applyState;

	private Date actualOnlineDate;

	private Date submitTime;

	private String serialNumber;

	private Long businessOwnerId;

	private String executorId;

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public Long getProjectId() {
		return projectId;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public String getProjectSerialNumber() {
		return projectSerialNumber;
	}

	@Override
	public void setProjectSerialNumber(String projectSerialNumber) {
		this.projectSerialNumber = projectSerialNumber;
	}

	@Override
	public Long getVersionId() {
		return versionId;
	}

	@Override
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public Long getProjectManagerId() {
		return projectManagerId;
	}

	@Override
	public void setProjectManagerId(Long projectManagerId) {
		this.projectManagerId = projectManagerId;
	}

	@Override
	public Long getProductManagerId() {
		return productManagerId;
	}

	@Override
	public void setProductManagerId(Long productManagerId) {
		this.productManagerId = productManagerId;
	}

	@Override
	public Long getTestManagerId() {
		return testManagerId;
	}

	@Override
	public void setTestManagerId(Long testManagerId) {
		this.testManagerId = testManagerId;
	}

	@Override
	public Long getDevelopmentManagerId() {
		return developmentManagerId;
	}

	@Override
	public void setDevelopmentManagerId(Long developmentManagerId) {
		this.developmentManagerId = developmentManagerId;
	}

	@Override
	public Date getOnlineDate() {
		return onlineDate;
	}

	@Override
	public void setOnlineDate(Date onlineDate) {
		this.onlineDate = onlineDate;
	}

	@Override
	public String getDependencyDescription() {
		return dependencyDescription;
	}

	@Override
	public void setDependencyDescription(String dependencyDescription) {
		this.dependencyDescription = dependencyDescription;
	}

	@Override
	public String getScopeDescription() {
		return scopeDescription;
	}

	@Override
	public void setScopeDescription(String scopeDescription) {
		this.scopeDescription = scopeDescription;
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
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
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
	public Date getActualOnlineDate() {
		return actualOnlineDate;
	}

	@Override
	public void setActualOnlineDate(Date actualOnlineDate) {
		this.actualOnlineDate = actualOnlineDate;
	}

	@Override
	public Date getSubmitTime() {
		return submitTime;
	}

	@Override
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
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
	public Long getBusinessOwnerId() {
		return businessOwnerId;
	}

	@Override
	public void setBusinessOwnerId(Long businessOwnerId) {
		this.businessOwnerId = businessOwnerId;
	}

	@Override
	public String getExecutorId() {
		return executorId;
	}

	@Override
	public void setExecutorId(String executorId) {
		this.executorId = executorId;
	}

}
