package com.dili.sysadmin.service.impl;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.dao.ResourceMapper;
import com.dili.sysadmin.dao.RoleResourceMapper;
import com.dili.sysadmin.domain.Resource;
import com.dili.sysadmin.domain.RoleResource;
import com.dili.sysadmin.domain.dto.ResourceDto;
import com.dili.sysadmin.exception.ApplicationException;
import com.dili.sysadmin.service.ResourceService;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Service
public class ResourceServiceImpl extends BaseServiceImpl<Resource, Long> implements ResourceService {

	@Autowired
	private ResourceMapper resourceMapper;
	@Autowired
	private RoleResourceMapper roleResourceMapper;

	public ResourceMapper getActualDao() {
		return (ResourceMapper) getDao();
	}

	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public BaseOutput<Object> add(ResourceDto dto) throws ResourceException {
		Resource query = new Resource();
		query.setName(dto.getName());
		int count = this.resourceMapper.selectCount(query);
		if (count > 0) {
			return BaseOutput.failure(String.format("已存在名称为%s的资源", dto.getName()));
		}
		query = new Resource();
		query.setCode(dto.getCode());
		count = this.resourceMapper.selectCount(query);
		if (count > 0) {
			return BaseOutput.failure(String.format("已存在名称为%s的资源", dto.getCode()));
		}
		Resource resource = new Resource();
		resource.setName(dto.getName());
		resource.setCode(dto.getCode());
		resource.setMenuId(dto.getMenuId());
		count = this.resourceMapper.insertSelective(resource);
		if (count <= 0) {
			return BaseOutput.failure("新增失败");
		}
		RoleResource rr = new RoleResource();
		rr.setRoleId(1L);
		rr.setResourceId(resource.getId());
		count = this.roleResourceMapper.insertSelective(rr);
		if (count <= 0) {
			throw new ResourceException("资源绑定超级管理员失败");
		}
		return BaseOutput.success("新增成功").setData(resource);
	}

	@Override
	public BaseOutput<Object> update(ResourceDto dto) {
		Resource resource = this.resourceMapper.selectByPrimaryKey(dto.getId());
		if (resource == null) {
			return BaseOutput.failure("资源不存在");
		}
		Resource query = new Resource();
		query.setName(dto.getName());
		Resource nameResource = this.resourceMapper.selectOne(query);
		if (nameResource != null && !nameResource.getId().equals(resource.getId())) {
			return BaseOutput.failure("存在相同资源名称的资源");
		}
		query = new Resource();
		query.setCode(dto.getCode());
		Resource codeResource = this.resourceMapper.selectOne(query);
		if (codeResource != null && !codeResource.getId().equals(resource.getId())) {
			return BaseOutput.failure("存在相同资源代码的资源");
		}
		resource.setName(dto.getName());
		resource.setCode(dto.getCode());
		resource.setDescription(dto.getDescription());
		resource.setModified(new Date());
		int result = this.resourceMapper.updateByPrimaryKey(resource);
		if (result <= 0) {
			return BaseOutput.failure("修改失败");
		}
		return BaseOutput.success("修改成功");
	}

	@Override
	public List<String> findCodeByUserId(Long id) {

		return this.getActualDao().findResourceCodeByUserId(id);
	}

	@Transactional
	@Override
	public BaseOutput<Object> deleteWithOutput(Long id) {
		RoleResource record = new RoleResource();
		record.setResourceId(id);
		this.roleResourceMapper.delete(record);
		int rows = this.getActualDao().deleteByPrimaryKey(id);
		if (rows <= 0) {
			return BaseOutput.failure();
		}
		return BaseOutput.success();
	}
}