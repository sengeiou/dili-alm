package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Component
public class ProjectPhaseProvider implements ValueProvider {

	@Autowired
	private ProjectPhaseService projectPhaseService;
	@Autowired
	private PhaseNameProvider nameProvider;

	@Override
	public List<ValuePair<?>> getLookupList(Object o, Map map, FieldMeta fieldMeta) {
		List<ProjectPhase> versions = this.projectPhaseService.list(null);
		if (CollectionUtils.isEmpty(versions)) {
			return null;
		}
		ArrayList<ValuePair<?>> buffer = new ArrayList<>(versions.size());
		versions.forEach(v -> {
			String text = this.nameProvider.getDisplayText(v.getName(), null, null);
			buffer.add(new ValuePairImpl<Long>(text, v.getId()));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {
		if (o == null)
			return null;
		ProjectPhase phase = this.projectPhaseService.get((Long) o);
		return this.nameProvider.getDisplayText(phase.getName(), null, null);
	}

}
