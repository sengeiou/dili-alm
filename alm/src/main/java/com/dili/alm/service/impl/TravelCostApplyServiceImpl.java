package com.dili.alm.service.impl;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.TravelCostApplyMapper;
import com.dili.alm.dao.TravelCostDetailMapper;
import com.dili.alm.dao.TravelCostMapper;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.TravelCost;
import com.dili.alm.domain.TravelCostApply;
import com.dili.alm.domain.TravelCostApplyResult;
import com.dili.alm.domain.TravelCostApplyState;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.TravelCostApplyUpdateDto;
import com.dili.alm.domain.dto.TravelCostDto;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.TravelCostApplyException;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.TravelCostApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-04-24 16:43:13.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class TravelCostApplyServiceImpl extends BaseServiceImpl<TravelCostApply, Long>
		implements TravelCostApplyService {

	@Autowired
	private UserRpc userRpc;
	@Autowired
	private TravelCostMapper travelCostMapper;
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private TravelCostDetailMapper travelCostDetailMapper;

	public TravelCostApplyMapper getActualDao() {
		return (TravelCostApplyMapper) getDao();
	}

	@Override
	public void saveOrUpdate(TravelCostApplyUpdateDto dto) throws TravelCostApplyException {
		if (dto.getId() == null) {
			this.insertTravelCostApply(dto);
		} else {
			this.updateTravelCostApply(dto);
		}
	}

	private void updateTravelCostApply(TravelCostApplyUpdateDto dto) throws TravelCostApplyException {
		// 更新差旅费明细，先删除后插入
		TravelCost tcQuery = DTOUtils.newDTO(TravelCost.class);
		tcQuery.setApplyId(dto.getId());
		this.travelCostMapper.delete(tcQuery);
		int travelDayAmount = 0;
		for (TravelCostDto t : dto.getTravelCost()) {
			travelDayAmount += t.getTravelDayAmount();
			// 插入费用明细
			int rows = this.travelCostDetailMapper.insertList(t.getTravelCostDetail());
			if (rows <= 0) {
				throw new TravelCostApplyException("插入费用明细失败");
			}
			rows = this.travelCostMapper.insert(t);
			if (rows <= 0) {
				throw new TravelCostApplyException("插入差旅费明细失败");
			}
		}
		dto.setTravelDayAmount(travelDayAmount);
		this.getActualDao().insert(dto);
	}

	private void insertTravelCostApply(TravelCostApplyUpdateDto dto) throws TravelCostApplyException {
		BaseOutput<User> output = this.userRpc.findUserById(dto.getApplicantId());
		if (output == null || !output.isSuccess()) {
			throw new TravelCostApplyException("查询申请人信息失败");
		}
		User applicant = output.getData();
		if (applicant == null) {
			throw new TravelCostApplyException("申请人不存在");
		}
		Department dept = AlmCache.getInstance().getDepMap().get(applicant.getDepartmentId());
		dto.setDepartmentId(dept.getId());
		while (dept.getParentId() != null) {
			dept = AlmCache.getInstance().getDepMap().get(dept.getParentId());
		}
		dto.setRootDepartemntId(dept.getId());
		int travelDayAmount = 0;
		// 插入差旅费明细
		for (TravelCostDto t : dto.getTravelCost()) {
			travelDayAmount += t.getTravelDayAmount();
			// 插入费用明细
			int rows = this.travelCostDetailMapper.insertList(t.getTravelCostDetail());
			if (rows <= 0) {
				throw new TravelCostApplyException("插入费用明细失败");
			}
			rows = this.travelCostMapper.insert(t);
			if (rows <= 0) {
				throw new TravelCostApplyException("插入差旅费明细失败");
			}
		}
		dto.setTravelDayAmount(travelDayAmount);
		this.getActualDao().insert(dto);
	}

	@Override
	public void submit(Long applyId) throws TravelCostApplyException {
		TravelCostApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new TravelCostApplyException("差旅成本不存在");
		}
		if (!apply.getApplyState().equals(TravelCostApplyState.APPLING.getValue())) {
			throw new TravelCostApplyException("当前状态不能提交");
		}
		apply.setApplyState(TravelCostApplyState.REVIEWING.getValue());
		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (rows <= 0) {
			throw new TravelCostApplyException("更新状态失败");
		}

	}

	@Override
	public void review(Long applyId, TravelCostApplyResult result) throws TravelCostApplyException {
		TravelCostApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new TravelCostApplyException("差旅成本不存在");
		}
		if (!apply.getApplyState().equals(TravelCostApplyState.REVIEWING.getValue())) {
			throw new TravelCostApplyException("当前状态不能进行复核操作");
		}
		if (TravelCostApplyResult.REJECT.equals(result)) {
			apply.setApplyState(TravelCostApplyState.APPLING.getValue());
		}
		if (TravelCostApplyResult.PASS.equals(result)) {
			apply.setApplyState(TravelCostApplyState.COMPLETED.getValue());
		}
		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (rows <= 0) {
			throw new TravelCostApplyException("更新状态失败");
		}
	}

	@Override
	public void deleteTravelCostApply(Long applyId) throws TravelCostApplyException {
		TravelCostApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new TravelCostApplyException("差旅成本不存在");
		}
		if (!apply.getApplyState().equals(TravelCostApplyState.APPLING.getValue())) {
			throw new TravelCostApplyException("当前状态不能进行删除操作");
		}
		int rows = this.getActualDao().deleteByPrimaryKey(applyId);
		if (rows <= 0) {
			throw new TravelCostApplyException("删除失败");
		}
	}
}