package com.dili.alm.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.domain.FileType;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.ProjectVersionFormDto;
import com.dili.alm.domain.dto.ProjectVersionChangeStateViewDto;
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

	public ProjectVersionMapper getActualDao() {
		return (ProjectVersionMapper) getDao();
	}

	@Transactional
	@Override
	public BaseOutput<Object> insertSelectiveWithOutput(ProjectVersionFormDto dto) {
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
		// 如果要通知，则生成调度信息
		// if (projectVersion.getEmailNotice().equals(1)) {
		// ScheduleJob scheduleJob = DTOUtils.newDTO(ScheduleJob.class);
		// scheduleJob.setJobStatus(QuartzConstants.JobStatus.NORMAL.getCode());
		// scheduleJob.setIsConcurrent(QuartzConstants.Concurrent.Async.getCode());
		// scheduleJob.setJobGroup("milestones");
		// scheduleJob.setJobName(projectVersion.getCode());
		// scheduleJob.setDescription("里程碑发布通知, code:" + projectVersion.getCode() + ",
		// version:" + projectVersion.getVersion());
		// scheduleJob.setSpringId("emailNoticeJob");
		// scheduleJob.setStartDelay(0);
		// scheduleJob.setMethodName("scan");
		// scheduleJob.setCronExpression(CronDateUtils.getCron(new
		// Date(System.currentTimeMillis() + 10000)));
		// scheduleJob.setJobData(JSONObject.toJSONStringWithDateFormat(projectVersion,
		// "yyyy-MM-dd HH:mm:ss"));
		// scheduleJobService.insertSelective(scheduleJob, true);
		// }

		try {
			Map<Object, Object> target = this.parseEasyUiModel(dto);
			return BaseOutput.success("新增成功").setData(target);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@Override
	public BaseOutput<Object> updateSelectiveWithOutput(ProjectVersionFormDto dto) {
		ProjectVersion query = DTOUtils.newDTO(ProjectVersion.class);
		query.setProjectId(dto.getProjectId());
		query.setVersion(dto.getVersion());
		ProjectVersion version = this.getActualDao().selectOne(query);
		if (version != null && !version.getId().equals(dto.getId())) {
			return BaseOutput.failure("版本已存在");
		}
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		dto.setModifierId(userTicket.getId());
		dto.setModified(new Date());
		super.update(dto);
		if (CollectionUtils.isNotEmpty(dto.getFileIds())) {
			dto.getFileIds().forEach(id -> {
				Files file = this.filesService.get(id);
				file.setProjectId(dto.getProjectId());
				file.setVersionId(dto.getId());
				file.setType(FileType.PROJECT_INTRODUCE.getValue());
				this.filesService.updateSelective(file);
			});
		}
		// UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		// dto.setModifierId(userTicket.getId());
		// dto.setModified(new Date());
		// // 如果要通知，刷新调度信息
		// if (dto.getEmailNotice().equals(1)) {
		// ProjectVersion oriMilestones = get(dto.getId());
		// dto = DTOUtils.link(dto, oriMilestones, ProjectVersion.class);
		// ScheduleJob scheduleJob = DTOUtils.newDTO(ScheduleJob.class);
		// scheduleJob.setJobStatus(QuartzConstants.JobStatus.NORMAL.getCode());
		// scheduleJob.setIsConcurrent(QuartzConstants.Concurrent.Async.getCode());
		// scheduleJob.setJobGroup("milestones");
		// scheduleJob.setJobName(dto.getCode());
		// scheduleJob.setDescription("里程碑修改通知, code:" + dto.getCode() + ", version:" +
		// dto.getVersion());
		// scheduleJob.setSpringId("emailNoticeJob");
		// scheduleJob.setStartDelay(0);
		// scheduleJob.setMethodName("scan");
		// scheduleJob.setCronExpression(CronDateUtils.getCron(new
		// Date(System.currentTimeMillis() + 10000)));
		// scheduleJob.setJobData(JSONObject.toJSONStringWithDateFormat(dto, "yyyy-MM-dd
		// HH:mm:ss"));
		// ScheduleJob scheduleJobCondition = DTOUtils.newDTO(ScheduleJob.class);
		// scheduleJobCondition.setJobGroup("milestones");
		// scheduleJobCondition.setJobName(dto.getCode());
		// List<ScheduleJob> scheduleJobs =
		// scheduleJobService.list(scheduleJobCondition);
		// // 如果数据库没有调度信息，则新增调度器
		// if (ListUtils.emptyIfNull(scheduleJobs).isEmpty()) {
		// scheduleJobService.insertSelective(scheduleJob, true);
		// } else {
		// scheduleJob.setId(scheduleJobs.get(0).getId());
		// scheduleJobService.updateSelective(scheduleJob, true);
		// }
		// }
		// super.updateSelective(dto);
		Map<Object, Object> target;
		try {
			target = this.parseEasyUiModel(dto);
			return BaseOutput.success("修改成功").setData(target);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@Override
	public BaseOutput deleteWithOutput(Long id) {
		// 没有子里程碑才能删除，并且删除所有里程碑下的文件
		Files files = DTOUtils.newDTO(Files.class);
		files.setVersionId(id);
		List<Files> filesList = filesService.list(files);
		filesList.forEach(f -> {
			filesService.delete(f);
		});
		super.delete(id);
		return BaseOutput.success("删除成功");
	}

	@Override
	public ProjectVersionChangeStateViewDto getChangeStateViewData(Long id) {
		ProjectVersion version = this.getActualDao().selectByPrimaryKey(id);
		if (version == null) {
			return null;
		}
		ProjectVersionChangeStateViewDto dto = new ProjectVersionChangeStateViewDto();
		dto.setVersion(version);
		ProjectChange condition = DTOUtils.newDTO(ProjectChange.class);
		condition.setVersionId(id);
		List<ProjectChange> changes = this.projectChangeService.list(condition);

		Map<Object, Object> metadata = new HashMap<>();

		JSONObject changeNameProvider = new JSONObject();
		changeNameProvider.put("provider", "changeNameProvider");
		metadata.put("name", changeNameProvider);

		JSONObject phaseProvider = new JSONObject();
		phaseProvider.put("provider", "phaseProvider");
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
				Map<Object, Object> target = this.parseEasyUiModel(pv);
				return BaseOutput.success().setData(target);
			} catch (Exception e) {
				return BaseOutput.failure(e.getMessage());
			}
		}
		return BaseOutput.failure();
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

	private Map<Object, Object> parseEasyUiModel(ProjectVersion projectVersion) throws Exception {
		List<Map> listMap = this.parseEasyUiModelList(Arrays.asList(projectVersion));
		return listMap.get(0);
	}

	private List<Map> parseEasyUiModelList(List<ProjectVersion> list) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();
		JSONObject versionStateProvider = new JSONObject();
		versionStateProvider.put("provider", "projectStateProvider");
		metadata.put("versionState", versionStateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("plannedStartDate", datetimeProvider);
		metadata.put("plannedEndDate", datetimeProvider);
		metadata.put("actualStartDate", datetimeProvider);

		JSONObject onlineProvider = new JSONObject();
		onlineProvider.put("provider", "onlineProvider");
		metadata.put("online", onlineProvider);
		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

}