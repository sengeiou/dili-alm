package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Component
public class ProjectVersionProvider implements ValueProvider {

	@Autowired
	ProjectVersionService projectVersionService;

	@Override
	public List<ValuePair<?>> getLookupList(Object o, Map map, FieldMeta fieldMeta) {
		List<ProjectVersion> versions = this.projectVersionService.list(null);
		if (CollectionUtils.isEmpty(versions)) {
			return null;
		}
		ArrayList<ValuePair<?>> buffer = new ArrayList<>(versions.size());
		versions.forEach(v -> {
			buffer.add(new ValuePairImpl<Long>(v.getCode(), v.getId()));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {
		if (o == null)
			return null;
		ProjectVersion version = this.projectVersionService.get((Long) o);
		return version == null ? null : version.getVersion();
	}

}
