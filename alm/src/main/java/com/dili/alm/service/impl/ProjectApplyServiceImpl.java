package com.dili.alm.service.impl;

import com.dili.alm.cache.ProjectNumberGenerator;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.SequenceMapper;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.service.ProjectApplyService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-21 16:19:02.
 */
@Service
public class ProjectApplyServiceImpl extends BaseServiceImpl<ProjectApply, Long> implements ProjectApplyService {

    @Autowired
    private ProjectNumberGenerator projectNumberGenerator;

    public ProjectApplyMapper getActualDao() {
        return (ProjectApplyMapper) getDao();
    }
}