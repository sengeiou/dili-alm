package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class ProjectTypeProvider implements ValueProvider {

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		AlmCache.getInstance().getProjectTypeMap().forEach((k, v) -> {
			buffer.add(new ValuePairImpl<String>(v, k));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals(""))
			return null;
		return AlmCache.getInstance().getProjectTypeMap().get(obj.toString());
	}
}