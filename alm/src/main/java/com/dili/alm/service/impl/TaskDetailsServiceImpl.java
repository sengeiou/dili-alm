package com.dili.alm.service.impl;

import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.service.TaskDetailsService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-22 16:02:19.
 */
@Service
public class TaskDetailsServiceImpl extends BaseServiceImpl<TaskDetails, Long> implements TaskDetailsService {

    public TaskDetailsMapper getActualDao() {
        return (TaskDetailsMapper)getDao();
    }
}