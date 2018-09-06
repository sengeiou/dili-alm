package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.Task;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * Created by asiamaster on 2017/5/31 0031.
 */
@Component
public class ProjectChangeProvider implements ValueProvider {

	@Autowired
	private ProjectChangeMapper changeMapper;

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		ProjectChange query = DTOUtils.newDTO(ProjectChange.class);
		List<ProjectChange> list = this.changeMapper.select(query);
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
		if (metaMap != null && metaMap instanceof JSONObject) {
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
					ProjectChange change = this.changeMapper.selectByPrimaryKey(obj);
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
					ProjectChange change = this.changeMapper.selectByPrimaryKey(obj);
					return change.getName();
				}

				if (Objects.equals(json.getString("field"), "project")) {
					if (json.get("_rowData") instanceof Approve) {
						Approve approve = json.getObject("_rowData", Approve.class);
						obj = approve.getProjectApplyId();
					}
					if (json.get("_rowData") instanceof Task) {
						Task task = json.getObject("_rowData", Task.class);
						obj = task.getChangeId();
					}
					ProjectChange change = this.changeMapper.selectByPrimaryKey(obj);
					Project project = AlmCache.getInstance().getProjectMap().get(change.getProjectId());
					if (project == null) {
						return "";
					}
					return project.getName();
				}
			} catch (Exception ignored) {
			}
		}
		if (obj == null || obj.equals(""))
			return null;
		ProjectChange change = this.changeMapper.selectByPrimaryKey(obj);
		return change.getName();
	}
}
