package com.dili.alm.service.impl;

import com.dili.alm.dao.ProjectActionRecordConfigMapper;
import com.dili.alm.domain.ProjectActionRecordConfig;
import com.dili.alm.service.ProjectActionRecordConfigService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-07-09 15:58:15.
 */
@Service
public class ProjectActionRecordConfigServiceImpl extends BaseServiceImpl<ProjectActionRecordConfig, Long> implements ProjectActionRecordConfigService {

    public ProjectActionRecordConfigMapper getActualDao() {
        return (ProjectActionRecordConfigMapper)getDao();
    }
}