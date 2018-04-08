package com.dili.alm.service.impl;

import com.dili.alm.dao.HardwareResourceApplyMapper;
import com.dili.alm.dao.HardwareResourceMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ResourceEnvironmentMapper;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.HardwareResource;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ResourceEnvironment;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.alm.service.HardwareResourceService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.github.pagehelper.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-20 17:22:08.
 */
@Service
public class HardwareResourceServiceImpl extends BaseServiceImpl<HardwareResource, Long> implements HardwareResourceService{
	private static final String DEPARTMENTNAME = "运维部";
	@Autowired
	UserRpc userRpc;
	@Autowired
	DepartmentRpc departmentRpc;
	@Autowired
	private ProjectMapper projectMapper;
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
		BaseOutput<Department> findByDepartmentName = departmentRpc.findByDepartmentName(DEPARTMENTNAME);
		Department department =findByDepartmentName.getData();
		User user = new User();
		user.setDepartmentId(department.getId());
		return userRpc.list(user).getData();
	}
	
	@Override
	public List<Project> projectList() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		List<Project> listProject = new ArrayList<Project>();
		/* if (isNoTeam()||isCommittee()) { */
		listProject = projectMapper.selectAll();
		/*
		 * }else{ listProject = taskMapper.selectProjectByTeam(userTicket.getId()); }
		 */
		return listProject;
	}

	@Override
	public BaseOutput insertHardwareResourceSelective(
			List<HardwareResource> hardwareResourceList) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		List<HardwareResource> list=new ArrayList<HardwareResource>();
		for (HardwareResource hardwareResource : hardwareResourceList) {
			hardwareResource.setMaintenanceOwner(userTicket.getId());
			hardwareResource.setLastModifyDate(new Date());
			list.add(hardwareResource);
		}
		
		int insertList = this.hardwareResourceMapper.insertList(list);
		if(list.size()==insertList){
			return  BaseOutput.success("新增成功");
		}
		return  BaseOutput.failure("新增失败");
	}

	@Override
	public boolean isSubmit(Long id) {
		HardwareResource selectByPrimaryKey = this.hardwareResourceMapper.selectByPrimaryKey(id);
		if(selectByPrimaryKey.getIsSubmit()==1){
			return true;
		}
		return false;
	}
	
	@Override
	public EasyuiPageOutput listEasyuiPageByExample(HardwareResource domain,List<Long> projectIds, boolean useProvider) throws Exception {
		
		String sort = domain.getSort();
		if(WebUtil.strIsEmpty(sort)){
			domain.setSort("id");
			domain.setOrder("ASC");
			
		}else if(sort.equals("projectId")){
			domain.setSort("project_id");
		}else if(sort.equals("maintenanceDate")){
			domain.setSort("maintenance_date");
		}
		List<HardwareResource> list = this.hardwareResourceMapper.selectByIds(domain,projectIds);
		int total = this.hardwareResourceMapper.selectByIdsCounts(domain,projectIds);
		List results = null;
		results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, list) : list;
		return new EasyuiPageOutput(Integer.parseInt(String.valueOf(total)), results);
	}

	@Override
	public boolean isOperation(Long id) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			throw new RuntimeException("未登录");
		}
		HardwareResource selectByPrimaryKey = this.hardwareResourceMapper.selectByPrimaryKey(id);
		if(selectByPrimaryKey.getMaintenanceOwner()==user.getId()){
			return true;
			
		}
		return false;
	}

	@Override
	public BaseOutput insertOneSelective(HardwareResource hardwareResource) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		hardwareResource.setMaintenanceOwner(userTicket.getId());
		hardwareResource.setLastModifyDate(new Date());
		int insertList = this.hardwareResourceMapper.insert(hardwareResource);
		if(insertList==1){
			return  BaseOutput.success("新增成功");
		}
		return  BaseOutput.failure("新增失败");
	}

	@Override
	public void submit(Long projectId, Long userId) {
		HardwareResource hardwareResource=DTOUtils.newDTO(HardwareResource.class);
  		hardwareResource.setProjectId(projectId);
  		hardwareResource.setLastModifyDate(new Date());
  		hardwareResource.setMaintenanceOwner(userId);
  		hardwareResourceMapper.updateByProjectId(hardwareResource);
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


}