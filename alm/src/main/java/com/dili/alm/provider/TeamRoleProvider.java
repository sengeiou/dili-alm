package com.dili.alm.provider;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.service.TeamService;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
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
public class TeamRoleProvider implements ValueProvider, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private TeamService teamService;

	public void init() {
		if (AlmCache.TEAM_ROLE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = teamService.getTeamRoles();
			list.forEach(type -> {
				AlmCache.TEAM_ROLE_MAP.put(type.getValue(), type.getCode());
			});
		}
	}

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		init();
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		AlmCache.TEAM_ROLE_MAP.forEach((k, v) -> {
			buffer.add(new ValuePairImpl<String>(v, k));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals(""))
			return null;
		return AlmCache.TEAM_ROLE_MAP.get(obj.toString());
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		this.init();
	}
}