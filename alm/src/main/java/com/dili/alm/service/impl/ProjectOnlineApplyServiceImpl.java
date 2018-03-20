package com.dili.alm.service.impl;

import com.dili.alm.dao.ProjectOnlineApplyMapper;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.dto.ProjectOnlineApplyDto;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
import com.dili.alm.service.ProjectOnlineApplyDetailDto;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:31:10.
 */
@Service
public class ProjectOnlineApplyServiceImpl extends BaseServiceImpl<ProjectOnlineApply, Long>
		implements ProjectOnlineApplyService {

	public ProjectOnlineApplyMapper getActualDao() {
		return (ProjectOnlineApplyMapper) getDao();
	}

	@Override
	public void saveOrUpdate(ProjectOnlineApplyDto dto) {
		// TODO Auto-generated method stub

	}

	@Override
	public ProjectOnlineApplyDetailDto getEditViewDataById(Long applyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void submit(Long applyId) throws ProjectOnlineApplyException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startExecute(Long applyId, OperationResult result) throws ProjectOnlineApplyException {
		// TODO Auto-generated method stub

	}

	@Override
	public void testerConfirm(Long applyId, OperationResult result) throws ProjectOnlineApplyException {
		// TODO Auto-generated method stub

	}

	@Override
	public void excuteConfirm(Long applyId, OperationResult result) throws ProjectOnlineApplyException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validate(Long applyId) throws ProjectOnlineApplyException {
		// TODO Auto-generated method stub

	}
}