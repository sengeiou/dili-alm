package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dili.alm.domain.ProjectVersionState;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

@Component
public class ProjectVersionStateProvider implements ValueProvider {

	@Override
	public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> list = new ArrayList<>(ProjectVersionState.values().length);
		for (ProjectVersionState state : ProjectVersionState.values()) {
			list.add(new ValuePairImpl<Integer>(state.getName(), state.getValue()));
		}
		return list;
	}

	@Override
	public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
		if (val == null) {
			return null;
		}
		for (ProjectVersionState state : ProjectVersionState.values()) {
			if (state.getValue().equals(val)) {
				return state.getName();
			}
		}
		return null;
	}

}
