package com.dili.alm.provider;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Project;
import com.dili.alm.service.ProjectService;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Component
public class ProjectProvider implements ValueProvider, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	ProjectService projectService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		init();
	}

	public void init() {
		if (AlmCache.PROJECT_MAP.isEmpty()) {
			List<Project> list = projectService.list(DTOUtils.newDTO(Project.class));
			list.forEach(project -> {
				AlmCache.PROJECT_MAP.put(project.getId(), project);
			});
		}
	}

	@Override
	public List<ValuePair<?>> getLookupList(Object o, Map map, FieldMeta fieldMeta) {
		init();
		ArrayList buffer = new ArrayList<ValuePair<?>>();
		List<Map> dataauth = SessionContext.getSessionContext().dataAuth(AlmConstants.DATA_AUTH_TYPE_PROJECT);
		dataauth.forEach(da -> {
			Long key = Long.parseLong(da.get("dataId").toString());
			if (AlmCache.PROJECT_MAP.containsKey(key)) {
				buffer.add(new ValuePairImpl(AlmCache.PROJECT_MAP.get(key).getName(), key));
			}
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {
		if (o == null)
			return null;
		init();
		Project project = AlmCache.PROJECT_MAP.get(o);
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
		metadata.put("actualStartDate", almDateProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("projectManager", memberProvider);
		metadata.put("testManager", memberProvider);
		metadata.put("productManager", memberProvider);
		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

}
