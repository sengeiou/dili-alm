package com.dili.alm.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.HardwareResourceMapper;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.uap.sdk.domain.Department;
import com.dili.alm.domain.HardwareResource;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.exceptions.HardwareResourceException;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.HardwareResourceService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-20 17:22:08.
 */
@Service
public class HardwareResourceServiceImpl extends BaseServiceImpl<HardwareResource, Long>
		implements HardwareResourceService {
	private static final String DEPARTMENTNAME = "运维部";
	@Autowired
	UserRpc userRpc;
	@Autowired
	DepartmentRpc departmentRpc;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectApplyMapper projectApplyMapper;
	@Autowired
	private HardwareResourceMapper hardwareResourceMapper;
	@Autowired
	private DataDictionaryService dataDictionaryService;

	private static final String REGIONAL_CODE = "regional";
	private static final String ENVIRONMENT_CODE = "environment";

	@Override
	public List<User> listUserByDepartment() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		Department newDepartment = DTOUtils.newDTO(Department.class);
		newDepartment.setName(DEPARTMENTNAME);
		newDepartment.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<Department> findByDepartmentName = departmentRpc.getOne(newDepartment);
		Department department = findByDepartmentName.getData();
		User user = DTOUtils.newDTO(User.class);
		user.setDepartmentId(department.getId());
		user.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		return userRpc.listByExample(user).getData();
	}

	@Override
	public List<Project> projectList() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		List<Project> listProject = new ArrayList<Project>();
		listProject = projectMapper.selectAll();
		return listProject;
	}

	@Override
	public BaseOutput insertHardwareResourceSelective(List<HardwareResource> hardwareResourceList) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		List<HardwareResource> list = new ArrayList<HardwareResource>();
		for (HardwareResource hardwareResource : hardwareResourceList) {
			hardwareResource.setMaintenanceOwner(userTicket.getId());
			hardwareResource.setLastModifyDate(new Date());
			list.add(hardwareResource);
		}

		int insertList = this.hardwareResourceMapper.insertList(list);
		if (list.size() == insertList) {
			return BaseOutput.success("新增成功");
		}
		return BaseOutput.failure("新增失败");
	}

	@Override
	public void insertHardwareResource(HardwareResource hardwareResource) throws HardwareResourceException {
		Project project = this.projectMapper.selectByPrimaryKey(hardwareResource.getProjectId());
		if (project == null) {
			throw new HardwareResourceException("项目不存在");
		}
		hardwareResource.setProjectSerialNumber(project.getSerialNumber());
		hardwareResource.setLastModifyDate(new Date());
		int rows = this.hardwareResourceMapper.insertSelective(hardwareResource);
		if (rows <= 0) {
			throw new HardwareResourceException("修改IT资源失败");
		}
	}

	@Override
	public void submit(Long projectId, Long userId) {
		HardwareResource hardwareResource = DTOUtils.newDTO(HardwareResource.class);
		hardwareResource.setProjectId(projectId);
		hardwareResource.setMaintenanceOwner(userId);
		HardwareResource po = DTOUtils.newDTO(HardwareResource.class);
		po.setProjectId(projectId);
		int count = this.hardwareResourceMapper.selectCount(po);
		if (count <= 0) {
			// 产品要求提交it资源为空要插入一条空数据并且可以编辑
			Project project = this.projectMapper.selectByPrimaryKey(projectId);
			po.setProjectSerialNumber(project.getSerialNumber());
			po.setMaintenanceOwner(userId);
			this.hardwareResourceMapper.insertSelective(po);
		} else {
			hardwareResourceMapper.updateByProjectId(hardwareResource);
		}
	}

	@Override
	public List<DataDictionaryValueDto> getEnvironments() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(ENVIRONMENT_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

	@Override
	public List<DataDictionaryValueDto> getRegionals() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(REGIONAL_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

	@Override
	public Map<String, String> projectNumById(String id) {
		Map<String, String> map = new HashMap<String, String>();
		Project project = projectMapper.selectByPrimaryKey(Long.parseLong(id));
		map.put("projectNum", project.getSerialNumber());
		BigDecimal total = new BigDecimal("0");
		HardwareResource query = DTOUtils.newDTO(HardwareResource.class);
		query.setProjectId(Long.valueOf(id));
		List<HardwareResource> list = this.hardwareResourceMapper.select(query);
		if (CollectionUtils.isNotEmpty(list)) {
			for (HardwareResource h : list) {
				total = total.add(h.getCost());
			}
		}
		map.put("projectTotal", total.toString());
		return map;
	}

	@Override
	public void updateHardwareResource(HardwareResource hardwareResource) throws HardwareResourceException {
		int rows = this.hardwareResourceMapper.updateByPrimaryKeySelective(hardwareResource);
		if (rows <= 0) {
			throw new HardwareResourceException("修改IT资源失败");
		}
	}

}