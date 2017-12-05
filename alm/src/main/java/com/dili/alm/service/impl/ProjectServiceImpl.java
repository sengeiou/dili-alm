package com.dili.alm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.AlmConstants.MemberState;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.TeamType;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.rpc.DataAuthRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TaskService;
import com.dili.alm.service.TeamService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-18 17:22:54.
 */
@Service
public class ProjectServiceImpl extends BaseServiceImpl<Project, Long> implements ProjectService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);
	private static final String PROJECT_TYPE_CODE = "project_type";
	private static final String PROJECT_STATE_CODE = "project_state";

	@Autowired
	DataDictionaryService dataDictionaryService;
	@Autowired
	private ProjectVersionMapper projectVersionMapper;
	@Autowired
	private TeamMapper teamMapper;
	@Autowired
	DataAuthRpc dataAuthRpc;
	@Autowired
	private TeamService teamService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private TaskMapper taskMapper;

	public ProjectMapper getActualDao() {
		return (ProjectMapper) getDao();
	}

	@Override
	public int update(Project condtion) {
		// 同步更新缓存
		if (StringUtils.isNotBlank(condtion.getName())) {
			AlmCache.projectMap.get(condtion.getId()).setName(condtion.getName());
		}
		dataAuthRpc.updateDataAuth(condtion.getId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT, condtion.getName());
		return super.update(condtion);
	}

	@Override
	public int insert(Project project) {
		int i = super.insert(project);
		// 同步更新缓存
		AlmCache.projectMap.put(project.getId(), project);
		// 向权限系统中添加项目数据权限
		String parentId = project.getParentId() == null ? null : project.getParentId().toString();
		dataAuthRpc.addDataAuth(project.getId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT, project.getName(), parentId);
		return i;
	}

	@Override
	public int insertSelective(Project project) {
		int i = super.insertSelective(project);
		// 同步更新缓存
		AlmCache.projectMap.put(project.getId(), project);
		// 向权限系统中添加项目数据权限
		String parentId = project.getParentId() == null ? null : project.getParentId().toString();
		dataAuthRpc.addDataAuth(project.getId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT, project.getName(), parentId);
		return i;
	}

	@Override
	public int delete(Long id) {
		AlmCache.projectMap.remove(id);
		dataAuthRpc.deleteDataAuth(id.toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT);
		return super.delete(id);
	}

	@Override
	public int delete(List<Long> ids) {
		ids.forEach(id -> {
			AlmCache.projectMap.remove(id);
			dataAuthRpc.deleteDataAuth(id.toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT);
		});
		return super.delete(ids);
	}

	@Override
	public List<DataDictionaryValueDto> getPojectTypes() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

	@Transactional
	@Override
	public BaseOutput<Object> deleteBeforeCheck(Long projectId) {
		ProjectVersion record = DTOUtils.newDTO(ProjectVersion.class);
		record.setProjectId(projectId);
		int count = this.projectVersionMapper.selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("项目关联了里程碑，不能删除");
		}
		Team teamQuery = DTOUtils.newDTO(Team.class);
		teamQuery.setProjectId(projectId);
		List<Team> teams = this.teamMapper.select(teamQuery);
		if (CollectionUtils.isNotEmpty(teams)) {
			teams.forEach(t -> {
				this.teamService.delete(t.getId());
			});
		}

		Project projectQuery = DTOUtils.newDTO(Project.class);
		projectQuery.setParentId(projectId);
		count = this.getActualDao().selectCount(projectQuery);
		if (count > 0) {
			return BaseOutput.failure("项目包含子项目，不能删除");
		}
		count = this.delete(projectId);
		if (count > 0) {
			return BaseOutput.success("删除成功");
		}
		return BaseOutput.failure("删除失败");
	}

	@Override
	public List<Project> getChildProjects(Long id) {
		return getActualDao().getChildProjects(id);
	}

	@Override
	public EasyuiPageOutput listEasyuiPageByExample(Project domain, boolean useProvider) throws Exception {
		SessionContext sessionContext = SessionContext.getSessionContext();
		if (sessionContext == null) {
			throw new RuntimeException("未登录");
		}
		List<Map> dataauth = sessionContext.dataAuth(AlmConstants.DATA_AUTH_TYPE_PROJECT);
		List<Long> projectIds = new ArrayList<>(dataauth.size());
		dataauth.forEach(t -> {
			List<Project> projects = getChildProjects(Long.parseLong(t.get("dataId").toString()));
			projects.forEach(p -> {
				projectIds.add(p.getId());
			});
			// projectIds.add(Long.parseLong(t.get("dataId").toString()));
		});
		// 去掉数据权限
		// if (projectIds.isEmpty()) {
		// return new EasyuiPageOutput(0, null);
		// }
		// // 刷新projectProvider缓存
		// List<Project> list = list(DTOUtils.newDTO(Project.class));
		// list.forEach(project -> {
		// AlmCache.projectMap.put(project.getId(), project);
		// });
		// projectDto.setIds(projectIds);
		return super.listEasyuiPageByExample(domain, useProvider);
	}

	@Transactional(rollbackFor = ProjectException.class)
	@Override
	public BaseOutput<Object> insertAfterCheck(Project project) throws ProjectException {
		Project record = DTOUtils.newDTO(Project.class);
		record.setName(project.getName());
		int count = this.getActualDao().selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("已存在项目相同的项目");
		}
		int result = this.insertSelective(project);
		Team t = new DTOUtils().newDTO(Team.class);
		t.setProjectId(project.getId());
		t.setMemberId(project.getProjectManager());
		count = this.teamMapper.selectCount(t);
		if (project.getProjectManager() != null && count <= 0) {
			Team tt = DTOUtils.newDTO(Team.class);
			Date now = new Date();
			tt.setJoinTime(now);
			tt.setMemberId(project.getProjectManager());
			tt.setMemberState(MemberState.JOIN.getCode());
			tt.setProjectId(project.getId());
			tt.setType(String.valueOf(TeamType.DEVELOP.getCode()));
			BaseOutput<Object> output = this.teamService.insertAfterCheck(tt);
			if (!output.isSuccess()) {
				throw new ProjectException(output.getResult());
			}
		}
		t.setMemberId(project.getProductManager());
		count = this.teamMapper.selectCount(t);
		if (project.getProductManager() != null && count <= 0) {
			Team tt = DTOUtils.newDTO(Team.class);
			Date now = new Date();
			tt.setJoinTime(now);
			tt.setMemberId(project.getProductManager());
			tt.setMemberState(MemberState.JOIN.getCode());
			tt.setProjectId(project.getId());
			tt.setType(String.valueOf(TeamType.PRODUCT.getCode()));
			BaseOutput<Object> output = this.teamService.insertAfterCheck(tt);
			if (!output.isSuccess()) {
				throw new ProjectException(output.getResult());
			}
		}
		t.setMemberId(project.getTestManager());
		count = this.teamMapper.selectCount(t);
		if (project.getTestManager() != null && count <= 0) {
			Team tt = DTOUtils.newDTO(Team.class);
			Date now = new Date();
			tt.setJoinTime(now);
			tt.setMemberId(project.getTestManager());
			tt.setMemberState(MemberState.JOIN.getCode());
			tt.setProjectId(project.getId());
			tt.setType(String.valueOf(TeamType.TEST.getCode()));
			BaseOutput<Object> output = this.teamService.insertAfterCheck(tt);
			if (!output.isSuccess()) {
				throw new ProjectException(output.getResult());
			}
		}
		if (result > 0) {
			return BaseOutput.success().setData(project);
		}
		return BaseOutput.failure();
	}

	@Transactional
	@Override
	public BaseOutput<Object> updateAfterCheck(Project project) {
		int result;
		Project record = DTOUtils.newDTO(Project.class);
		record.setName(project.getName());
		Project oldProject = this.getActualDao().selectOne(record);
		if (oldProject != null && !oldProject.getId().equals(project.getId())) {
			return BaseOutput.failure("存在相同名称的项目");
		}
		project.setModified(new Date());

		result = this.updateSelective(project);
		// 刷新projectProvider缓存
		AlmCache.projectMap.put(project.getId(), project);

		dataAuthRpc.updateDataAuth(project.getId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT, project.getName());
		if (result > 0) {
			return BaseOutput.success().setData(project);
		}
		return BaseOutput.failure();
	}

	@Override
	public List<DataDictionaryValueDto> getProjectStates() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_STATE_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}
	

	public EasyuiPageOutput listPageMyProject() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if(userTicket==null){
			throw new RuntimeException("未登录");
		}
		int total = this.getActualDao().getPageByProjectCount(userTicket.getId());
		List<Project>   projectList=this.getActualDao().getProjectsByTaskOwner(userTicket.getId());
		List<ProjectDto>   projectDtoList =new ArrayList<ProjectDto>();
		if(projectList!=null&&projectList.size()>0){
			for (Project project : projectList) {
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				ProjectDto projectDto =new ProjectDto();
				projectDto.setId(project.getId());
				projectDto.setSerialNumber(project.getSerialNumber());
				projectDto.setName(project.getName());
				if(project.getType().equalsIgnoreCase("I")){
					projectDto.setType("内部项目");
				}else if(project.getType().equalsIgnoreCase("K")){
					projectDto.setType("重点项目");
				}else if(project.getType().equalsIgnoreCase("R")){
					projectDto.setType("预约项目");
				}else if(project.getType().equalsIgnoreCase("G")){
					projectDto.setType("一般项目");
				}
				projectDto.setStartToEndDate(sdf.format(project.getCreated())+"至"
				+sdf.format(project.getEndDate()));
				projectDto.setActualStartDate(sdf.format(project.getActualStartDate()));
				if(project.getProjectState()==1){
					projectDto.setProjectState("进行中");
				}else if(project.getProjectState()==2){
					projectDto.setProjectState("已完成");
				}else if(project.getProjectState()==3){
					projectDto.setProjectState("暂停中");
				}else if(project.getProjectState()==4){
					projectDto.setProjectState("已关闭");
				}
				projectDto.setTaskCount(project.getTaskCount());
				projectDto.setMemberCount(project.getMemberCount());
				projectDto.setCompletedProgress(project.getCompletedProgress());
				projectDto.setOriginator(project.getOriginator());
				projectDtoList.add(projectDto);
			}
		}
		EasyuiPageOutput   out=new EasyuiPageOutput(total,projectDtoList);
		return out;
	}

}