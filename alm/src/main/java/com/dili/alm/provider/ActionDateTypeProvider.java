package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dili.alm.domain.ActionDateType;
import com.dili.alm.domain.ProjectAction;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

@Component
public class ActionDateTypeProvider implements ValueProvider {

	@Override
	public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> list = new ArrayList<>(ProjectAction.values().length);
		for (ActionDateType type : ActionDateType.values()) {
			list.add(new ValuePairImpl<Integer>(type.getName(), type.getValue()));
		}
		return list;
	}

	@Override
	public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
		if (val == null) {
			return null;
		}
		for (ActionDateType type : ActionDateType.values()) {
			if (type.getValue().equals(val)) {
				return type.getName();
			}
		}
		return null;
	}

}
