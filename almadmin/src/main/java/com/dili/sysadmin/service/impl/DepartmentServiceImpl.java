package com.dili.sysadmin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.dao.DepartmentMapper;
import com.dili.sysadmin.dao.UserDepartmentMapper;
import com.dili.sysadmin.dao.UserMapper;
import com.dili.sysadmin.domain.Department;
import com.dili.sysadmin.domain.UserDepartment;
import com.dili.sysadmin.domain.dto.DepartmentUserCountDto;
import com.dili.sysadmin.service.DepartmentService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-09-07 09:48:51.
 */
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<Department, Long> implements DepartmentService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserDepartmentMapper userDepartmentMapper;

	public DepartmentMapper getActualDao() {
		return (DepartmentMapper) getDao();
	}

	@Transactional
	@Override
	public BaseOutput<Object> checkBeforeDelete(Long deptId) {
		Department record = new Department();
		record.setParentId(deptId);
		int count = this.getActualDao().selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("该部门下包含子部门不能删除");
		}
		count = this.userMapper.countByDepartmentId(deptId);
		if (count > 0) {
			return BaseOutput.failure("部门下包含用户不能删除");
		}
		count = this.getActualDao().deleteByPrimaryKey(deptId);
		return count > 0 ? BaseOutput.success() : BaseOutput.failure();
	}

	@Transactional
	@SuppressWarnings("unchecked")
	@Override
	public BaseOutput<Object> insertAfterCheck(Department department) {
		Department record = new Department();
		record.setName(department.getName());
		int count = this.getActualDao().selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("存在相同名称的部门");
		}
		int result = this.getActualDao().insertSelective(department);
		if (result > 0) {
			return BaseOutput.success().setData(department);
		}
		return BaseOutput.failure("插入失败");
	}

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public BaseOutput<Object> updateAfterCheck(Department department) {
		Department record = new Department();
		record.setName(department.getName());
		Department oldDept = this.getActualDao().selectOne(record);
		if (oldDept != null && !oldDept.getId().equals(department.getId())) {
			return BaseOutput.failure("存在相同名称的部门");
		}		
		department.setModified(new Date());
		int result = this.getActualDao().updateByPrimaryKey(department);
		if (result > 0) {
			return BaseOutput.success().setData(department);
		}
		return BaseOutput.failure("更新失败");
	}

	@Override
	public List<DepartmentUserCountDto> listDepartmentUserCount(Department department) {
		List<Department> departments = this.getActualDao().select(department);
		if (CollectionUtils.isEmpty(departments)) {
			return null;
		}
		List<DepartmentUserCountDto> dtos = new ArrayList<>(departments.size());
		departments.forEach(d -> {
			DepartmentUserCountDto dto = new DepartmentUserCountDto();
			dto.setCode(d.getCode());
			dto.setCreated(d.getCreated());
			dto.setId(d.getId());
			dto.setModified(d.getModified());
			dto.setName(d.getName());
			dto.setNotes(d.getNotes());
			dto.setOperatorId(d.getOperatorId());
			dto.setParentId(d.getParentId());
			UserDepartment record = new UserDepartment();
			record.setDepartmentId(d.getId());
			Integer userCount = this.userMapper.countByDepartmentId(d.getId());
			dto.setUserCount(userCount);
			dtos.add(dto);
		});
		return dtos;
	}

	@Override
	public Department findById(Long departmentId) {
		return this.getActualDao().selectByPrimaryKey(departmentId);
	}

	@Override
	public List<Department> findByUserId(Long userId) {
		return this.getActualDao().findByUserId(userId);
	}

}