package com.dili.alm.service.impl;

import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.ss.base.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 16:05:32.
 */
@Service
public class ProjectPhaseServiceImpl extends BaseServiceImpl<ProjectPhase, Long> implements ProjectPhaseService {

	private static final String PHASE_NAME_CODE = "phase_name";
	@Autowired
	private DataDictionaryService dataDictionaryService;

	public ProjectPhaseMapper getActualDao() {
		return (ProjectPhaseMapper) getDao();
	}

	@Override
	public List<DataDictionaryValueDto> getPhaseNames() {
		DataDictionaryDto dataDictionary = this.dataDictionaryService.findByCode(PHASE_NAME_CODE);
		if (dataDictionary == null) {
			return null;
		}
		return dataDictionary.getValues();
	}
}