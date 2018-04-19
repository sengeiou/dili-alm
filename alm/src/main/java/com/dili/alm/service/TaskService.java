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
	
	
	/**
	 * 判断是否已经填写超过8小时
	 * @param id 任务所有人
	 * @param taskHour 当期填写的工时
	 * @param modified 任务日
	 * @return
	 */
	boolean isSetTask(Long id,short taskHour,String modified);
	
	boolean isManager(Long managerId);

	EasyuiPageOutput listByTeam(Task task, String phaseName);

	/**
	 * 校验任务开始和结束时间与项目时间
	 * @param projectId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	boolean validateBeginAndEnd(Long projectId,Date startDate,Date endDate);
	/**
	 * 权限，判断是否是项目经理
	 * @return
	 */
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
	
	/**
	 * 
	 * @param owerId   传入任务所有人
	 * @param modified 传入任务填写日期
	 * @return
	 */
	int restTaskHour(Long owerId,String modified);
	
	TaskDetails selectDetails(Long taskId);
	
	boolean isNoTeam();
	
	boolean isCommittee();
}