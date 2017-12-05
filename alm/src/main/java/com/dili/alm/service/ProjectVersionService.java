package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.Files;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.ProjectVersionFormDto;
import com.dili.alm.domain.dto.ProjectVersionChangeStateViewDto;
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
	BaseOutput insertSelectiveWithOutput(ProjectVersionFormDto dto);

	/**
	 * @param dto
	 * @return
	 */
	BaseOutput updateSelectiveWithOutput(ProjectVersionFormDto dto);

	/**
	 *
	 * @param id
	 * @return
	 */
	BaseOutput deleteWithOutput(Long id);

	ProjectVersionChangeStateViewDto getChangeStateViewData(Long id);

	BaseOutput<Object> changeState(Long id, Integer versionState);

	List<Files> listFiles(Long versionId);
}