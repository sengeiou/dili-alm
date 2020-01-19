package com.dili.alm.domain.dto;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.dili.alm.component.ProcessHandleInfoDto;
import com.dili.alm.domain.Demand;
import com.dili.ss.domain.annotation.Operator;

public class DemandDto extends Demand implements ProcessHandleInfoDto {
	@Operator(Operator.IN)
	@Column(name = "`id`")
	List<Long> ids;

	Long departmentId;
	String departmentName;
	String departmentFirstName;
	String userPhone;
	String firmName;
	String userName;
	String startTime;
	String endTime;
	int processFlag;//流程编辑标识 1是可编辑，空为不可编辑
	@Transient
	@Column(name = "`content`")
	private String content;
	/**
	 * 当前登录人是否可以处理流程
	 */
	private Boolean isHandleProcess = false;
	private String formKey;
	private String taskId;

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getDepartmentFirstName() {
		return departmentFirstName;
	}

	public void setDepartmentFirstName(String departmentFirstName) {
		this.departmentFirstName = departmentFirstName;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(int processFlag) {
		this.processFlag = processFlag;
	}
	

	@Override
	public Boolean getIsHandleProcess() {
		return isHandleProcess;
	}

	@Override
	public void setIsHandleProcess(Boolean isHandleProcess) {
		this.isHandleProcess = isHandleProcess;
	}

	@Override
	public String getFormKey() {
		return formKey;
	}

	@Override
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}