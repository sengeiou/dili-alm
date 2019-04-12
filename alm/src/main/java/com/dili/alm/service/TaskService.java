package com.dili.alm.service;

import java.util.Date;
import java.util.List;

import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.exceptions.ProjectVersionException;
import com.dili.alm.exceptions.TaskException;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.quartz.domain.ScheduleMessage;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:05.
 */
public interface TaskService extends BaseService<Task, Long> {

	EasyuiPageOutput listPageSelectTaskDto(Task task) throws Exception;

	void startTask(Long taskId, Long modifierId) throws TaskException, ProjectVersionException, ProjectException;

	void notComplateTask(ScheduleMessage msg);

	List<DataDictionaryValueDto> getTaskStates();

	List<DataDictionaryValueDto> getTaskTypes();

	boolean isProjectManager(Long projectId);
	
	/**
	 * 判断是否已经填写超过8小时
	 * 
	 * @param id       任务所有人
	 * @param taskHour 当期填写的工时
	 * @param modified 任务日
	 * @return
	 */
	boolean isSetTask(Long id, short taskHour, String modified);

	EasyuiPageOutput listByTeam(Task task);

	/**
	 * 校验任务开始和结束时间与项目时间
	 * 
	 * @param projectId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	boolean validateBeginAndEnd(Long projectId, Date startDate, Date endDate);

	List<ProjectChange> projectChangeList(Long projectId);

	List<User> listUserByTeam();

	List<ProjectVersion> listProjectVersionByTeam();

	List<User> listUserByProjectId(Long projectId);

	List<Task> listTaskByProjectId(Long projectId);

	/**
	 * 判断是否是项目经理
	 * 
	 * @param projectId 项目id
	 * @return
	 */
	boolean isThisProjectManager(Long projectId);

	boolean isCreater(Task task);

	List<TaskDetails> otherProjectTaskHour(String updateDate, Long owerId);

	/**
	 * 
	 * @param owerId   传入任务所有人
	 * @param modified 传入任务填写日期
	 * @return
	 */
	int restTaskHour(Long owerId, String modified);

	TaskDetails selectDetails(Long taskId);

	boolean isNoTeam();

	void addTask(Task task, Short planTime, Date startDateShow, Date endDateShow, Boolean flow, Long creatorId) throws TaskException;

	void updateTask(Task task, Long modifyMemberId, Short planTime, Date startDateShow, Date endDateShow, Boolean flow) throws TaskException;

	void deleteTask(Long id) throws TaskException;

	/**
	 * 提交工时
	 * 
	 * @param taskId   任务id
	 * @param operator TODO
	 * @param taskDate 任务日
	 * @param taskHour 正常工时
	 * @param overHour 加班工时
	 * @param content  工作内容
	 * @return TODO
	 * @throws TaskException
	 */
	Long submitWorkingHours(Long taskId, Long operator, Date taskDate, Short taskHour, Short overHour, String content) throws TaskException;

	/**
	 * 任务id
	 * 
	 * @param taskId     任务id
	 * @param operatorId 操作人id，只有任务责任人和项目经理可以提前结束任务
	 * @throws TaskException
	 */
	void completeTask(Long taskId, Long operatorId) throws TaskException;

	/**
	 * 判断任务日期是否在当前周内，任务责任人可以补填当前一周的工时
	 * 
	 * @param taskId 任务id
	 * @return
	 */
	boolean isInCurrentWeek(Long taskId);
}