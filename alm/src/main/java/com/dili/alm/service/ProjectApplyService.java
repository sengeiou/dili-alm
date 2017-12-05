package com.dili.alm.service;

import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyFiles;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-21 16:19:02.
 */
public interface ProjectApplyService extends BaseService<ProjectApply, Long> {

    int insertApply(ProjectApply apply);

    List<DataDictionaryValueDto> getPlanPhase();

    BaseOutput submit(ProjectApply projectApply, ApplyFiles files);

}