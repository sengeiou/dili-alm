package com.dili.alm.service;

import com.dili.alm.domain.InsertProjectVersionDto;
import com.dili.alm.domain.ProjectVersion;
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
	 * @param projectVersion
	 * @return
	 */
	BaseOutput updateSelectiveWithOutput(ProjectVersion projectVersion);

	/**
	 *
	 * @param id
	 * @return
	 */
	BaseOutput deleteWithOutput(Long id);
}