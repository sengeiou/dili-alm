package com.dili.alm.service.impl;

import com.dili.alm.dao.ProjectCompleteMapper;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.service.ProjectCompleteService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-08 11:29:47.
 */
@Service
public class ProjectCompleteServiceImpl extends BaseServiceImpl<ProjectComplete, Long> implements ProjectCompleteService {

    public ProjectCompleteMapper getActualDao() {
        return (ProjectCompleteMapper)getDao();
    }
}