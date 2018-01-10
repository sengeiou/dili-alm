package com.dili.alm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.*;
import com.dili.alm.domain.dto.*;
import com.dili.alm.service.*;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 16:05:32.
 */
@Service
public class ProjectPhaseServiceImpl extends BaseServiceImpl<ProjectPhase, Long> implements ProjectPhaseService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectPhaseServiceImpl.class);

	private static final String PHASE_NAME_CODE = "phase_name";
	@Autowired
	private DataDictionaryService dataDictionaryService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectVersionService projectVersionService;
	@Autowired
	private FilesService filesService;
	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private FilesMapper fileMapper;
	@Autowired
	private ProjectChangeMapper projectChangeMapper;

	public ProjectPhaseMapper getActualDao() {
		return (ProjectPhaseMapper) getDao();
	}

	@Override
	public List<DataDictionaryValueDto> getPhaseNames() {
		DataDictionaryDto dataDictionary = this.dataDictionaryService.findByCode(PHASE_NAME_CODE);
		if (dataDictionary == null) {
			return null;
		}
		return dataDictionary.getValues();
	}

	@Override
	public ProjectPhaseAddViewDto getAddViewData(Long projectId) {
		ProjectPhaseAddViewDto dto = new ProjectPhaseAddViewDto();
		Project project = this.projectService.get(projectId);
		dto.setProject(project);
		ProjectVersion query = DTOUtils.newDTO(ProjectVersion.class);
		query.setProjectId(projectId);
		List<ProjectVersion> allVersions = this.projectVersionService.list(query);
		dto.setAllVersions(allVersions);
		dto.setPhaseNames(this.getPhaseNames());
		return dto;
	}

	@Override
	public ProjectPhaseEditViewDto getEditViewData(Long id) {
		ProjectPhase po = this.getActualDao().selectByPrimaryKey(id);
		ProjectPhaseEditViewDto dto = DTOUtils.toEntity(po, ProjectPhaseEditViewDto.class, false);
		Project project = this.projectService.get(dto.getProjectId());
		dto.setProject(project);
		ProjectVersion version = DTOUtils.newDTO(ProjectVersion.class);
		version.setProjectId(dto.getProjectId());
		List<ProjectVersion> versions = this.projectVersionService.list(version);
		dto.setAllVersions(versions);
		dto.setPhaseNames(this.getPhaseNames());
		Files query = DTOUtils.newDTO(Files.class);
		query.setPhaseId(id);
		List<Files> files = this.filesService.list(query);
		dto.setFiles(files);
		return dto;
	}

	@Override
	public BaseOutput<Object> addProjectPhase(ProjectPhaseFormDto dto) {
		ProjectPhase query = DTOUtils.newDTO(ProjectPhase.class);
		query.setProjectId(dto.getProjectId());
		query.setVersionId(dto.getVersionId());
		query.setName(dto.getName());
		int count = this.getActualDao().selectCount(query);
		if (count > 0) {
			return BaseOutput.failure("该项目版本已存在相同阶段");
		}
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		dto.setCreatorId(userTicket.getId());
		dto.setCreated(new Date());
		super.insertSelective(dto);
		if (CollectionUtils.isNotEmpty(dto.getFileIds())) {
			dto.getFileIds().forEach(id -> {
				Files file = this.filesService.get(id);
				file.setProjectId(dto.getProjectId());
				file.setVersionId(dto.getVersionId());
				file.setPhaseId(dto.getId());
				file.setType(FileType.PROJECT_INTRODUCE.getValue());
				this.filesService.updateSelective(file);
			});
		}
		try {
			Map<Object, Object> model = this.parseEasyUiModel(dto);
			return BaseOutput.success().setData(model);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return BaseOutput.failure("新增失败");
		}
	}

	@Override
	public List<Map> listEasyUiModels(ProjectPhase query) {
		List<ProjectPhase> list = this.list(query);
		try {
			return this.parseEasyUiModelList(list);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public BaseOutput<Object> updateProjectPhase(ProjectPhaseFormDto dto) {
		ProjectPhase query = DTOUtils.newDTO(ProjectPhase.class);
		query.setProjectId(dto.getProjectId());
		query.setVersionId(dto.getVersionId());
		query.setName(dto.getName());
		ProjectPhase old = this.getActualDao().selectOne(query);
		if (old != null && !old.getId().equals(dto.getId())) {
			return BaseOutput.failure("该项目已存在相同阶段");
		}
		old = this.getActualDao().selectByPrimaryKey(dto.getId());
		old.setVersionId(dto.getVersionId());
		old.setName(dto.getName());
		old.setPlannedStartDate(dto.getPlannedStartDate());
		old.setPlannedEndDate(dto.getPlannedEndDate());
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		old.setModifierId(userTicket.getId());
		old.setModified(new Date());
		super.updateSelective(old);
		if (CollectionUtils.isNotEmpty(dto.getFileIds())) {
			dto.getFileIds().forEach(id -> {
				Files file = this.filesService.get(id);
				file.setProjectId(dto.getProjectId());
				file.setVersionId(dto.getVersionId());
				file.setPhaseId(dto.getId());
				file.setType(FileType.PROJECT_INTRODUCE.getValue());
				this.filesService.updateSelective(file);
			});
		}
		try {
			Map<Object, Object> model = this.parseEasyUiModel(old);
			return BaseOutput.success().setData(model);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return BaseOutput.failure("修改失败");
		}
	}

	private Map<Object, Object> parseEasyUiModel(ProjectPhase projectPahse) throws Exception {
		List<Map> listMap = this.parseEasyUiModelList(Arrays.asList(projectPahse));
		return listMap.get(0);
	}

	private List<Map> parseEasyUiModelList(List<ProjectPhase> list) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject phaseNameProvider = new JSONObject();
		phaseNameProvider.put("provider", "phaseNameProvider");
		metadata.put("name", phaseNameProvider);

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);

		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("provider", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject almDateProvider = new JSONObject();
		almDateProvider.put("provider", "almDateProvider");
		metadata.put("plannedStartDate", almDateProvider);
		metadata.put("plannedEndDate", almDateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("actualStartDate", datetimeProvider);

		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

	@Transactional
	@Override
	public BaseOutput<Object> deleteWithOutput(Long id) {
		Task taskQuery = DTOUtils.newDTO(Task.class);
		taskQuery.setPhaseId(id);
		int count = this.taskMapper.selectCount(taskQuery);
		if (count > 0) {
			return BaseOutput.failure("该阶段包含任务不能删除");
		}
		ProjectChange changeQuery = DTOUtils.newDTO(ProjectChange.class);
		changeQuery.setPhaseId(id);
		count = this.projectChangeMapper.selectCount(changeQuery);
		if (count > 0) {
			return BaseOutput.failure("该阶段关联了需求变更不能删除");
		}
		int result = this.delete(id);
		if (result <= 0) {
			return BaseOutput.failure("删除失败");
		}
		Files condition = DTOUtils.newDTO(Files.class);
		condition.setPhaseId(id);
		this.fileMapper.delete(condition);
		return BaseOutput.success();
	}

}