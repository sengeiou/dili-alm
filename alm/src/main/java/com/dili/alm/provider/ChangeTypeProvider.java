package com.dili.alm.provider;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.service.DataDictionaryService;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-24 14:31:10.
 */
@Component
public class ChangeTypeProvider implements ValueProvider, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private DataDictionaryService dataDictionaryService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		init();
	}

	public void init() {
		if (AlmCache.CHANGE_TYPE.isEmpty()) {
			DataDictionaryDto code = dataDictionaryService.findByCode("change_type");
			List<DataDictionaryValueDto> list = code != null ? code.getValues() : null;
			list.forEach(type -> AlmCache.CHANGE_TYPE.put(type.getValue(), type.getCode()));
		}
	}

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		init();
		List<ValuePair<?>> buffer = new ArrayList<>();
		AlmCache.CHANGE_TYPE.forEach((k, v) -> buffer.add(new ValuePairImpl<>(v, k)));
		return buffer;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || "".equals(obj))
			return null;
		return AlmCache.CHANGE_TYPE.get(obj.toString());
	}
}