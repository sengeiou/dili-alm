package com.dili.alm.dao;

import java.util.List;

import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.ss.base.MyMapper;

public interface ProjectOnlineApplyMapper extends MyMapper<ProjectOnlineApply> {
	
	
	List<ProjectOnlineApply>  selectProjectOnlineApplyByExecutorId(Long id );
}