package com.dili.sysadmin.service.impl;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.dao.ResourceMapper;
import com.dili.sysadmin.domain.Resource;
import com.dili.sysadmin.domain.dto.ResourceDto;
import com.dili.sysadmin.service.ResourceService;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Service
public class ResourceServiceImpl extends BaseServiceImpl<Resource, Long> implements ResourceService {

	@Autowired
	private ResourceMapper resourceMapper;

	public ResourceMapper getActualDao() {
		return (ResourceMapper) getDao();
	}

	@Override
	public BaseOutput<Object> add(ResourceDto dto) {
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
}