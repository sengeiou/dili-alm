package com.dili.alm.component;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Milestones;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

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

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
			List<ScheduleJob> scheduleJobs = scheduleJobService.list(null);
			for (ScheduleJob job : scheduleJobs) {
				scheduleJobService.addJob(job, true);
			}
		}
	}

	public void initUserMap(){
		//应用启动时初始化userMap
		if(AlmCache.userMap.isEmpty()){
			BaseOutput<List<User>> output = userRpc.list(new User());
			if(output.isSuccess()){
				output.getData().forEach(user ->{
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
		Milestones milestones = JSONObject.parseObject(scheduleMessage.getJobData(), Milestones.class);
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = null;
		initUserMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Team teamCondition = DTOUtils.newDTO(Team.class);
		teamCondition.setProjectId(milestones.getProjectId());
		teamCondition.setMemberState(AlmConstants.MemberState.JOIN.getCode());
		//获取团队成员
		List<Team> teams = teamService.list(teamCondition);
		String path = "fileupload/milestones/" + milestones.getId() + "/";
		//获取里程碑相关文件
		Files filesCondition = DTOUtils.newDTO(Files.class);
		filesCondition.setMilestonesId(milestones.getId());
		List<Files> files = filesService.list(filesCondition);
		String from = SystemConfigUtils.getProperty("spring.mail.username");
		for (Team team : teams) {
			try {
				helper = new MimeMessageHelper(mimeMessage, true);
				helper.setFrom(from);
				helper.setTo(AlmCache.userMap.get(team.getMemberId()).getEmail());
				helper.setSubject("主题：里程碑[" + milestones.getCode() + "]发布");
 				helper.setText("里程碑[" + milestones.getCode() + "]发布, \r\n版本号:" + milestones.getVersion()
						+ ", \r\n市场:" + milestones.getMarket()
						+ ", \r\n文档地址:" + milestones.getDocUrl()
						+ ", \r\ngit地址:" + milestones.getGit()
						+ ", \r\n项目阶段:" + milestones.getProjectPhase()
						+ ", \r\n发布人:" + AlmCache.userMap.get(milestones.getPublishMemberId()).getRealName()
						+ ", \r\n发布时间:" + sdf.format(milestones.getReleaseTime())
						+ ", \r\n访问地址:" + milestones.getVisitUrl()
						+ ", \r\n备注:" + milestones.getNotes());
				//给团队所有成员发送附件
				for (Files files1 : files) {
					FileSystemResource file = new FileSystemResource(new File(path + files1.getName()));
					helper.addAttachment(files1.getName(), file);
				}
				mailSender.send(mimeMessage);
			} catch (MessagingException e) {
				e.printStackTrace();
				return;
			}
		}

	}

}
