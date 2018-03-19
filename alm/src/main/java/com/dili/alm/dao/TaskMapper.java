package com.dili.alm.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskByUsersDto;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.TaskEntity;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.ss.base.MyMapper;

public interface TaskMapper extends MyMapper<Task> {

	Integer countNotCompletedByPhaseId(Map<String,Object> query);

	List<Task> selectByProjectState(Integer value);

	int selectByTeamCount(@Param("task")Task task,@Param("selectOwner")Long selectOwner,@Param("ids")List<Long> ids);

	List<Task> selectByTeam(@Param("task")Task task,@Param("selectOwner")Long selectOwner,@Param("ids")List<Long> ids);

	List<ProjectChange> selectProjectChangeByTeam(@Param("selectOwner")Long selectOwner,@Param("projectId")Long projectId);
	
	List<Long> selectUserByTeam(@Param("selectOwner")Long selectOwner);
	
	List<ProjectVersion> selectVersionByTeam(@Param("selectOwner")Long selectOwner);
	
	List<Project> selectProjectByTeam(@Param("selectOwner")Long selectOwner);
	
	List<TaskDetails> selectOtherTaskDetail(@Param("selectOwner")Long selectOwner,@Param("selectDate")String selectDate);
	

	
	/***数据统计相关 begin***/
	List<TaskByUsersDto> selectTaskHourByUser(@Param("beginTime")String beginTime,@Param("endTime")String endTime,@Param("departmentId")Long departmentId,@Param("uId")Long uId);
	/***数据统计相关 end***/

	List<TaskStateCountDto>  selectTaskStateCount(@Param("list")List<Long> list);

}
