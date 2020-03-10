package com.dili.alm.domain.dto;

import com.dili.alm.component.ProcessHandleInfoDto;
import com.dili.alm.domain.Approve;

public interface ApproveDto extends Approve,ProcessHandleInfoDto {

	public Boolean getIsHandleProcess();

	public void setIsHandleProcess(Boolean isHandleProcess);
	
	public Integer getIsSameAssignee();

	public void setIsSameAssignee(Integer isSameAssignee);

}