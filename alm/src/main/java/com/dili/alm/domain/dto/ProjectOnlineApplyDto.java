package com.dili.alm.domain.dto;

import java.util.Date;
import java.util.List;

import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.ss.domain.BaseDomain;

public class ProjectOnlineApplyDto extends BaseDomain implements ProjectOnlineApply {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8354171445080544962L;

	private String projectName;
	private Long projectId;
	private String projectSerialNumber;
	private Long versionId;
	private String version;
	private Long projectManagerId;
	private Long productManagerId;
	private Long testManagerId;

	private Long developmentManagerId;

	private Date onlineDate;

	private String dependencyDescription;

	private String scopeDescription;

	private Long applicantId;

	private Date created;
	private List<OnlineSystemDto> onlineSystems;
	private List<String> emailAddresses;

	private Integer applyState;

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

	public List<OnlineSystemDto> getOnlineSystems() {
		return onlineSystems;
	}

	public void setOnlineSystems(List<OnlineSystemDto> onlineSystems) {
		this.onlineSystems = onlineSystems;
	}

	public List<String> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(List<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	@Override
	public Integer getApplyState() {
		return applyState;
	}

	@Override
	public void setApplyState(Integer applyState) {
		this.applyState = applyState;
	}

}
