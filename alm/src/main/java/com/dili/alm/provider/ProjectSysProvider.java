package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.SystemDto;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * 所属系统
 */
@Component
public class ProjectSysProvider implements ValueProvider {

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		AlmCache.getInstance().getProjectSysMap().forEach((k, v) -> {
			buffer.add(new ValuePairImpl(v.getName(), k));
		});
		return buffer;
	}



	@Override
	public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {
		if (o == null)
			return null;
		SystemDto entity = AlmCache.getInstance().getProjectSysMap().get(Long.valueOf((o.toString())));
		return entity == null ? null : entity.getName();
	}
}