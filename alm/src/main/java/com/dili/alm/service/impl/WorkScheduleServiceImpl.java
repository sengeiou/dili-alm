package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.dili.alm.dao.WorkScheduleMapper;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.WorkSchedule;
import com.dili.alm.domain.WorkScheduleEntity;
import com.dili.alm.domain.dto.TaskSelectDto;
import com.dili.alm.service.WorkScheduleService;
import com.dili.ss.base.BaseServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:19:21.
 */
@Service
public class WorkScheduleServiceImpl extends BaseServiceImpl<WorkSchedule, Long> implements WorkScheduleService {

    public WorkScheduleMapper getActualDao() {
        return (WorkScheduleMapper)getDao();
    }

	@Override
	public List<WorkScheduleEntity> listWorkScheduleDto(WorkSchedule workSchedule)  {
		List<WorkSchedule> list = this.listByExample(workSchedule);//查询出来
		return this.ParseWorkScheduleEntity(list);
	}
	
	private List<WorkScheduleEntity> ParseWorkScheduleEntity(List<WorkSchedule> results){
		List<WorkScheduleEntity> target = new ArrayList<>(results.size());
		for (WorkSchedule entity : results) {
			WorkScheduleEntity workScheduleEntity = new WorkScheduleEntity(entity);
			target.add(workScheduleEntity);
		}
		return target;
	}
}