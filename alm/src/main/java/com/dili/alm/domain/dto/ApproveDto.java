package com.dili.alm.domain.dto;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.dili.alm.domain.Approve;
import com.dili.alm.domain.DataDictionary;

public interface ApproveDto extends Approve {

	public boolean getIsApprove();

	public void setIsApprove(boolean isApprove);

}