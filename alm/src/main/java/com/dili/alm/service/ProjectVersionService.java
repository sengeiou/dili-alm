package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.Files;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.ProjectVersionFormDto;
import com.dili.alm.domain.dto.ProjectVersionChangeStateViewDto;
import com.dili.alm.exceptions.ProjectVersionException;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-20 11:02:17.
 */
public interface ProjectVersionService extends BaseService<ProjectVersion, Long> {

	/**
	 * @param dto
	 * @throws ProjectVersionException
	 */
	void addProjectVersion(ProjectVersionFormDto dto) throws ProjectVersionException;

	/**
	 * @param dto
	 * @throws ProjectVersionException
	 */
	void updateProjectVersion(ProjectVersionFormDto dto) throws ProjectVersionException;

	/**
	 *
	 * @param id
	 * @throws ProjectVersionException
	 */
	void deleteProjectVersion(Long id) throws ProjectVersionException;

	List<Files> listFiles(Long versionId);

	/**
	 * 暂停
	 * 
	 * @param id
	 * @throws ProjectVersionException
	 */
	void pause(Long id) throws ProjectVersionException;

	/**
	 * 重启
	 * 
	 * @param id
	 * @throws ProjectVersionException
	 */
	void resume(Long id) throws ProjectVersionException;

	/**
	 * 完成
	 * 
	 * @param id
	 * @throws ProjectVersionException 
	 */
	void complete(Long id) throws ProjectVersionException;
}