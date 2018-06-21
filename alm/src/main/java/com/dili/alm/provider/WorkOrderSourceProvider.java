package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dili.alm.domain.WorkOrderSource;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-24 14:31:10.
 */
@Component
public class WorkOrderSourceProvider implements ValueProvider {

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> list = new ArrayList<>(WorkOrderSource.values().length);
		for (WorkOrderSource source : WorkOrderSource.values()) {
			list.add(new ValuePairImpl<Integer>(source.getName(), source.getValue()));
		}
		return list;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals("")) {
			return null;
		}
		for (WorkOrderSource source : WorkOrderSource.values()) {
			if (source.getValue().equals(obj)) {
				return source.getName();
			}
		}
		return null;
	}
}