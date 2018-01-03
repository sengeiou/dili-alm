package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.Task;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.service.TaskService;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Component
public class TaskProvider implements ValueProvider {

	@Autowired
	private TaskService  taskService;

	@Override
	public List<ValuePair<?>> getLookupList(Object o, Map map, FieldMeta fieldMeta) {
		List<Task> versions = this.taskService.list(null);
		if (CollectionUtils.isEmpty(versions)) {
			return null;
		}
		ArrayList<ValuePair<?>> buffer = new ArrayList<>(versions.size());
		versions.forEach(v -> {
			buffer.add(new ValuePairImpl<Long>(v.getName(), v.getId()));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {
		if (o == null)
			return null;
		Task task = this.taskService.get((Long) o);
		return task == null ? null : task.getName();
	}

}
