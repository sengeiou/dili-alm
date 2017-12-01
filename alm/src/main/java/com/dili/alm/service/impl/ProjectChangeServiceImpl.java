package com.dili.alm.service.impl;

import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.service.ProjectChangeService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-30 17:21:55.
 */
@Service
public class ProjectChangeServiceImpl extends BaseServiceImpl<ProjectChange, Long> implements ProjectChangeService {

    public ProjectChangeMapper getActualDao() {
        return (ProjectChangeMapper)getDao();
    }
}