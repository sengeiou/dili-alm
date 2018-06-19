package com.dili.alm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.FileType;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.ProjectVersionFormDto;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.dto.ProjectVersionChangeStateViewDto;
import com.dili.alm.provider.ProjectVersionProvider;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.quartz.service.ScheduleJobService;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import tk.mybatis.mapper.entity.Example;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-20 11:02:17.
 */
@Service
public class ProjectVersionServiceImpl extends BaseServiceImpl<ProjectVersion, Long> implements ProjectVersionService {

	@Autowired
	FilesService filesService;

	@Autowired
	ScheduleJobService scheduleJobService;
	@Autowired
	private ProjectChangeService projectChangeService;
	@Autowired
	private FilesMapper filesMapper;
	@Autowired
	private ProjectPhaseMapper phaseMapper;
	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private ProjectChangeMapper projectChangeMapper;
	@Autowired
	private ProjectMapper projectMapper;

	public ProjectVersionMapper getActualDao() {
		return (ProjectVersionMapper) getDao();
	}

	@Transactional
	@Override
	public BaseOutput<Object> insertSelectiveWithOutput(ProjectVersionFormDto dto) {
		// 检查项目状态是否为进行中
		Project project = this.projectMapper.selectByPrimaryKey(dto.getProjectId());
		if (!project.getProjectState().equals(ProjectState.NOT_START.getValue())
				&& !project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue())) {
			return BaseOutput.failure("项目不在进行中，不能编辑");
		}
		ProjectVersion query = DTOUtils.newDTO(ProjectVersion.class);
		query.setProjectId(dto.getProjectId());
		query.setVersion(dto.getVersion());
		int count = this.getActualDao().selectCount(query);
		if (count > 0) {
			return BaseOutput.failure("版本已存在");
		}
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		dto.setCreatorId(userTicket.getId());
		dto.setCreated(new Date());
		super.insertSelective(dto);
		if (CollectionUtils.isNotEmpty(dto.getFileIds())) {
			dto.getFileIds().forEach(id -> {
				Files file = this.filesService.get(id);
				file.setProjectId(dto.getProjectId());
				file.setVersionId(dto.getId());
				file.setType(FileType.PROJECT_INTRODUCE.getValue());
				this.filesService.updateSelective(file);
			});
		}

		try {
			Map<Object, Object> target = ProjectVersionProvider.parseEasyUiModel(dto);
			return BaseOutput.success("新增成功").setData(target);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@Transactional
	@Override
	public BaseOutput<Object> updateSelectiveWithOutput(ProjectVersionFormDto dto) {
		// 检查项目状态是否为进行中
		Project project = this.projectMapper.selectByPrimaryKey(dto.getProjectId());
		if (!project.getProjectState().equals(ProjectState.NOT_START.getValue())
				&& !project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue())) {
			return BaseOutput.failure("项目不在进行中，不能编辑");
		}
		ProjectVersion query = DTOUtils.newDTO(ProjectVersion.class);
		query.setProjectId(dto.getProjectId());
		query.setVersion(dto.getVersion());
		ProjectVersion version = this.getActualDao().selectOne(query);
		if (version != null && !version.getId().equals(dto.getId())) {
			return BaseOutput.failure("版本已存在");
		}
		version = this.getActualDao().selectByPrimaryKey(dto.getId());
		version.setVersion(dto.getVersion());
		version.setPlannedStartDate(dto.getPlannedStartDate());
		version.setPlannedEndDate(dto.getPlannedEndDate());
		version.setGit(dto.getGit());
		version.setVisitUrl(dto.getVisitUrl());
		version.setPlannedOnlineDate(dto.getPlannedOnlineDate());
		version.setHost(dto.getHost());
		version.setPort(dto.getPort());
		version.setRedmineUrl(dto.getRedmineUrl());
		version.setNotes(dto.getNotes());
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		version.setModifierId(userTicket.getId());
		version.setModified(new Date());
		super.update(version);
		if (CollectionUtils.isNotEmpty(dto.getFileIds())) {
			dto.getFileIds().forEach(id -> {
				Files file = this.filesService.get(id);
				file.setProjectId(dto.getProjectId());
				file.setVersionId(dto.getId());
				file.setType(FileType.PROJECT_INTRODUCE.getValue());
				this.filesService.updateSelective(file);
			});
		}
		Map<Object, Object> target;
		try {
			target = ProjectVersionProvider.parseEasyUiModel(version);
			return BaseOutput.success("修改成功").setData(target);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@Override
	public BaseOutput deleteWithOutput(Long id) {
		// 检查项目状态是否为进行中
		ProjectVersion version = this.getActualDao().selectByPrimaryKey(id);
		Project project = this.projectMapper.selectByPrimaryKey(version.getProjectId());
		if (!project.getProjectState().equals(ProjectState.NOT_START.getValue())
				&& !project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue())) {
			return BaseOutput.failure("项目不在进行中，不能删除");
		}
		ProjectPhase phaseQuery = DTOUtils.newDTO(ProjectPhase.class);
		phaseQuery.setVersionId(id);
		int count = this.phaseMapper.selectCount(phaseQuery);
		if (count > 0) {
			return BaseOutput.failure("该版本包含阶段信息不能删除");
		}
		Task taskQuery = DTOUtils.newDTO(Task.class);
		taskQuery.setVersionId(id);
		count = this.taskMapper.selectCount(taskQuery);
		if (count > 0) {
			return BaseOutput.failure("该版本下包含任务不能删除");
		}
		ProjectChange changeQuery = DTOUtils.newDTO(ProjectChange.class);
		changeQuery.setVersionId(id);
		count = this.projectChangeMapper.selectCount(changeQuery);
		if (count > 0) {
			return BaseOutput.failure("该版本关联了需求变更不能删除");
		}
		Files files = DTOUtils.newDTO(Files.class);
		files.setVersionId(id);
		List<Files> filesList = filesService.list(files);
		filesList.forEach(f -> {
			this.filesMapper.delete(f);
		});
		ProjectVersion projectVersion = super.get(id);
		if (projectVersion != null) {
			super.delete(id);
		}
		return BaseOutput.success("删除成功")
				.setData(String.valueOf(projectVersion.getId() + ":" + projectVersion.getVersion()));
	}

	@Override
	public ProjectVersionChangeStateViewDto getChangeStateViewData(Long id) {
		ProjectVersion version = this.getActualDao().selectByPrimaryKey(id);
		if (version == null) {
			return null;
		}
		ProjectVersionChangeStateViewDto dto = new ProjectVersionChangeStateViewDto();
		dto.setVersion(version);
		ProjectChange changeQuery = DTOUtils.newDTO(ProjectChange.class);
		changeQuery.setStatus(AlmConstants.ChangeState.PASS.getCode());
		changeQuery.setVersionId(id);
		List<ProjectChange> changes = this.projectChangeService.list(changeQuery);

		Map<Object, Object> metadata = new HashMap<>();

		// JSONObject changeNameProvider = new JSONObject();
		// changeNameProvider.put("provider", "projectChangeProvider");
		// metadata.put("name", changeNameProvider);

		JSONObject phaseProvider = new JSONObject();
		phaseProvider.put("provider", "projectPhaseProvider");
		metadata.put("phaseId", phaseProvider);
		try {
			List<Map> listMap = ValueProviderUtils.buildDataByProvider(metadata, changes);
			dto.setChanges(listMap);
			return dto;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public BaseOutput<Object> changeState(Long id, Integer versionState) {
		ProjectVersion pv = this.getActualDao().selectByPrimaryKey(id);
		if (pv == null) {
			return BaseOutput.failure("版本不存在");
		}
		pv.setVersionState(versionState);
		int result = this.getActualDao().updateByPrimaryKey(pv);
		if (result > 0) {
			try {
				Map<Object, Object> target = ProjectVersionProvider.parseEasyUiModel(pv);
				return BaseOutput.success().setData(target);
			} catch (Exception e) {
				return BaseOutput.failure(e.getMessage());
			}
		}
		return BaseOutput.failure();
	}

	@Override
	public List<Files> listFiles(Long versionId) {
		Example example = new Example(Files.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("versionId", versionId).andIsNull("phaseId");
		return this.filesMapper.selectByExample(example);
	}

	// @Override
	// public EasyuiPageOutput listEasyuiPageByExample(ProjectVersion
	// projectVersion, boolean useProvider) throws Exception {
	// SessionContext sessionContext = SessionContext.getSessionContext();
	// if (sessionContext == null) {
	// throw new RuntimeException("未登录");
	// }
	// List<Map> dataauth =
	// sessionContext.dataAuth(AlmConstants.DATA_AUTH_TYPE_PROJECT);
	// List<Long> projectIds = new ArrayList<>(dataauth.size());
	// dataauth.forEach(t -> {
	// projectIds.add(Long.parseLong(t.get("dataId").toString()));
	// });
	// MilestonesDto milestonesDto = DTOUtils.as(projectVersion,
	// MilestonesDto.class);
	// if (projectIds.isEmpty()) {
	// return new EasyuiPageOutput(0, null);
	// }
	// milestonesDto.setProjectIds(projectIds);
	// return super.listEasyuiPageByExample(milestonesDto, useProvider);
	// }

}