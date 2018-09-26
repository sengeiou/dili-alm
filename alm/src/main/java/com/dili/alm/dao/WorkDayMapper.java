package com.dili.alm.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dili.alm.domain.WorkDay;
import com.dili.ss.base.MyMapper;

public interface WorkDayMapper extends MyMapper<WorkDay> {

	List<String> getWorkYear();

	WorkDay getWorkDayNowDate(@Param("nowDate") String nowDate);

	WorkDay getMaxOrMinWeekWorkDay(@Param("falt") Integer falt, @Param("year") String yaer);

	Integer deteleWorkDaysByYear(@Param("year") String year);

	Integer countByStartAndEndDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}