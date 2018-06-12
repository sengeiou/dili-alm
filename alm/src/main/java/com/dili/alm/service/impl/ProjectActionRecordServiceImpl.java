package com.dili.alm.service.impl;

import com.dili.alm.dao.ProjectActionRecordMapper;
import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.service.ProjectActionRecordService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-06-12 14:36:05.
 */
@Service
public class ProjectActionRecordServiceImpl extends BaseServiceImpl<ProjectActionRecord, Long> implements ProjectActionRecordService {

    public ProjectActionRecordMapper getActualDao() {
        return (ProjectActionRecordMapper)getDao();
    }
}