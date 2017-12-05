package com.dili.alm.provider;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.service.ProjectPhaseService;
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
 * Created by asiamaster on 2017/5/31 0031.
 */
@Component
public class PhaseNameProvider implements ValueProvider, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ProjectPhaseService phaseService;

	public void init() {
		if (AlmCache.PHASE_NAME_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = phaseService.getPhaseNames();
			list.forEach(type -> {
				AlmCache.PHASE_NAME_MAP.put(type.getValue(), type.getCode());
			});
		}
	}

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		init();
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		AlmCache.PHASE_NAME_MAP.forEach((k, v) -> {
			buffer.add(new ValuePairImpl<String>(v, k));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals(""))
			return null;
		return AlmCache.PHASE_NAME_MAP.get(obj.toString());
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		this.init();
	}
}
