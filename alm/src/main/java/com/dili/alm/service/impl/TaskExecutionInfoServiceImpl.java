package com.dili.alm.service.impl;

import com.dili.alm.dao.TaskExecutionInfoMapper;
import com.dili.alm.domain.TaskExecutionInfo;
import com.dili.alm.service.TaskExecutionInfoService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2017-11-22 16:02:20.
 */
@Service
public class TaskExecutionInfoServiceImpl extends BaseServiceImpl<TaskExecutionInfo, Long> implements TaskExecutionInfoService {

    public TaskExecutionInfoMapper getActualDao() {
        return (TaskExecutionInfoMapper)getDao();
    }
}