package com.dili.alm.dao;

import com.dili.alm.domain.TaskDetails;
import com.dili.ss.base.MyMapper;

public interface TaskDetailsMapper extends MyMapper<TaskDetails> {

	long sumTaskHourByProject(Long projectId);
	
	long sumTaskAndOverHourByVersion(Long versionId);
	
}