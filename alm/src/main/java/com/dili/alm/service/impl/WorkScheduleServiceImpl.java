package com.dili.alm.service.impl;

import com.dili.alm.dao.WorkScheduleMapper;
import com.dili.alm.domain.WorkSchedule;
import com.dili.alm.service.WorkScheduleService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2017-11-22 16:28:58.
 */
@Service
public class WorkScheduleServiceImpl extends BaseServiceImpl<WorkSchedule, Long> implements WorkScheduleService {

    public WorkScheduleMapper getActualDao() {
        return (WorkScheduleMapper)getDao();
    }
}