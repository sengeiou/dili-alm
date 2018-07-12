package com.dili.alm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dili.alm.domain.ProjectActionRecord;
import com.dili.ss.base.MyMapper;

public interface ProjectActionRecordMapper extends MyMapper<ProjectActionRecord> {

	List<ProjectActionRecord> ganttQuery(@Param("projectId") Long projectId, @Param("versionIds") List<Long> versionIds,
			@Param("taskIds") List<Long> taskIds, @Param("actionCodes") List<String> actionCodes);
}