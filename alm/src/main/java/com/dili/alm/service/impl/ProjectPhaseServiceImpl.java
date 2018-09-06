package com.dili.alm.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.FileType;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.ProjectVersionState;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectPhaseFormDto;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 16:05:32.
 */
@Service
public class ProjectPhaseServiceImpl extends BaseServiceImpl<ProjectPhase, Long> implements ProjectPhaseService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectPhaseServiceImpl.class);

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
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectVersionMapper projectVersionMapper;

	public ProjectPhaseMapper getActualDao() {
		return (ProjectPhaseMapper) getDao();
	}

	@Override
	public List<DataDictionaryValueDto> getPhaseNames() {
		DataDictionaryDto dataDictionary = this.dataDictionaryService.findByCode(AlmConstants.PHASE_NAME_CODE);
		if (dataDictionary == null) {
			return null;
		}
		return dataDictionary.getValues();
	}

	@Override
	public ProjectPhase getAddViewData(Long projectId) {
		ProjectPhase dto = DTOUtils.newDTO(ProjectPhase.class);
		Project project = this.projectService.get(projectId);
		dto.aset("project1", project);
		ProjectVersion query = DTOUtils.newDTO(ProjectVersion.class);
		query.setProjectId(projectId);
		List<ProjectVersion> allVersions = this.projectVersionService.list(query);
		dto.aset("allVersions", allVersions);
		dto.aset("phaseNames", this.getPhaseNames());
		return dto;
	}

	@Override
	public ProjectPhase getEditViewData(Long id) {
		ProjectPhase dto = this.getActualDao().selectByPrimaryKey(id);
		Project project = this.projectService.get(dto.getProjectId());
		dto.aset("project1", project);
		ProjectVersion version = DTOUtils.newDTO(ProjectVersion.class);
		version.setProjectId(dto.getProjectId());
		List<ProjectVersion> versions = this.projectVersionService.list(version);
		dto.aset("allVersions", versions);
		dto.aset("phaseNames", this.getPhaseNames());
		Files query = DTOUtils.newDTO(Files.class);
		query.setPhaseId(id);
		List<Files> files = this.filesService.list(query);
		dto.aset("files", files);
		return dto;
	}

	@Override
	public void addProjectPhase(ProjectPhaseFormDto dto) throws ProjectPhaseException {
		ProjectPhase query = DTOUtils.newDTO(ProjectPhase.class);
		query.setProjectId(dto.getProjectId());
		query.setVersionId(dto.getVersionId());
		query.setName(dto.getName());
		int count = this.getActualDao().selectCount(query);
		if (count > 0) {
			throw new ProjectPhaseException("该项目版本已存在相同阶段");
		}
		this.checkProjectState(dto);
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
	}

	private void checkProjectState(ProjectPhase dto) throws ProjectPhaseException {
		// 判断项目状态
		Project project = this.projectMapper.selectByPrimaryKey(dto.getProjectId());
		if (project == null) {
			throw new ProjectPhaseException("项目不存在");
		}
		if (!project.getProjectState().equals(ProjectState.NOT_START.getValue())
				&& !project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue())) {
			throw new ProjectPhaseException("当前项目状态不能创建阶段");
		}

		// 判断版本状态
		ProjectVersion version = this.projectVersionMapper.selectByPrimaryKey(dto.getVersionId());
		if (version == null) {
			throw new ProjectPhaseException("当前项目状态不能创建阶段");
		}
		if (!version.getVersionState().equals(ProjectVersionState.NOT_START.getValue())
				&& !version.getVersionState().equals(ProjectVersionState.IN_PROGRESS.getValue())) {
			throw new ProjectPhaseException("当前版本状态不能创建阶段");
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Map> listEasyUiModels(ProjectPhase query) {
		List<ProjectPhase> list = this.list(query);
		list.forEach(p -> {
			ProjectVersion version = this.projectVersionMapper.selectByPrimaryKey(p.getVersionId());
			p.aset("versionState", version.getVersionState());
		});
		try {
			return parseEasyUiModelList(list);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateProjectPhase(ProjectPhaseFormDto dto) throws ProjectPhaseException {
		this.checkProjectState(dto);
		ProjectPhase query = DTOUtils.newDTO(ProjectPhase.class);
		query.setProjectId(dto.getProjectId());
		query.setVersionId(dto.getVersionId());
		query.setName(dto.getName());
		ProjectPhase old = this.getActualDao().selectOne(query);
		if (old != null && !old.getId().equals(dto.getId())) {
			throw new ProjectPhaseException("该项目已存在相同阶段");
		}
		old = this.getActualDao().selectByPrimaryKey(dto.getId());
		old.setVersionId(dto.getVersionId());
		old.setName(dto.getName());
		old.setPlannedStartDate(dto.getPlannedStartDate());
		old.setPlannedEndDate(dto.getPlannedEndDate());
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		old.setModifierId(userTicket.getId());
		old.setModified(new Date());
		super.update(old);
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
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<Object, Object> parseEasyUiModel(ProjectPhase projectPahse) throws Exception {
		List<Map> listMap = parseEasyUiModelList(Arrays.asList(projectPahse));
		return listMap.get(0);
	}

	@SuppressWarnings("rawtypes")
	public static List<Map> parseEasyUiModelList(List<ProjectPhase> list) throws Exception {
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

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public BaseOutput<Object> deleteWithOutput(Long id) {
		Task taskQuery = DTOUtils.newDTO(Task.class);
		taskQuery.setPhaseId(id);
		int count = this.taskMapper.selectCount(taskQuery);
		if (count > 0) {
			return BaseOutput.failure("该阶段包含任务不能删除");
		}
		ProjectPhase projectPhase = this.get(id);
		int result = this.delete(id);
		if (result <= 0) {
			return BaseOutput.failure("删除失败");
		}
		Files condition = DTOUtils.newDTO(Files.class);
		condition.setPhaseId(id);
		Files selectOne = fileMapper.selectOne(condition);
		if (selectOne != null) {
			this.fileMapper.delete(condition);
		}
		Map<Object, Object> model = null;
		try {
			model = parseEasyUiModel(projectPhase);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return BaseOutput.success("删除成功").setData(model);
	}

}