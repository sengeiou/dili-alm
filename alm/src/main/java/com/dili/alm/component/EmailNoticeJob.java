package com.dili.alm.component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.User;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.quartz.domain.ScheduleJob;
import com.dili.ss.quartz.domain.ScheduleMessage;
import com.dili.ss.quartz.service.ScheduleJobService;
import com.dili.ss.util.SystemConfigUtils;

/**
 * Created by asiamaster on 2017/10/24 0024.
 */
@Component
public class EmailNoticeJob implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger log = LoggerFactory.getLogger(EmailNoticeJob.class);

	@Autowired
	ScheduleJobService scheduleJobService;

	@Autowired
	TeamService teamService;

	@Autowired
	FilesService filesService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserRpc userRpc;

	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private TeamMapper teamMapper;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
			List<ScheduleJob> scheduleJobs = scheduleJobService.list(null);
			for (ScheduleJob job : scheduleJobs) {
				scheduleJobService.addJob(job, true);
			}
		}
	}

	public void initUserMap() {
		// 应用启动时初始化userMap
		if (AlmCache.userMap.isEmpty()) {
			BaseOutput<List<User>> output = userRpc.list(new User());
			if (output.isSuccess()) {
				output.getData().forEach(user -> {
					AlmCache.userMap.put(user.getId(), user);
				});
			}
		}
	}

	/**
	 * 发布email
	 *
	 * @param scheduleMessage
	 */
	public void scan(ScheduleMessage scheduleMessage) {
		ProjectVersion projectVersion = JSONObject.parseObject(scheduleMessage.getJobData(), ProjectVersion.class);
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = null;
		initUserMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取团队成员
		List<Project> projects = this.projectMapper.getChildProjects(projectVersion.getProjectId());
		if (CollectionUtils.isEmpty(projects)) {
			return;
		}
		List<Long> projectIds = new ArrayList<>(projects.size());
		projects.forEach(p -> {
			projectIds.add(p.getId());
		});
		List<Team> teams = this.teamMapper.listByProjectIds(projectIds);
		// 这个地方用来去除重复的用户id
		Set<Long> sentUserIds = new HashSet<>();

		String path = "fileupload/milestones/" + projectVersion.getId() + "/";
		// 获取里程碑相关文件
		Files filesCondition = DTOUtils.newDTO(Files.class);
		filesCondition.setMilestonesId(projectVersion.getId());
		List<Files> files = filesService.list(filesCondition);
		String from = SystemConfigUtils.getProperty("spring.mail.username");
		for (Team team : teams) {
			try {
				if (sentUserIds.contains(team.getMemberId())) {
					continue;
				}
				helper = new MimeMessageHelper(mimeMessage, true);
				helper.setFrom(from);
				helper.setTo(AlmCache.userMap.get(team.getMemberId()).getEmail());
				helper.setSubject("主题：里程碑[" + projectVersion.getCode() + "]发布");
				helper.setText("里程碑[" + projectVersion.getCode() + "]发布, \r\n版本号:" + projectVersion.getVersion() + ",\r\n文档地址:" + projectVersion.getRedmineUrl()
						+ ", \r\ngit地址:" + projectVersion.getGit() + ", \r\n发布人:" + AlmCache.userMap.get(projectVersion.getCreatorId()).getRealName()
						+ ", \r\n发布时间:" + sdf.format(projectVersion.getReleaseTime()) + ", \r\n访问地址:" + projectVersion.getVisitUrl() + ", \r\n备注:" + projectVersion.getNotes());
				// 给团队所有成员发送附件
				for (Files files1 : files) {
					FileSystemResource file = new FileSystemResource(new File(path + files1.getName()));
					helper.addAttachment(files1.getName(), file);
				}
				Thread.sleep(2000);
				mailSender.send(mimeMessage);
				sentUserIds.add(team.getMemberId());
			} catch (MessagingException e) {
				log.error("MessagingException, 邮件发送失败, email:" + AlmCache.userMap.get(team.getMemberId()).getEmail());
				e.printStackTrace();
				log.error(e.getMessage());
				return;
			} catch (Exception e) {
				log.error("Exception, 邮件发送失败, email:" + AlmCache.userMap.get(team.getMemberId()).getEmail());
				e.printStackTrace();
				log.error(e.getMessage());
				return;
			}
		}

	}

}
