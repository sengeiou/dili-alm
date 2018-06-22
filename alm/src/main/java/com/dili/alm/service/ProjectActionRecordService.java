package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.domain.dto.ProjectActionRecordGanttDto;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-06-21 12:00:46.
 */
public interface ProjectActionRecordService extends BaseService<ProjectActionRecord, Long> {

	List<ProjectActionRecordGanttDto> getGanntData(Long projectId);
}