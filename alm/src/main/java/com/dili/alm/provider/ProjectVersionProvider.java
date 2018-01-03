package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.dili.ss.metadata.ValueProviderUtils;

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

	public static Map<Object, Object> parseEasyUiModel(ProjectVersion projectVersion) throws Exception {
		List<Map> listMap = parseEasyUiModelList(Arrays.asList(projectVersion));
		return listMap.get(0);
	}

	public static List<Map> parseEasyUiModelList(List<ProjectVersion> list) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();
		JSONObject versionStateProvider = new JSONObject();
		versionStateProvider.put("provider", "projectStateProvider");
		metadata.put("versionState", versionStateProvider);

		JSONObject almDateProvider = new JSONObject();
		almDateProvider.put("provider", "almDateProvider");
		metadata.put("plannedStartDate", almDateProvider);
		metadata.put("plannedEndDate", almDateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("actualStartDate", datetimeProvider);

		JSONObject onlineProvider = new JSONObject();
		onlineProvider.put("provider", "onlineProvider");
		metadata.put("online", onlineProvider);
		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

}
