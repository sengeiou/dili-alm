package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.Task;
import com.dili.alm.domain.WorkSchedule;
import com.dili.alm.domain.WorkScheduleEntity;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:19:21.
 */
public interface WorkScheduleService extends BaseService<WorkSchedule, Long> {
	
	  List<WorkScheduleEntity> listWorkScheduleDto(WorkSchedule workSchedule) throws Exception;
}