package com.dili.alm.dao;

import com.dili.alm.domain.TaskDetails;
import com.dili.ss.base.MyMapper;

public interface TaskDetailsMapper extends MyMapper<TaskDetails> {

	Long sumTaskHourByProject(Long projectId);
	
	Long sumTaskAndOverHourByVersion(Long versionId);
	
}