package com.dili.alm.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.dao.AlarmConfigMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.AlarmConfig;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.service.AlarmConfigService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.quartz.domain.ScheduleMessage;
import com.dili.ss.util.SystemConfigUtils;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-09 15:41:26.
 */
@Service
public class AlarmConfigServiceImpl extends BaseServiceImpl<AlarmConfig, Long> implements AlarmConfigService {

	private static final Logger LOG = LoggerFactory.getLogger(AlarmConfigServiceImpl.class);

	@Autowired
	private ProjectService projectService;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private ProjectVersionService versionService;
	@Autowired
	private TaskMapper taskMapper;

	public AlarmConfigMapper getActualDao() {
		return (AlarmConfigMapper) getDao();
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
	@Override
	public void alarm(ScheduleMessage msg) {
		List<AlarmConfig> configs = this.getActualDao().select(null);
		if (CollectionUtils.isEmpty(configs)) {
			return;
		}
		Date now = new Date();
		configs.forEach(config -> {
			List<Task> tasks = this.taskMapper.selectByProjectState(ProjectState.IN_PROGRESS.getValue());
			tasks.forEach(task -> {
				long diff = config.getThreshold() > 0 ? task.getEndDate().getTime() - now.getTime()
						: now.getTime() - task.getEndDate().getTime();
				if (diff >= (24 * 60 * 60 * 100 * config.getThreshold()) || diff <= 0) {
					Project project = this.projectService.get(task.getProjectId());
					ProjectVersion version = this.versionService.get(task.getVersionId());
					try {
						this.sendMail(project, version, task, now);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}
			});
		});
	}

	private void sendMail(Project project, ProjectVersion version, Task task, Date now)
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
		sb.append("项目名称：").append(project.getName()).append(version.getVersion());
		if (task != null) {
			sb.append("\r\n任务名称：").append(task.getName()).append("\r\n计划结束日期：").append(sdf.format(task.getEndDate()));
		}
		sb.append("\r\n当前日期：").append(sdf.format(now));
		helper.setText(sb.toString());
		Thread.sleep(2000);
		mailSender.send(mimeMessage);
	}
}