package com.dili.alm.provider;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectVersionService;
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
import java.util.Objects;

/**
 * Created by asiamaster on 2017/5/31 0031.
 */
@Component
public class ProjectChangeProvider implements ValueProvider {

	@Autowired
	private ProjectChangeService changeService;

	@Autowired
	private ProjectVersionService projectVersionService;

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
		JSONObject json = (JSONObject) metaMap;
		try {
			if (Objects.equals(json.getString("field"), "changeType")) {
				if (json.get("_rowData") instanceof Approve) {
					Approve approve = json.getObject("_rowData", Approve.class);
					obj = approve.getProjectApplyId();
				}
				if (json.get("_rowData") instanceof Task) {
					Task task = json.getObject("_rowData", Task.class);
					obj = task.getChangeId();
				}
				ProjectChange change = changeService.get((Long) obj);
				return AlmCache.getInstance().getChangeType().get(change.getType());
			}

			if (Objects.equals(json.getString("field"), "changeName")) {
				if (json.get("_rowData") instanceof Approve) {
					Approve approve = json.getObject("_rowData", Approve.class);
					obj = approve.getProjectApplyId();
				}
				if (json.get("_rowData") instanceof Task) {
					Task task = json.getObject("_rowData", Task.class);
					obj = task.getChangeId();
				}
				ProjectChange change = changeService.get((Long) obj);
				return change.getName();
			}

			if (Objects.equals(json.getString("field"), "version")) {
				if (json.get("_rowData") instanceof Approve) {
					Approve approve = json.getObject("_rowData", Approve.class);
					obj = approve.getProjectApplyId();
				}
				if (json.get("_rowData") instanceof Task) {
					Task task = json.getObject("_rowData", Task.class);
					obj = task.getChangeId();
				}
				ProjectChange change = changeService.get((Long) obj);
				ProjectVersion version = projectVersionService.get(change.getVersionId());
				return version.getVersion();
			}
		} catch (Exception ignored) {
		}

		if (obj == null || obj.equals(""))
			return null;
		ProjectChange change = this.changeService.get((Long) obj);
		return change.getName();
	}
}
