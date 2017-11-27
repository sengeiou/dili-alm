package com.dili.alm.dao;

import com.dili.alm.domain.Project;
import com.dili.ss.base.MyMapper;

import java.util.List;

public interface ProjectMapper extends MyMapper<Project> {

	/**
	 * 获取子项目，包含当前参数id
	 * @param id
	 * @return
	 */
	List<Project> getChildProjects(Long id);

	List<Project> selectByTaskOwner(Long ownerId);
}