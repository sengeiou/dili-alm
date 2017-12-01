package com.dili.alm.service;

import com.dili.alm.domain.InsertProjectVersionDto;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.dto.ProjectVersionChangeStateViewDto;
import com.dili.alm.domain.dto.UpdateProjectVersionDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-10-20 11:02:17.
 */
public interface ProjectVersionService extends BaseService<ProjectVersion, Long> {

	/**
	 * @param dto TODO
	 * @return
	 */
	BaseOutput insertSelectiveWithOutput(InsertProjectVersionDto dto);

	/**
	 * @param dto
	 * @return
	 */
	BaseOutput updateSelectiveWithOutput(UpdateProjectVersionDto dto);

	/**
	 *
	 * @param id
	 * @return
	 */
	BaseOutput deleteWithOutput(Long id);

	ProjectVersionChangeStateViewDto getChangeStateViewData(Long id);

	BaseOutput<Object> changeState(Long id, Integer versionState);
}