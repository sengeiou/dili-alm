package com.dili.sysadmin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.dao.DepartmentRoleMapper;
import com.dili.sysadmin.dao.UserRoleMapper;
import com.dili.sysadmin.domain.DepartmentRole;
import com.dili.sysadmin.domain.UserRole;
import com.dili.sysadmin.service.DepartmentRoleService;
import com.netflix.discovery.converters.Auto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-07 15:11:37.
 */
@Service
public class DepartmentRoleServiceImpl extends BaseServiceImpl<DepartmentRole, Long> implements DepartmentRoleService {

	@Autowired
	private UserRoleMapper userRoleMapper;

	public DepartmentRoleMapper getActualDao() {
		return (DepartmentRoleMapper) getDao();
	}

	@Override
	public BaseOutput<Object> insertAndGet(DepartmentRole departmentRole) {
		DepartmentRole record = new DepartmentRole();
		record.setDepartmentId(departmentRole.getDepartmentId());
		record.setRoleId(departmentRole.getRoleId());
		int count = this.getActualDao().selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("存在相同职位");
		}
		this.insertSelective(departmentRole);
		try {
			Map<Object, Object> model = this.parseEasyUiModel(departmentRole);
			return BaseOutput.success().setData(model);
		} catch (Exception e) {
			e.printStackTrace();
			return BaseOutput.failure();
		}
	}

	@Override
	public List<Map> listByProvider(DepartmentRole departmentRole) {
		List<DepartmentRole> roles = this.list(departmentRole);
		try {
			return this.parseEasyUiModelList(roles);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BaseOutput<Object> updateAndGet(DepartmentRole departmentRole) {
		DepartmentRole record = new DepartmentRole();
		record.setDepartmentId(departmentRole.getDepartmentId());
		record.setRoleId(departmentRole.getRoleId());
		DepartmentRole old = this.getActualDao().selectOne(record);
		if (old != null && !old.getId().equals(departmentRole.getId())) {
			return BaseOutput.failure("存在相同的职务");
		}
		this.updateSelective(departmentRole);
		try {
			Map<Object, Object> model = this.parseEasyUiModel(departmentRole);
			return BaseOutput.success().setData(model);
		} catch (Exception e) {
			e.printStackTrace();
			return BaseOutput.failure();
		}
	}

	private Map<Object, Object> parseEasyUiModel(DepartmentRole departmentRole) throws Exception {
		List<Map> list = this.parseEasyUiModelList(Arrays.asList(departmentRole));
		return list.get(0);
	}

	private List<Map> parseEasyUiModelList(List<DepartmentRole> list) throws Exception {
		Map<String, Object> metadata = new HashMap<>();

		JSONObject roleNameProvider = new JSONObject();
		roleNameProvider.put("provider", "roleNameProvider");
		metadata.put("roleId", roleNameProvider);

		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

	@Override
	public BaseOutput<Object> deleteAfterCheck(Long id) {
		DepartmentRole dr = this.getActualDao().selectByPrimaryKey(id);
		UserRole ur = new UserRole();
		ur.setRoleId(dr.getRoleId());
		int count = this.userRoleMapper.selectCount(ur);
		if (count > 0) {
			return BaseOutput.failure("该职位关联了用户，不能删除");
		}
		count = this.getActualDao().deleteByPrimaryKey(id);
		if (count <= 0) {
			return BaseOutput.failure("删除失败");
		}
		return BaseOutput.success();
	}

}