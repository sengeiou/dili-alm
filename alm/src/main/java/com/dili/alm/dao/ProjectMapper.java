package com.dili.alm.dao;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.ProjectStatusCountDto;
import com.dili.alm.domain.dto.ProjectTypeCountDTO;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.ss.base.MyMapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.ibatis.annotations.Param;

public interface ProjectMapper extends MyMapper<Project> {

	/**
	 * 获取子项目，包含当前参数id
	 * @param id
	 * @return
	 */
	List<Project> getChildProjects(Long id);
				  
	List<Project> getProjectsByTeam(@Param("project")Project project,@Param("owner")Long owner);
	
	int getPageByProjectCount(@Param("owner")Long owner);
		
	List<ProjectStatusCountDto> getTpyeByProjectCount(@Param("type")String type,@Param("startTime")String startTime,@Param("endTime")String endTime);
 
}