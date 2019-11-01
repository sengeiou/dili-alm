package com.dili.alm.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.domain.DataDictionary;
import com.dili.alm.domain.User;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderSource;
import com.dili.alm.domain.WorkOrderState;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.alm.service.DataDictionaryService;
import com.dili.ss.base.BaseService;

@Component
public class OutsideWorkOrderManager extends BaseWorkOrderManager {

	private static final String OUTSIDE_SWITCH = "outside_work_order_switch";
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private WorkOrderMapper workOrderMapper;

	@Override
	public void submit(WorkOrder workOrder) throws WorkOrderException {
		// 外部工单
		workOrder.setWorkOrderState(WorkOrderState.OPENED.getValue());
		Date now = new Date();
		workOrder.setSubmitTime(now);
		workOrder.setModifyTime(now);
		int rows = this.workOrderMapper.updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("提交工单失败");
		}

		Map<Long, User> userMap = AlmCache.getInstance().getUserMap();
		User receiver = userMap.get(workOrder.getAcceptorId());
		Set<String> emails = new HashSet<>();
		if (receiver != null) {
			emails.add(receiver.getEmail());
		}
		if (StringUtils.isNotBlank(workOrder.getCopyUserId())) {
			List<Long> userIds = JSON.parseArray(workOrder.getCopyUserId(), Long.class);
			userIds.forEach(uid -> {
				User user = userMap.get(uid);
				if (user != null) {
					emails.add(user.getEmail());
				}
			});
		}
		this.sendMail(workOrder, "工单申请", emails);
	}

	@Override
	public void update(WorkOrder workOrder) throws WorkOrderException {
		workOrder.setExecutorId(null);
		this.updateExactSimple(workOrder);
		int rows = this.updateExactSimple(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("新增工单失败");
		}
	}

	@Override
	public List<User> getReceivers() {
		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.WORK_ORDER_MEMBER_FILTER_SWITCH);
		if (dd == null) {
			return null;
		}
		if (CollectionUtils.isEmpty(dd.getValues())) {
			return null;
		}
		DataDictionaryValueDto ddValue = dd.getValues().stream().filter(v -> v.getCode().equals(OUTSIDE_SWITCH)).findFirst().orElse(null);
		if (ddValue == null) {
			return null;
		}
		if (Boolean.valueOf(ddValue.getValue())) {
			dd = this.ddService.findByCode(AlmConstants.OUTSIDE_WORK_ORDER_RECEIVERS_CODE);
			if (dd == null || CollectionUtils.isEmpty(dd.getValues())) {
				return null;
			}
			List<User> target = new ArrayList<>(dd.getValues().size());
			dd.getValues().forEach(v -> {
				Map.Entry<Long, User> entry = AlmCache.getInstance().getUserMap().entrySet().stream().filter(e -> e.getValue().getUserName().equals(v.getValue())).findFirst().orElse(null);
				if (entry != null) {
					target.add(entry.getValue());
				}
			});
			return target;
		}
		return new ArrayList<>(AlmCache.getInstance().getUserMap().values());
	}

	@Override
	public WorkOrderSource getType() {
		return WorkOrderSource.OUTSIDE;
	}

}
