package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

import java.util.Date;

import javax.persistence.*;

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
	private	Integer projectState;
	private Integer taskCount;
	private Integer memberCount;
	private Integer completedProgress;
	private Long originator;
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
	public Long getParentId() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setParentId(Long parentId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Long getProjectManager() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setProjectManager(Long projectManager) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getDevelopManager() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setDevelopManager(String developManager) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Long getTestManager() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setTestManager(Long testManager) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Long getProductManager() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setProductManager(Long productManager) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Date getCreated() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setCreated(Date created) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Date getModified() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setModified(Date modified) {
		// TODO Auto-generated method stub
		
	}
}