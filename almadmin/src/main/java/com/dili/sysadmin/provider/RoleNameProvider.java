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
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.service.RoleService;

@Component
public class RoleNameProvider implements ValueProvider {

	@Autowired
	private RoleService roleService;

	@Override
	public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		List<Role> roles = this.roleService.list(null);
		if (CollectionUtils.isEmpty(roles)) {
			return buffer;
		}
		roles.forEach(r -> {
			buffer.add(new ValuePairImpl<Long>(r.getRoleName(), r.getId()));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
		if (val == null || val.equals(""))
			return null;
		Role role = this.roleService.get((Long) val);
		if (role == null) {
			return null;
		}
		return role.getRoleName();
	}

}
