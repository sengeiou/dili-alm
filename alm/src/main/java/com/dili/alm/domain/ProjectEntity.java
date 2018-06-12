package com.dili.alm.domain;

import java.util.Date;

import com.dili.ss.domain.BaseDomain;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-11-24 17:50:14.
 */

public class ProjectEntity extends BaseDomain implements Project {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2760497347976601584L;

	private Long id;
	private String serialNumber;
	private String name;
	private String type;
	private Date startDate;
	private Date endDate;
	private Date actualStartDate;
	private Date actualEndDate;
	private Integer projectState;
	private Integer taskCount;
	private Integer memberCount;
	private Integer completedProgress;
	private Long originator;

	private Long projectManager;

	private Long developManager;

	private Long testManager;

	private Long productManager;

	private Date created;

	private Date modified;

	private Long parentId;

	private Long applyId;

	private Date closeTime;

	private Long dep;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public Date getActualStartDate() {
		return actualStartDate;
	}

	@Override
	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	@Override
	public Integer getProjectState() {
		return projectState;
	}

	@Override
	public void setProjectState(Integer projectState) {
		this.projectState = projectState;
	}

	@Override
	public Integer getTaskCount() {
		return taskCount;
	}

	@Override
	public void setTaskCount(Integer taskCount) {
		this.taskCount = taskCount;
	}

	@Override
	public Integer getMemberCount() {
		return memberCount;
	}

	@Override
	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}

	@Override
	public Integer getCompletedProgress() {
		return completedProgress;
	}

	@Override
	public void setCompletedProgress(Integer completedProgress) {
		this.completedProgress = completedProgress;
	}

	@Override
	public Long getOriginator() {
		return originator;
	}

	@Override
	public void setOriginator(Long originator) {
		this.originator = originator;
	}

	@Override
	public Long getBusinessOwner() {
		return null;
	}

	@Override
	public void setBusinessOwner(Long businessOwner) {

	}

	@Override
	public Date getEstimateLaunchDate() {
		return null;
	}

	@Override
	public void setEstimateLaunchDate(Date estimateLaunchDate) {

	}

	@Override
	public Long getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Override
	public Long getProjectManager() {
		return projectManager;
	}

	@Override
	public void setProjectManager(Long projectManager) {
		this.projectManager = projectManager;
	}

	@Override
	public Long getDevelopManager() {
		return developManager;
	}

	@Override
	public void setDevelopManager(Long developManager) {
		this.developManager = developManager;
	}

	@Override
	public Long getTestManager() {
		return testManager;
	}

	@Override
	public void setTestManager(Long testManager) {
		this.testManager = testManager;
	}

	@Override
	public Long getProductManager() {
		return productManager;
	}

	@Override
	public void setProductManager(Long productManager) {
		this.productManager = productManager;
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
	public Date getModified() {
		return modified;
	}

	@Override
	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Override
	public Long getApplyId() {
		return applyId;
	}

	@Override
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	@Override
	public Date getActualEndDate() {
		return actualEndDate;
	}

	@Override
	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	@Override
	public Date getCloseTime() {
		return closeTime;
	}

	@Override
	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	@Override
	public Long getDep() {
		return dep;
	}

	@Override
	public void setDep(Long dep) {
		this.dep = dep;
	}

}