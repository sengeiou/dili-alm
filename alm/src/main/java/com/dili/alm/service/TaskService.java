package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:23:05.
 */
public interface TaskService extends BaseService<Task, Long> {

	List<Project> selectByOwner(Long ownerId);

	EasyuiPageOutput listPageSelectTaskDto(Task task) throws Exception;
	
	int updateTaskDetail(TaskDetails taskDetails ,Task task);
	
	int startTask(Task task);
	
	void notComplateTask();

	List<DataDictionaryValueDto> getTaskStates();

	List<DataDictionaryValueDto> getTaskTypes();
	
	boolean isSetTask(Long id,short taskHour);
	
	boolean isManager(Long managerId);

	EasyuiPageOutput listByTeam(Task task, String phaseName);
	

}