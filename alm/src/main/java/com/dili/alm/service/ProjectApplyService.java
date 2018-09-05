package com.dili.alm.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.dili.alm.domain.Files;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.RoiUpdateDto;
import com.dili.alm.domain.dto.apply.ApplyFiles;
import com.dili.alm.domain.dto.apply.ApplyImpact;
import com.dili.alm.domain.dto.apply.ApplyRisk;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-21 16:19:02.
 */
public interface ProjectApplyService extends BaseService<ProjectApply, Long> {

	int insertApply(ProjectApply apply);

	List<DataDictionaryValueDto> getPlanPhase();

	void submit(ProjectApply projectApply, ApplyFiles files) throws ProjectApplyException;

	Long reApply(Long id);

	List<Files> listFiles(Long applyId);

	/**
	 * 构建立项申请第一步数据
	 *
	 * @param modelMap
	 * @param applyDTO
	 * @throws Exception
	 */
	void buildStepOne(Map modelMap, Map applyDTO) throws Exception;

	List<Map> loadPlan(Long id) throws Exception;

	List<ApplyImpact> loadImpact(Long id);

	public List<ApplyRisk> loadRisk(Long id);

	void updateRoi(RoiUpdateDto roi) throws ProjectApplyException;

	void deleteProjectApply(Long id) throws ProjectApplyException;

	void migrate() throws ParseException;
}