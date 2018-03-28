package com.dili.alm.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.domain.WorkDay;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

public interface WorkDayService extends BaseService<WorkDay, Long> {

	boolean upload(MultipartFile myfile,String year);

	WorkDay getNextWeeklyWorkDays();

	WorkDay getNowWeeklyWorkDay();

	List<String> getWorkDayYaers();
	
}
