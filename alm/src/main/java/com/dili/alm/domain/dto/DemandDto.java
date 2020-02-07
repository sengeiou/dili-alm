package com.dili.alm.domain.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.dili.alm.component.ProcessHandleInfoDto;
import com.dili.alm.domain.Demand;
import com.dili.ss.domain.annotation.Operator;

public class DemandDto extends Demand implements ProcessHandleInfoDto {
	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = -8773268871608713471L;

	@Operator(Operator.IN)
	@Column(name = "`id`")
	List<Long> ids;

	@Transient
	Long departmentId;
	@Transient
	String departmentName;
	@Transient
	String departmentFirstName;
	@Transient
	String userPhone;
	@Transient
	String firmName;
	@Transient
	String userName;
	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "create_date")
	Date startTime;
	@Operator(Operator.LITTLE_THAN)
	@Column(name = "create_date")
	Date endTime;
	@Transient
	int processFlag;// 流程编辑标识 1是可编辑，空为不可编辑
	@Transient
	@Column(name = "`content`")
	private String content;
	/**
	 * 当前登录人是否可以处理流程
	 */
	@Transient
	private Boolean isHandleProcess = false;
	@Transient
	private String formKey;
	@Transient
	private String taskId;
	@Transient
	private Boolean isNeedClaim;
	@Operator(Operator.NOT_EQUAL)
	@Column(name = "status")
	private Byte filterStatus;

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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
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

	@Override
	public Boolean getIsNeedClaim() {
		return isNeedClaim;
	}

	@Override
	public void setIsNeedClaim(Boolean isNeedClaim) {
		this.isNeedClaim = isNeedClaim;
	}

	public Byte getFilterStatus() {
		return filterStatus;
	}

	public void setFilterStatus(Byte filterStatus) {
		this.filterStatus = filterStatus;
	}

}