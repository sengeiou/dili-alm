package com.dili.alm.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.metadata.ValueProviderUtils;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:32:14.
 */
public interface DemandService extends BaseService<Demand, Long> {

	List<Demand> queryDemandListToProject(Long projectId);

	List<Demand> queryDemandListByIds(String ids);

	List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id, Integer type);

	DemandDto getDetailViewData(Long id) throws Exception;

	static Object parseViewModel(DemandDto detailViewData) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();
		
		JSONObject demandProjectProvider = new JSONObject();
		demandProjectProvider.put("provider", "demandProjectProvider");
		metadata.put("belongProId", demandProjectProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("userId", memberProvider);

		JSONObject demandStatusProvider = new JSONObject();
		demandStatusProvider.put("provider", "demandStatusProvider");
		metadata.put("status", demandStatusProvider);
		
		JSONObject demandTypeProvider = new JSONObject();
		demandTypeProvider.put("provider", "demandTypeProvider");
		metadata.put("type", demandTypeProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("createDate", datetimeProvider);
		metadata.put("submitDate", datetimeProvider);
		metadata.put("finishDate", datetimeProvider);

		List<Map> listMap = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(detailViewData));
		return listMap.get(0);
	}
}