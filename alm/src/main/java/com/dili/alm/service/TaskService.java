package com.dili.alm.service;

import java.util.Date;
import java.util.List;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:23:05.
 */
public interface TaskService extends BaseService<Task, Long> {

	EasyuiPageOutput listPageSelectTaskDto(Task task) throws Exception;
	
	int updateTaskDetail(TaskDetails taskDetails ,Task task);
	
	int startTask(Task task);
	
	void notComplateTask();

	List<DataDictionaryValueDto> getTaskStates();

	List<DataDictionaryValueDto> getTaskTypes();
	
	boolean isSetTask(Long id,short taskHour);
	
	boolean isManager(Long managerId);

	EasyuiPageOutput listByTeam(Task task, String phaseName);

	boolean validateBeginAndEnd(Long projectId,Date startDate,Date endDate);
	
	boolean isProjectManager();
	
	List<ProjectChange> projectChangeList(Long projectId);
	
	List<Project> projectList();

	List<User> listUserByTeam();
	
	List<ProjectVersion> listProjectVersionByTeam();
	
	List<User> listUserByProjectId(Long projectId);
	
	List<Task> listTaskByProjectId(Long projectId);
	
	boolean isThisProjectManger(Long projectId);
	
	boolean isCreater(Task task);

	List<TaskDetails> otherProjectTaskHour(String updateDate,Long owerId);
	
	int restTaskHour(Long owerId);
	
	TaskDetails selectDetails(Long taskId);
	
	boolean isNoTeam();
}