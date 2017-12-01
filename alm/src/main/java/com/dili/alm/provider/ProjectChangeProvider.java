package com.dili.alm.provider;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by asiamaster on 2017/5/31 0031.
 */
@Component
public class ProjectChangeProvider implements ValueProvider {

	@Autowired
	private ProjectChangeService changeService;

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		ProjectChange query = DTOUtils.newDTO(ProjectChange.class);
		List<ProjectChange> list = this.changeService.list(query);
		if (CollectionUtils.isNotEmpty(list)) {
			list.forEach(p -> {
				ValuePair<?> vp = new ValuePairImpl<>(p.getName(), p.getId());
				buffer.add(vp);
			});
		}
		return buffer;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals(""))
			return null;
		ProjectChange change = this.changeService.get((Long) obj);
		return change.getName();
	}
}
