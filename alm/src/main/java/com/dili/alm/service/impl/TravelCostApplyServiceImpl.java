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
		TravelCostApply apply = this.getActualDao().selectByPrimaryKey(dto.getId());
		// 更新差旅费明细，先删除后插入
		TravelCost tcQuery = DTOUtils.newDTO(TravelCost.class);
		tcQuery.setApplyId(dto.getId());
		this.travelCostMapper.delete(tcQuery);
		for (TravelCostDto t : dto.getTravelCost()) {
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
		// 插入差旅费明细
		for (TravelCostDto t : dto.getTravelCost()) {
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
		this.getActualDao().insert(dto);
	}

	@Override
	public void submit(Long applyId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void review(Long applyId, TravelCostApplyResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteTravelCostApply(Long applyId) {
		// TODO Auto-generated method stub

	}
}