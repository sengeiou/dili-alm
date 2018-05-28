package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import com.dili.alm.cache.AlmCache;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-24 14:31:10.
 */
@Component
public class WorkOrderPriorityProvider implements ValueProvider {

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		Map<String, String> map = AlmCache.getInstance().getWorkOrderPriorityMap();
		if (MapUtils.isEmpty(map)) {
			return new ArrayList<>(0);
		}
		List<ValuePair<?>> list = new ArrayList<>(map.size());
		map.entrySet().forEach(entry -> list.add(new ValuePairImpl<String>(entry.getValue(), entry.getKey())));
		return list;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals(""))
			return null;
		return AlmCache.getInstance().getWorkOrderPriorityMap().get(obj.toString());
	}
}