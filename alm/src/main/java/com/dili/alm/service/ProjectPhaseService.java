package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-30 16:05:32.
 */
public interface ProjectPhaseService extends BaseService<ProjectPhase, Long> {

	List<DataDictionaryValueDto> getPhaseNames();
}