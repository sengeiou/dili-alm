package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;

import java.util.Date;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public Integer getProjectState() {
		return projectState;
	}

	public void setProjectState(Integer projectState) {
		this.projectState = projectState;
	}

	public Integer getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(Integer taskCount) {
		this.taskCount = taskCount;
	}

	public Integer getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}

	public Integer getCompletedProgress() {
		return completedProgress;
	}

	public void setCompletedProgress(Integer completedProgress) {
		this.completedProgress = completedProgress;
	}

	public Long getOriginator() {
		return originator;
	}

	public void setOriginator(Long originator) {
		this.originator = originator;
	}

	@Override
	public Long getDep() {
		return null;
	}

	@Override
	public void setDep(Long dep) {

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

	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}


}