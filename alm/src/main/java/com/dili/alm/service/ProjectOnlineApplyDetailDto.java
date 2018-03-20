package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.dto.ProjectOnlineOperationRecordDto;
import com.dili.alm.domain.dto.ProjectOnlineApplyDto;

public class ProjectOnlineApplyDetailDto extends ProjectOnlineApplyDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4917155891264212812L;

	private List<ProjectOnlineOperationRecordDto> operations;

	public List<ProjectOnlineOperationRecordDto> getOperations() {
		return operations;
	}

	public void setOperations(List<ProjectOnlineOperationRecordDto> operations) {
		this.operations = operations;
	}

}
