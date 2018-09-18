package com.dili.alm.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.dto.ProjectYearCoverDto;
import com.dili.alm.domain.dto.ProjectYearCoverForAllDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserProjectDto;
import com.dili.alm.domain.dto.TaskByUsersDto;
import com.dili.alm.domain.dto.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.alm.domain.dto.UserWorkHourDetailDto;
import com.dili.ss.base.MyMapper;

public interface TaskMapper extends MyMapper<Task> {

	Integer countNotCompletedByPhaseId(Map<String, Object> query);

	List<Task> selectByProjectState(Integer value);

	int selectByTeamCount(@Param("task") Task task, @Param("selectOwner") Long selectOwner,
			@Param("ids") List<Long> ids);

	List<Task> selectByTeam(@Param("task") Task task, @Param("selectOwner") Long selectOwner,
			@Param("ids") List<Long> ids);

	List<ProjectChange> selectProjectChangeByTeam(@Param("selectOwner") Long selectOwner,
			@Param("projectId") Long projectId);

	List<Long> selectUserByTeam(@Param("selectOwner") Long selectOwner);

	List<ProjectVersion> selectVersionByTeam(@Param("selectOwner") Long selectOwner);

	List<Project> selectProjectByTeam(@Param("selectOwner") Long selectOwner);

	List<TaskDetails> selectOtherTaskDetail(@Param("selectOwner") Long selectOwner,
			@Param("selectDate") String selectDate);

	/***
	 * 数据统计相关 begin
	 * 
	 * @param order
	 *            TODO
	 * @param srot
	 *            TODO
	 ***/
	List<TaskByUsersDto> selectTaskHourByUser(@Param("beginTime") String beginTime, @Param("endTime") String endTime,
			@Param("departmentIds") List<Long> dids, @Param("uIds") List<Long> uids, @Param("order") String order,
			@Param("sort") String sort);

	List<TaskHoursByProjectDto> selectProjectHours(@Param("beginTime") String beginTime,
			@Param("endTime") String endTime, @Param("pids") List<Long> projectId);

	List<SelectTaskHoursByUserDto> selectUsersHours(@Param("beginTime") String beginTime,
			@Param("endTime") String endTime, @Param("pids") List<Long> projectId);

	List<SelectTaskHoursByUserProjectDto> selectUsersProjectHours(@Param("userIds") List<Long> userId,
			@Param("projectIds") List<Long> projectId, @Param("startTime") Date startTime,
			@Param("endTime") Date endDate);

	List<ProjectYearCoverDto> selectProjectYearsCover(@Param("beginTime") String beginTime,
			@Param("endTime") String endTime);

	ProjectYearCoverForAllDto selectProjectYearsCoverForAll(@Param("beginTime") String beginTime,
			@Param("endTime") String endTime);

	/*** 数据统计相关 end ***/

	List<TaskStateCountDto> selectTaskStateCount(@Param("list") List<Long> list);

	List<TaskStateCountDto> getStateByTaskCount(@Param("owner") Long owner, @Param("list") List<Long> list);

	List<Map<Object, Object>> sumUserProjectTaskHour(@Param("projectIds") List<Long> projectIds,
			@Param("userIds") Set<Long> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endDate);

	List<UserWorkHourDetailDto> selectUserWorkHourDetail(@Param("userId") Long userId);

	Long selectTotalTaskHourByUserId(@Param("userId") Long userId);
}
