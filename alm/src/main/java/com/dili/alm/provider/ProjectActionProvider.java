package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.domain.ProjectAction;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

@Component
public class ProjectActionProvider implements ValueProvider {

	@Autowired
	private ProjectVersionProvider versionProvider;
	@Autowired
	private TaskProvider taskProvider;

	@Override
	public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> list = new ArrayList<>(ProjectAction.values().length);
		for (ProjectAction action : ProjectAction.values()) {
			list.add(new ValuePairImpl<String>(action.getName(), action.getCode()));
		}
		return list;
	}

	@Override
	public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
		if (val == null) {
			return null;
		}
		for (ProjectAction action : ProjectAction.values()) {
			if (action.getCode().equals(val)) {
				return action.getName();
			}
		}
		return null;
	}

}
