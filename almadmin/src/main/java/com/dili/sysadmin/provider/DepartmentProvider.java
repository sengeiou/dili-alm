package com.dili.sysadmin.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.dili.sysadmin.dao.DepartmentMapper;
import com.dili.sysadmin.domain.Department;

@Component
public class DepartmentProvider implements ValueProvider {

	@Autowired
	private DepartmentMapper deptMapper;

	@Override
	public List<ValuePair<?>> getLookupList(Object o, Map metaMap, FieldMeta fieldMeta) {
		List<Department> list = this.deptMapper.selectAll();
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		List<ValuePair<?>> target = new ArrayList<>(list.size());
		list.forEach(d -> {
			target.add(new ValuePairImpl<Long>(d.getName(), d.getId()));
		});
		return target;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals(""))
			return "";
		Department dept = this.deptMapper.selectByPrimaryKey(obj);
		if (dept == null) {
			return null;
		}
		return dept.getName();
	}
}
