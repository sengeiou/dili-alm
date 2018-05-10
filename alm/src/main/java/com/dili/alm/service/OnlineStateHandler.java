package com.dili.alm.service;

import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.ProjectOnlineApply;

public interface OnlineStateHandler {

	void setProjectOnlineApply(ProjectOnlineApply apply);

	ProjectOnlineApply getProjectOnlineApply();

	void operate(OperationResult result);
}
