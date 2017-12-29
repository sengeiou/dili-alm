package com.dili.alm.dao;

import java.util.List;

import com.dili.alm.domain.Team;
import com.dili.ss.base.MyMapper;

public interface TeamMapper extends MyMapper<Team> {

	List<Team> listByProjectIds(List<Long> projectIds);

	int countProjectMember(Long projectId);
	
	List<Long> selectByProjectId(Long projectId);
}