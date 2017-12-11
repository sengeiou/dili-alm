package com.dili.alm.dao;

import java.util.List;
import java.util.Map;

import com.dili.alm.domain.Task;
import com.dili.ss.base.MyMapper;

public interface TaskMapper extends MyMapper<Task> {

	Integer countNotCompletedByPhaseId(Map<String,Object> query);

	List<Task> selectByProjectState(Integer value);
}