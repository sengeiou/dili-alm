package com.dili.alm.domain.dto;

import java.util.Date;

import com.dili.alm.domain.ProjectOnlineOperationRecord;
import com.dili.ss.domain.BaseDomain;

public class ProjectOnlineOperationRecordDto extends BaseDomain implements ProjectOnlineOperationRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3259079666144431650L;
	private String managerTitle;
	private String description;
	private Date operateTime;
	private Integer opertateResult;
	private Long applyId;
	private String flowId;

	@Override
	public String getManagerTitle() {
		return managerTitle;
	}

	@Override
	public void setManagerTitle(String managerTitle) {
		this.managerTitle = managerTitle;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Date getOperateTime() {
		return operateTime;
	}

	@Override
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	@Override
	public Integer getOpertateResult() {
		return opertateResult;
	}

	@Override
	public void setOpertateResult(Integer opertateResult) {
		this.opertateResult = opertateResult;
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
	public String getFlowId() {
		return flowId;
	}

	@Override
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

}
