package com.dili.alm.service;

import com.dili.alm.domain.Files;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyFiles;
import com.dili.alm.domain.dto.apply.ApplyImpact;
import com.dili.alm.domain.dto.apply.ApplyRisk;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-21 16:19:02.
 */
public interface ProjectApplyService extends BaseService<ProjectApply, Long> {

    int insertApply(ProjectApply apply);

    List<DataDictionaryValueDto> getPlanPhase();

    BaseOutput submit(ProjectApply projectApply, ApplyFiles files);
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
}