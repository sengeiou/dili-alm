package com.dili.alm.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskEntity;
import com.dili.ss.base.MyMapper;

public interface TaskMapper extends MyMapper<Task> {

	Integer countNotCompletedByPhaseId(Map<String,Object> query);

	List<Task> selectByProjectState(Integer value);

	int selectByTeamCount(@Param("task")Task task,@Param("selectOwner")Long selectOwner,@Param("ids")List<Long> ids);

	List<Task> selectByTeam(@Param("task")Task task,@Param("selectOwner")Long selectOwner,@Param("ids")List<Long> ids);

	List<Project> selectProjectByTeam(@Param("selectOwner")Long selectOwner);
	
	List<Long> selectUserByTeam(@Param("selectOwner")Long selectOwner);
	
	List<ProjectVersion> selectVersionByTeam(@Param("selectOwner")Long selectOwner);
}