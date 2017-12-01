package com.dili.alm.provider;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.service.ProjectService;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.dili.sysadmin.sdk.session.SessionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-24 14:31:10.
 */
@Component
public class ProjectStateProvider implements ValueProvider, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ProjectService projectService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		init();
	}

	public void init() {
		if (AlmCache.projectStateMap.isEmpty()) {
			List<DataDictionaryValueDto> list = projectService.getProjectStates();
			list.forEach(type -> {
				AlmCache.projectStateMap.put(type.getValue(), type.getCode());
			});
		}
	}

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		init();
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		AlmCache.projectStateMap.forEach((k, v) -> {
			buffer.add(new ValuePairImpl<String>(v, k));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals(""))
			return null;
		return AlmCache.projectStateMap.get(obj.toString());
	}
}