package com.dili.alm.service.impl;

import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.Task;
import com.dili.alm.service.TaskService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-22 15:55:41.
 */
@Service
public class TaskServiceImpl extends BaseServiceImpl<Task, Long> implements TaskService {

    public TaskMapper getActualDao() {
        return (TaskMapper)getDao();
    }
}