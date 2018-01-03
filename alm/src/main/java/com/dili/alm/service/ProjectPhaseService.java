package com.dili.alm.service;

import java.util.List;
import java.util.Map;

import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectPhaseAddViewDto;
import com.dili.alm.domain.dto.ProjectPhaseEditViewDto;
import com.dili.alm.domain.dto.ProjectPhaseFormDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 16:05:32.
 */
public interface ProjectPhaseService extends BaseService<ProjectPhase, Long> {

	List<DataDictionaryValueDto> getPhaseNames();

	ProjectPhaseAddViewDto getAddViewData(Long projectId);

	BaseOutput<Object> addProjectPhase(ProjectPhaseFormDto projectPhase);

	List<Map> listEasyUiModels(ProjectPhase query);

	BaseOutput<Object> updateProjectPhase(ProjectPhaseFormDto projectPhase);

	ProjectPhaseEditViewDto getEditViewData(Long id);

	BaseOutput<Object> deleteWithOutput(Long id);
}