package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.DataDictionary;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.service.DataDictionaryService;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

@Component
public class ResourceEnvironmentProvider implements ValueProvider {

	private static final String RESOURCE_ENVIRONMENT_CODE = "resource_environment";

	@Autowired
	private DataDictionaryService ddService;

	public void init() {
		if (AlmCache.RESOURCE_ENVIRONMENT_MAP.isEmpty()) {
			DataDictionary record = DTOUtils.newDTO(DataDictionary.class);
			record.setCode(RESOURCE_ENVIRONMENT_CODE);
			DataDictionaryDto dto = this.ddService.findByCode(RESOURCE_ENVIRONMENT_CODE);
			if (dto == null) {
				return;
			}
			if (CollectionUtils.isEmpty(dto.getValues())) {
				return;
			}
			dto.getValues()
					.forEach(v -> AlmCache.RESOURCE_ENVIRONMENT_MAP.put(Integer.valueOf(v.getValue()), v.getCode()));
		}
	}

	@Override
	public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
		init();
		if (AlmCache.RESOURCE_ENVIRONMENT_MAP.isEmpty()) {
			return null;
		}
		List<ValuePair<?>> list = new ArrayList<>();
		AlmCache.RESOURCE_ENVIRONMENT_MAP.forEach((k, v) -> list.add(new ValuePairImpl<Integer>(v, k)));
		return list;
	}

	@Override
	public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
		if (val == null) {
			return null;
		}
		return AlmCache.RESOURCE_ENVIRONMENT_MAP.get(val);
	}

}
