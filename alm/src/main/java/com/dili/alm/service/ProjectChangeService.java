package com.dili.alm.service;

import com.dili.alm.domain.ProjectChange;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 17:21:55.
 */
public interface ProjectChangeService extends BaseService<ProjectChange, Long> {

	void approve(ProjectChange change) throws ProjectApplyException;

	Long reChange(Long id);

	void addProjectChange(ProjectChange projectChange) throws ApplicationException;

	void updateProjectChange(ProjectChange projectChange) throws ApplicationException;
}