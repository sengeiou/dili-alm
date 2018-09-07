package com.dili.alm.service;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.domain.WorkDay;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

public interface WorkDayService extends BaseService<WorkDay, Long> {

	BaseOutput upload(MultipartFile myfile,String year);

	WorkDay getNextWeeklyWorkDays();

	WorkDay getNowWeeklyWorkDay();

	List<String> getWorkDayYaers();

	WorkDay getNextWorkDay(Date date);

	BaseOutput showWorkDay(Long userId);
}
