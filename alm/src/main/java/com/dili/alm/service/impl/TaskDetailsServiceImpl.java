package com.dili.alm.service.impl;

import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.dto.UserTaskDetailsQueryDto;
import com.dili.alm.service.TaskDetailsService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:06.
 */
@Service
public class TaskDetailsServiceImpl extends BaseServiceImpl<TaskDetails, Long> implements TaskDetailsService {

	public TaskDetailsMapper getActualDao() {
		return (TaskDetailsMapper) getDao();
	}

}