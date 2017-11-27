package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.Task;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:23:05.
 */
public interface TaskService extends BaseService<Task, Long> {

	List<Project> selectByOwner(Long ownerId);
}