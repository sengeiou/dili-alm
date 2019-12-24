package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.Demand;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:32:14.
 */
public interface DemandService extends BaseService<Demand, Long> {

	List<Demand> queryDemandListToProject(Long projectId);

	List<Demand> queryDemandListByIds(String ids);

	List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id, Integer type);
}