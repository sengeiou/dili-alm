package com.dili.alm.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants.TaskStatus;
import com.dili.alm.dao.AlarmConfigMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.AlarmConfig;
import com.dili.alm.domain.AlarmType;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.service.AlarmConfigService;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.util.SystemConfigUtils;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-09 15:41:26.
 */
@Service
public class AlarmConfigServiceImpl extends BaseServiceImpl<AlarmConfig, Long> implements AlarmConfigService {

	private static final Logger LOG = LoggerFactory.getLogger(AlarmConfigServiceImpl.class);

	private static final String ALARM_TYPE_CODE = "alarm_type";

	@Autowired
	private DataDictionaryService dataDictionaryService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectPhaseService phaseService;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private ProjectVersionService versionService;
	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private ProjectPhaseMapper phaseMapper;

	public AlarmConfigMapper getActualDao() {
		return (AlarmConfigMapper) getDao();
	}

	@Override
	public List<DataDictionaryValueDto> getTypes() {
		DataDictionaryDto dd = this.dataDictionaryService.findByCode(ALARM_TYPE_CODE);
		if (dd == null) {
			return null;
		}
		return dd.getValues();
	}

	@Override
	public BaseOutput<Object> saveOrUpdateWithOutput(AlarmConfig alarmConfig) {
		int result = 0;
		AlarmConfig query = DTOUtils.newDTO(AlarmConfig.class);
		query.setProjectId(alarmConfig.getProjectId());
		query.setType(alarmConfig.getType());
		AlarmConfig old = this.getActualDao().selectOne(query);
		if (old != null) {
			old.setThreshold(alarmConfig.getThreshold());
			result = this.updateSelective(old);
		} else {
			Project project = this.projectService.get(alarmConfig.getProjectId());
			if (project == null) {
				return BaseOutput.failure("项目不存在");
			}
			alarmConfig.setPlannedEndDate(project.getEndDate());
			result = this.insertSelective(alarmConfig);
		}
		return result > 0 ? BaseOutput.success() : BaseOutput.failure();
	}

	// 每天早上6点检查项目进度完成情况并发送邮件告警
	@Scheduled(cron = "0 0 6 * * ?")
	@Override
	public void alarm() {
		List<AlarmConfig> configs = this.getActualDao().select(null);
		if (CollectionUtils.isEmpty(configs)) {
			return;
		}
		Date now = new Date();
		configs.forEach(config -> {
			if (config.getType().equals(AlarmType.PHASE.getValue())) {
				List<ProjectPhase> phases = this.phaseMapper.selectByProjectState(ProjectState.IN_PROGRESS.getValue());
				if (CollectionUtils.isEmpty(phases)) {
					return;
				}
				phases.forEach(phase -> {
					long diff = config.getThreshold() > 0 ? phase.getPlannedEndDate().getTime() - now.getTime()
							: now.getTime() - phase.getPlannedEndDate().getTime();
					if (diff >= (24 * 60 * 60 * 100 * config.getThreshold()) || diff <= 0) {
						Project project = this.projectService.get(phase.getProjectId());
						ProjectVersion version = this.versionService.get(phase.getVersionId());
						Map<String, Object> taskQuery = new HashMap<>();
						taskQuery.put("phaseId", phase.getId());
						taskQuery.put("startStatus", TaskStatus.START.getCode());
						taskQuery.put("notCompletedStatus", TaskStatus.NOTCOMPLETE);
						int count = this.taskMapper.countNotCompletedByPhaseId(taskQuery);
						if (count > 0) {
							try {
								this.sendMail(project, version, phase, null, now);
							} catch (Exception e) {
								LOG.error(e.getMessage(), e);
							}
						}
						phase.setNotice(true);
						this.phaseService.updateSelective(phase);
					}
				});
			} else if (config.getType().equals(AlarmType.TASK.getValue())) {
				List<Task> tasks = this.taskMapper.selectByProjectState(ProjectState.IN_PROGRESS.getValue());
				tasks.forEach(task -> {
					long diff = config.getThreshold() > 0 ? task.getEndDate().getTime() - now.getTime()
							: now.getTime() - task.getEndDate().getTime();
					if (diff >= (24 * 60 * 60 * 100 * config.getThreshold()) || diff <= 0) {
						Project project = this.projectService.get(task.getProjectId());
						ProjectVersion version = this.versionService.get(task.getVersionId());
						ProjectPhase phase = this.phaseService.get(task.getPhaseId());
						try {
							this.sendMail(project, version, phase, task, now);
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
					}
				});
			}
		});
	}

	private void sendMail(Project project, ProjectVersion version, ProjectPhase phase, Task task, Date now)
			throws MessagingException, InterruptedException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		String from = SystemConfigUtils.getProperty("spring.mail.username");
		helper.setFrom(from);
		User receiver = AlmCache.getInstance().getUserMap().get(project.getProjectManager());
		helper.setTo(receiver.getEmail());
		helper.setSubject("主题：" + project.getName() + "项目进度告警");
		StringBuilder sb = new StringBuilder();
		sb.append("项目名称：").append(project.getName()).append(version.getVersion()).append("\r\n所属阶段：")
				.append(AlmCache.getInstance().getPhaseNameMap().get(phase.getName()));
		if (task != null) {
			sb.append("\r\n任务名称：").append(task.getName()).append("\r\n计划结束日期：").append(sdf.format(task.getEndDate()));
		} else {
			sb.append(sdf.format(phase.getPlannedEndDate()));
		}
		sb.append("\r\n当前日期：").append(sdf.format(now));
		helper.setText(sb.toString());
		Thread.sleep(2000);
		mailSender.send(mimeMessage);
	}
}