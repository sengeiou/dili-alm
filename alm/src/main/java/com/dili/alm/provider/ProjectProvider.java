package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.service.ProjectService;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.dili.ss.metadata.ValueProviderUtils;
import com.google.common.collect.Lists;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Component
public class ProjectProvider implements ValueProvider {

	@Autowired
	ProjectService projectService;

	@Override
	public List<ValuePair<?>> getLookupList(Object o, Map map, FieldMeta fieldMeta) {
		Object queryParams = map.get(QUERY_PARAMS_KEY);
        
        Boolean isAll = JSONObject.parseObject(queryParams.toString()).getBoolean("isAll");
		Map<Long, Project> projectMap = AlmCache.getInstance().getProjectMap();
		ArrayList<ValuePair<?>> buffer = new ArrayList<>(projectMap.size());
		// projectMap.forEach((k, v) -> buffer.add(new ValuePairImpl<Long>(v.getName(),
		// k)));
		if(isAll != null && isAll) {
			projectMap.values().stream()
			.collect(Collectors.toList()).forEach(p -> buffer.add(new ValuePairImpl<Long>(p.getName(), p.getId())));
		}else {
			projectMap.values().stream().filter(p -> !p.getProjectState().equals(ProjectState.CLOSED.getValue()))
			.collect(Collectors.toList()).forEach(p -> buffer.add(new ValuePairImpl<Long>(p.getName(), p.getId())));
		}
		
		return buffer;
	}

	@Override
	public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {
		if (o == null)
			return null;
		Project project = AlmCache.getInstance().getProjectMap().get(o);
		return project == null ? null : project.getName();
	}

	public static Map<Object, Object> parseEasyUiModel(Project project) throws Exception {
		List<Map> list = parseEasyUiModelList(Arrays.asList(project));
		return list.get(0);
	}

	public static List<Map> parseEasyUiModelList(List<Project> list) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject projectTypeProvider = new JSONObject();
		projectTypeProvider.put("provider", "projectTypeProvider");
		metadata.put("type", projectTypeProvider);

		JSONObject almDateProvider = new JSONObject();
		almDateProvider.put("provider", "almDateProvider");
		metadata.put("startDate", almDateProvider);
		metadata.put("endDate", almDateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("actualStartDate", datetimeProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("projectManager", memberProvider);
		metadata.put("testManager", memberProvider);
		metadata.put("productManager", memberProvider);
		metadata.put("developManager", memberProvider);
		metadata.put("businessOwner", memberProvider);
		metadata.put("originator", memberProvider);
		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

}
