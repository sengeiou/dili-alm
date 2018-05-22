package com.dili.alm.service;

import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.exceptions.ProjectCompleteException;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-08 11:29:47.
 */
public interface ProjectCompleteService extends BaseService<ProjectComplete, Long> {

    void approve(ProjectComplete complete);

    Long reComplete(Long id);

    Object loadMembers(Long id) throws Exception;

	void insertWithCheck(ProjectComplete projectComplete) throws ProjectCompleteException;
}