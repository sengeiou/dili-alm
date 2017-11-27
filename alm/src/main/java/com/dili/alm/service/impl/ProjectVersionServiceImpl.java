package com.dili.alm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.dto.MilestonesDto;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.quartz.domain.QuartzConstants;
import com.dili.ss.quartz.domain.ScheduleJob;
import com.dili.ss.quartz.service.ScheduleJobService;
import com.dili.ss.util.CronDateUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-20 11:02:17.
 */
@Service
public class ProjectVersionServiceImpl extends BaseServiceImpl<ProjectVersion, Long> implements ProjectVersionService {

	@Autowired
	FilesService filesService;

	@Autowired
	ScheduleJobService scheduleJobService;

	public ProjectVersionMapper getActualDao() {
		return (ProjectVersionMapper) getDao();
	}

	@Override
	public BaseOutput insertSelectiveWithOutput(ProjectVersion projectVersion) {
		ProjectVersion milestonesCondition = DTOUtils.newDTO(ProjectVersion.class);
		milestonesCondition.setCode(projectVersion.getCode());
		if (list(milestonesCondition).size() > 0) {
			return BaseOutput.failure("里程碑编码已存在!");
		}
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		projectVersion.setCreatorId(userTicket.getId());
		projectVersion.setCreated(new Date());
		super.insertSelective(projectVersion);
		// 如果要通知，则生成调度信息
		if (projectVersion.getEmailNotice().equals(1)) {
			ScheduleJob scheduleJob = DTOUtils.newDTO(ScheduleJob.class);
			scheduleJob.setJobStatus(QuartzConstants.JobStatus.NORMAL.getCode());
			scheduleJob.setIsConcurrent(QuartzConstants.Concurrent.Async.getCode());
			scheduleJob.setJobGroup("milestones");
			scheduleJob.setJobName(projectVersion.getCode());
			scheduleJob.setDescription("里程碑发布通知, code:" + projectVersion.getCode() + ", version:" + projectVersion.getVersion());
			scheduleJob.setSpringId("emailNoticeJob");
			scheduleJob.setStartDelay(0);
			scheduleJob.setMethodName("scan");
			scheduleJob.setCronExpression(CronDateUtils.getCron(new Date(System.currentTimeMillis() + 10000)));
			scheduleJob.setJobData(JSONObject.toJSONStringWithDateFormat(projectVersion, "yyyy-MM-dd HH:mm:ss"));
			scheduleJobService.insertSelective(scheduleJob, true);
		}
		return BaseOutput.success("新增成功");
	}

	@Override
	public BaseOutput updateSelectiveWithOutput(ProjectVersion projectVersion) {
		ProjectVersion milestonesCondition = DTOUtils.newDTO(ProjectVersion.class);
		milestonesCondition.setCode(projectVersion.getCode());
		List<ProjectVersion> milestones1 = list(milestonesCondition);
		// 如果和原有编码不同，并且还有其它的重复编码，则抛错
		if (milestones1.size() > 0 && !get(projectVersion.getId()).getCode().equals(projectVersion.getCode())) {
			return BaseOutput.failure("里程碑编码已存在!");
		}
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		projectVersion.setModifierId(userTicket.getId());
		projectVersion.setModified(new Date());
		// 如果要通知，刷新调度信息
		if (projectVersion.getEmailNotice().equals(1)) {
			ProjectVersion oriMilestones = get(projectVersion.getId());
			projectVersion = DTOUtils.link(projectVersion, oriMilestones, ProjectVersion.class);
			ScheduleJob scheduleJob = DTOUtils.newDTO(ScheduleJob.class);
			scheduleJob.setJobStatus(QuartzConstants.JobStatus.NORMAL.getCode());
			scheduleJob.setIsConcurrent(QuartzConstants.Concurrent.Async.getCode());
			scheduleJob.setJobGroup("milestones");
			scheduleJob.setJobName(projectVersion.getCode());
			scheduleJob.setDescription("里程碑修改通知, code:" + projectVersion.getCode() + ", version:" + projectVersion.getVersion());
			scheduleJob.setSpringId("emailNoticeJob");
			scheduleJob.setStartDelay(0);
			scheduleJob.setMethodName("scan");
			scheduleJob.setCronExpression(CronDateUtils.getCron(new Date(System.currentTimeMillis() + 10000)));
			scheduleJob.setJobData(JSONObject.toJSONStringWithDateFormat(projectVersion, "yyyy-MM-dd HH:mm:ss"));
			ScheduleJob scheduleJobCondition = DTOUtils.newDTO(ScheduleJob.class);
			scheduleJobCondition.setJobGroup("milestones");
			scheduleJobCondition.setJobName(projectVersion.getCode());
			List<ScheduleJob> scheduleJobs = scheduleJobService.list(scheduleJobCondition);
			// 如果数据库没有调度信息，则新增调度器
			if (ListUtils.emptyIfNull(scheduleJobs).isEmpty()) {
				scheduleJobService.insertSelective(scheduleJob, true);
			} else {
				scheduleJob.setId(scheduleJobs.get(0).getId());
				scheduleJobService.updateSelective(scheduleJob, true);
			}
		}
		super.updateSelective(projectVersion);
		return BaseOutput.success("修改成功");
	}

	@Override
	public BaseOutput deleteWithOutput(Long id) {
		// 没有子里程碑才能删除，并且删除所有里程碑下的文件
		Files files = DTOUtils.newDTO(Files.class);
		files.setMilestonesId(id);
		List<Files> filesList = filesService.list(files);
		for (Files file : filesList) {
			filesService.delete(file);
		}
		// 如果有一个文件，则删除文件目录
		if (!filesList.isEmpty()) {
			File dest = new File(filesList.get(0).getUrl());
			dest.delete();
		}
		super.delete(id);
		return BaseOutput.success("删除成功");
	}

	@Override
	public EasyuiPageOutput listEasyuiPageByExample(ProjectVersion projectVersion, boolean useProvider) throws Exception {
		SessionContext sessionContext = SessionContext.getSessionContext();
		if (sessionContext == null) {
			throw new RuntimeException("未登录");
		}
		List<Map> dataauth = sessionContext.dataAuth(AlmConstants.DATA_AUTH_TYPE_PROJECT);
		List<Long> projectIds = new ArrayList<>(dataauth.size());
		dataauth.forEach(t -> {
			projectIds.add(Long.parseLong(t.get("dataId").toString()));
		});
		MilestonesDto milestonesDto = DTOUtils.as(projectVersion, MilestonesDto.class);
		if (projectIds.isEmpty()) {
			return new EasyuiPageOutput(0, null);
		}
		milestonesDto.setProjectIds(projectIds);
		return super.listEasyuiPageByExample(milestonesDto, useProvider);
	}
}