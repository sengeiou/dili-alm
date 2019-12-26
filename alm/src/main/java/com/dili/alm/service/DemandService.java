package com.dili.alm.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.exceptions.DemandExceptions;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BasePage;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.metadata.ValueProviderUtils;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2019-12-23 17:32:14.
 */
public interface DemandService extends BaseService<Demand, Long> {

	/**
	 * 自定义添加新需求方法
	 * 
	 * @param newDemand
	 *            需要添加字段
	 * @return
	 */
	public int addNewDemand(Demand newDemand) throws DemandExceptions;

	/**
	 * 直接提交
	 * 
	 * @param newDemand
	 *            需要添加字段
	 * @return
	 */
	public int submint(Demand newDemand) throws DemandExceptions;

	/**
	 * 分页查询
	 * 
	 * @return 查询列表
	 */
	public EasyuiPageOutput listPageForUser(Demand selectDemand);

	/**
	 * 定时重新设置自增值
	 */
	public void setDailyBegin();

	List<Demand> queryDemandListToProject(Long projectId);

	List<Demand> queryDemandListByIds(String ids);

	List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id,
			Integer type);

	DemandDto getDetailViewData(Long id) throws Exception;

	
}