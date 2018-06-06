package com.dili.alm.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.ProjectProgressDto;
import com.dili.alm.domain.dto.ProjectStatusCountDto;
import com.dili.ss.base.MyMapper;

public interface ProjectMapper extends MyMapper<Project> {

	/**
	 * 获取子项目，包含当前参数id
	 * 
	 * @param id
	 * @return
	 */
	List<Project> getChildProjects(Long id);

	List<Project> getProjectsByTeam(@Param("project") Project project, @Param("owner") Long owner);

	int getPageByProjectCount(@Param("owner") Long owner);

	List<ProjectStatusCountDto> getTpyeByProjectCount(@Param("type") String type, @Param("startTime") Date startTime,
			@Param("endTime") Date endTime, @Param("currentTime") Date currentTime);

	List<Long> getProjectIds(@Param("startTime") Date startTime, @Param("endTime") Date endTime,
			@Param("currentTime") Date currentTime);

	List<ProjectProgressDto> getProjectProgressList(@Param("project") Project project,
			@Param("list") List<Integer> list, @Param("flat") Integer flat);

	int getProjectProgressListCount(@Param("list") List<Integer> list, @Param("flat") Integer flat);

	int getProjectTypeAllCount(@Param("startTime") Date startTime, @Param("endTime") Date endTime,
			@Param("currentTime") Date currentTime);

	List<ProjectStatusCountDto> getStateByProjectCount(@Param("owner") Long owner, @Param("list") List<Long> list);

	List<Long> selectNotSubmitWeekly(@Param("startTime") String startTime, @Param("endTime") String endTime);
}