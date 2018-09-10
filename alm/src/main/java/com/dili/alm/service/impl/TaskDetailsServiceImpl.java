package com.dili.alm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.dto.UserWorkHourDetailDto;
import com.dili.alm.service.TaskDetailsService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseDomain;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:06.
 */
@Service
public class TaskDetailsServiceImpl extends BaseServiceImpl<TaskDetails, Long> implements TaskDetailsService {

	@Autowired
	private TaskMapper taskMapper;

	public TaskDetailsMapper getActualDao() {
		return (TaskDetailsMapper) getDao();
	}

	@Override
	public Page<UserWorkHourDetailDto> listUserWorkHourDetail(Long userId, BaseDomain query) {
		PageHelper.startPage(query.getPage(), query.getRows());
		Page<UserWorkHourDetailDto> page = (Page<UserWorkHourDetailDto>) this.taskMapper.selectUserWorkHourDetail(userId);
		return page;
	}

}