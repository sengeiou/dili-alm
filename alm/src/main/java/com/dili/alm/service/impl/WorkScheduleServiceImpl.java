package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.dili.alm.dao.WorkScheduleMapper;
import com.dili.alm.domain.WorkSchedule;
import com.dili.alm.domain.WorkScheduleEntity;
import com.dili.alm.domain.dto.WorkScheduleDateDto;
import com.dili.alm.service.WorkScheduleService;
import com.dili.ss.base.BaseServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:19:21.
 */
@Service
public class WorkScheduleServiceImpl extends BaseServiceImpl<WorkSchedule, Long> implements WorkScheduleService {

	@Autowired
	WorkScheduleMapper workScheduleMapper;
	
    public WorkScheduleMapper getActualDao() {
        return (WorkScheduleMapper)getDao();
    }

	@Override
	public List<WorkScheduleEntity> listWorkScheduleDto(WorkSchedule workSchedule)  {
		WorkScheduleEntity workScheduleEntity = new WorkScheduleEntity(workSchedule);
		List<WorkSchedule> list = workScheduleMapper.selecByDateAndUser(workScheduleEntity);//查询出来
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

	@Override
	public List<WorkScheduleDateDto> listWorkScheduleDate(
			WorkSchedule workSchedule)  {
		List<WorkScheduleDateDto> list = null;
		try {
			list = workScheduleMapper.selecGroupByDate(workSchedule);//查询出来
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
}