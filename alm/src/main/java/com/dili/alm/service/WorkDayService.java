package com.dili.alm.service;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.domain.WorkDay;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

public interface WorkDayService extends BaseService<WorkDay, Long> {
	/**
	 * 保存对应年份的文件
	 * @param myfile
	 * @param year
	 * @return
	 */
	BaseOutput upload(MultipartFile myfile,String year);
	/**
	 * 获取下个周的工作时间
	 * @return
	 */
	WorkDay getNextWeeklyWorkDays();
	/**
	 * 获取本周的工作时间
	 * @return
	 */
	WorkDay getNowWeeklyWorkDay();
	/**
	 * 获取工作时间的年份
	 * @return
	 */
	List<String> getWorkDayYaers();
	/**
	 * 根据时间获取对应的下一年的工作日
	 * @param date
	 * @return
	 */
	WorkDay getNextWorkDay(Date date);
	/**
	 * 根据用户Id获取显示时间
	 * @param userId
	 * @return
	 */
	BaseOutput showWorkDay(Long userId);
}
