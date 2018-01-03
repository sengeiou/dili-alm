package com.dili.alm.dao;

import java.util.List;

import com.dili.alm.domain.ProjectPhase;
import com.dili.ss.base.MyMapper;

public interface ProjectPhaseMapper extends MyMapper<ProjectPhase> {

	List<ProjectPhase> selectByProjectState(Integer value);
}