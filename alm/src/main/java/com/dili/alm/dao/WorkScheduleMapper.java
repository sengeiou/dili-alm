package com.dili.alm.dao;

import java.util.List;

import com.dili.alm.domain.WorkSchedule;
import com.dili.alm.domain.WorkScheduleEntity;
import com.dili.alm.domain.dto.WorkScheduleDateDto;
import com.dili.ss.base.MyMapper;

public interface WorkScheduleMapper extends MyMapper<WorkSchedule> {
	
	List<WorkScheduleDateDto> selecGroupByDate(WorkSchedule workSchedule);
	List<WorkSchedule> selecByDateAndUser(WorkScheduleEntity workScheduleEntity);
}