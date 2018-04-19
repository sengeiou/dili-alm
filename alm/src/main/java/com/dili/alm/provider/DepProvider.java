package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Department;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

@Component
public class DepProvider implements ValueProvider {

	@Override
	public List<ValuePair<?>> getLookupList(Object o, Map map, FieldMeta fieldMeta) {
		ArrayList buffer = new ArrayList<ValuePair<?>>();
		AlmCache.getInstance().getDepMap().forEach((k, v) -> {
			buffer.add(new ValuePairImpl(v.getName(), k));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {
		if (o == null)
			return null;
		Department dep = AlmCache.getInstance().getDepMap().get(o);
		return dep == null ? null : dep.getName();
	}
}
