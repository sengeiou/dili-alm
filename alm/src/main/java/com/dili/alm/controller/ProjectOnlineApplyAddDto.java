package com.dili.alm.controller;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.ss.domain.BaseDomain;

public class ProjectOnlineApplyAddDto extends BaseDomain implements ProjectOnlineApply {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8993438986960351998L;

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
	@Size(max = 500)
	private String dependencyDescription;
	@Size(max = 500)
	private String scopeDescription;

	private Long applicantId;

	private Date created;

	private Integer applyState;

	private Date actualOnlineDate;

	private Date submitTime;

	private String serialNumber;

	private Long businessOwnerId;
	private String emailAddress;

	private String executorId;
	@Size(max = 255, message = "git地址最大长度50个字符")
	private String git;
	@Size(max = 50, message = "分支最大长度不能超过50个字符")
	private String branch;
	private MultipartFile sqlFile;
	private MultipartFile startupScriptFile;
	@Size(max = 50, message = "依赖系统最大长度不能超过50个字符")
	private String dependencySystem;
	private MultipartFile dependencySystemFile;
	@Size(max = 50, message = "其他说明最大长度不能超过50个字符")
	private String otherDescription;
	private List<String> managerId;
	private List<String> subProjectName;

	private Long dependencySystemFileId;

	private Long startupScriptFileId;

	private String startupScript;

	private Long sqlFileId;
	@Size(max = 50, message = "sql脚本最大长度不能超过50个字符")
	private String sqlScript;
	@NotNull(message = "请选择是否针对市场上线")
	private Boolean marketVersion;
	private List<String> market;

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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	public String getGit() {
		return git;
	}

	@Override
	public void setGit(String git) {
		this.git = git;
	}

	@Override
	public String getBranch() {
		return branch;
	}

	@Override
	public void setBranch(String branch) {
		this.branch = branch;
	}

	public MultipartFile getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(MultipartFile sqlFile) {
		this.sqlFile = sqlFile;
	}

	public MultipartFile getStartupScriptFile() {
		return startupScriptFile;
	}

	public void setStartupScriptFile(MultipartFile startupScriptFile) {
		this.startupScriptFile = startupScriptFile;
	}

	@Override
	public String getDependencySystem() {
		return dependencySystem;
	}

	@Override
	public void setDependencySystem(String dependencySystem) {
		this.dependencySystem = dependencySystem;
	}

	public MultipartFile getDependencySystemFile() {
		return dependencySystemFile;
	}

	public void setDependencySystemFile(MultipartFile dependencySystemFile) {
		this.dependencySystemFile = dependencySystemFile;
	}

	@Override
	public String getOtherDescription() {
		return otherDescription;
	}

	@Override
	public void setOtherDescription(String otherDescription) {
		this.otherDescription = otherDescription;
	}

	public List<String> getManagerId() {
		return managerId;
	}

	public void setManagerId(List<String> managerId) {
		this.managerId = managerId;
	}

	public List<String> getSubProjectName() {
		return subProjectName;
	}

	public void setSubProjectName(List<String> subProjectName) {
		this.subProjectName = subProjectName;
	}

	@Override
	public String getSqlScript() {
		return sqlScript;
	}

	@Override
	public void setSqlScript(String sqlScript) {
		this.sqlScript = sqlScript;
	}

	@Override
	public Long getSqlFileId() {
		return sqlFileId;
	}

	@Override
	public void setSqlFileId(Long sqlFileId) {
		this.sqlFileId = sqlFileId;
	}

	@Override
	public String getStartupScript() {
		return startupScript;
	}

	@Override
	public void setStartupScript(String startupScript) {
		this.startupScript = startupScript;
	}

	@Override
	public Long getStartupScriptFileId() {
		return startupScriptFileId;
	}

	@Override
	public void setStartupScriptFileId(Long startupScriptFileId) {
		this.startupScriptFileId = startupScriptFileId;
	}

	@Override
	public Long getDependencySystemFileId() {
		return dependencySystemFileId;
	}

	@Override
	public void setDependencySystemFileId(Long dependencySystemFileId) {
		this.dependencySystemFileId = dependencySystemFileId;
	}

	public Boolean getMarketVersion() {
		return marketVersion;
	}

	public void setMarketVersion(Boolean marketVersion) {
		this.marketVersion = marketVersion;
	}

	public List<String> getMarket() {
		return market;
	}

	public void setMarket(List<String> market) {
		this.market = market;
	}

}
