package com.dili.alm.service;

import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.dto.DemandProjectDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:32:14.
 */
public interface DemandProjectService extends BaseService<DemandProject, Long> {

	BaseOutput insertDemandProjectDto(DemandProjectDto demandProjectDto);

	BaseOutput deleteByProjectOrVersionOrWorkOrder(Long projectId, Long versionId, Long workOrderId);
}