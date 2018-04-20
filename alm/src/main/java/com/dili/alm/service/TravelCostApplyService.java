package com.dili.alm.service;

import com.dili.alm.domain.TravelCostApply;
import com.dili.alm.domain.TravelCostApplyResult;
import com.dili.alm.domain.dto.TravelCostApplyUpdateDto;
import com.dili.alm.exceptions.TravelCostApplyException;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-04-24 16:43:13.
 */
public interface TravelCostApplyService extends BaseService<TravelCostApply, Long> {

	void saveOrUpdate(TravelCostApplyUpdateDto dto) throws TravelCostApplyException;

	void submit(Long applyId) throws TravelCostApplyException;

	void review(Long applyId, TravelCostApplyResult result) throws TravelCostApplyException;

	void deleteTravelCostApply(Long applyId) throws TravelCostApplyException;

	TravelCostApply getUpdateViewData(Long id) throws Exception;

	/**
	 * 保存并提交申请
	 * 
	 * @param apply
	 *            数据模型
	 * @throws TravelCostApplyException 
	 */
	void saveAndSubmit(TravelCostApplyUpdateDto apply) throws TravelCostApplyException;
}