package com.dili.alm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.AlmConstants.ApplyState;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.dao.ProjectCompleteMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.domain.Team;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.alm.exceptions.ProjectCompleteException;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.ProjectCompleteService;
import com.dili.alm.service.TeamService;
import com.dili.alm.utils.DateUtil;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.RuntimeRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.SystemConfigUtils;
import com.dili.uap.sdk.session.SessionContext;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-08 11:29:47.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class ProjectCompleteServiceImpl extends BaseServiceImpl<ProjectComplete, Long> implements ProjectCompleteService {

	@Autowired
	private ApproveService approveService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TeamService teamService;
	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private RuntimeRpc runtimeRpc;
	@Autowired
	private TaskRpc tasksRpc;

	public ProjectCompleteMapper getActualDao() {
		return (ProjectCompleteMapper) getDao();
	}

	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public void approve(ProjectComplete complete) throws ProjectApplyException {
		if (complete.getStatus() == null) {
			return;
		}

		/*
		 * 处理生成审批单
		 */
		if (complete.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
			ProjectComplete projectComplete = get(complete.getId());
			Approve newApprove = DTOUtils.newInstance(Approve.class);
			newApprove.setProjectApplyId(complete.getId());
			newApprove.setType(AlmConstants.ApproveType.COMPLETE.getCode());
			Approve selectOne = approveService.selectOne(newApprove);
			Approve as = DTOUtils.as(complete, Approve.class);
			if (selectOne == null) {
				as.setId(null);
				as.setCreated(new Date());
				as.setNumber(projectComplete.getNumber());
				as.setName(projectComplete.getName());
				as.setProjectApplyId(projectComplete.getId());
				as.setStatus(AlmConstants.ApplyState.APPROVE.getCode());
				as.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
				as.setType(AlmConstants.ApproveType.COMPLETE.getCode());
				as.setProjectType(projectComplete.getType());

				approveService.insertBefore(as);
				// 开启引擎流程
				Long userId = SessionContext.getSessionContext().getUserTicket().getId();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("dataId", as.getId().toString());
				BaseOutput<ProcessInstanceMapping> processInstanceOutput = runtimeRpc.startProcessInstanceByKey(BpmConsts.PROJECT_COMPLETE_PROCESS, as.getId() + "", userId + "", map);
				if (!processInstanceOutput.isSuccess()) {
					throw new ProjectApplyException(processInstanceOutput.getMessage());
				}
				// 回调，写入相关流程任务数据
				ProcessInstanceMapping processInstance = processInstanceOutput.getData();
				Approve selectApprove = DTOUtils.newInstance(Approve.class);
				selectApprove.setId(as.getId());
				selectApprove.setProcessInstanceId(processInstance.getProcessInstanceId());
				selectApprove.setProcessDefinitionId(processInstance.getProcessDefinitionId());
				// 修改需求状态，记录流程实例id和流程定义id
				int update = approveService.updateSelective(selectApprove);
				if (update <= 0) {
					throw new ProjectApplyException("提交结项引擎流程失败");
				}
			} else {
				selectOne.setName(projectComplete.getName());
				selectOne.setProjectApplyId(projectComplete.getId());
				selectOne.setStatus(AlmConstants.ApplyState.APPROVE.getCode());
				selectOne.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
				selectOne.setType(AlmConstants.ApproveType.COMPLETE.getCode());
				selectOne.setProjectType(projectComplete.getType());
				approveService.updateBefore(selectOne);
				TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
				taskDto.setProcessInstanceBusinessKey(selectOne.getId().toString());
				BaseOutput<List<TaskMapping>> outputList = tasksRpc.list(taskDto);
				if (!outputList.isSuccess()) {
					throw new AppException("用户错误！" + outputList.getMessage());
				}
				// 获取formKey
				TaskMapping task = outputList.getData().get(0);
				tasksRpc.complete(task.getId(), SessionContext.getSessionContext().getUserTicket().getId().toString());
			}
			sendMail(projectComplete);

		}
	}

	public void sendMail(ProjectComplete complete) {
		try {
			String[] sendTo = complete.getEmail().split(",");
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(sendTo);
			message.setFrom(SystemConfigUtils.getProperty("spring.mail.username"));
			message.setSubject("结项申请");
			message.setText(complete.getName() + "的结项申请已经提交成功");
			mailSender.send(message);
		} catch (Exception e) {
			LOGGER.error("邮件发送出错:", e);
		}
	}

	@Override
	public Long reComplete(Long id) {
		ProjectComplete complete = get(id);
		if (complete == null) {
			return -1L;
		}
		if (!complete.getCreateMemberId().equals(SessionContext.getSessionContext().getUserTicket().getId())) {
			return -1L;
		}
		if (complete.getRestatus() != null) {
			return -1L;
		}
//		complete.setRestatus(0);
//		update(complete);
//		complete.setId(null);
//		complete.setCreated(new Date());
//		complete.setRestatus(null);
//		complete.setStatus(AlmConstants.ApplyState.APPLY.getCode());
//		insertSelective(complete);

		complete.setRestatus(0);
		complete.setStatus(AlmConstants.ApplyState.APPLY.getCode());
		update(complete);
		return complete.getId();
	}

	@Override
	public Object loadMembers(Long id) throws Exception {
		ProjectComplete projectComplete = this.get(id);
		if (StringUtils.isBlank(projectComplete.getMembers())) {
			Team team = DTOUtils.newInstance(Team.class);
			team.setProjectId(projectComplete.getProjectId());
			List<Team> list = teamService.list(team);
			Map<Object, Object> metadata = new HashMap<>();
			metadata.put("memberId", JSON.parse("{provider:'memberProvider'}"));
			metadata.put("role", JSON.parse("{provider:'teamRoleProvider'}"));
			metadata.put("joinTime", JSON.parse("{provider:'datetimeProvider'}"));
			metadata.put("leaveTime", JSON.parse("{provider:'datetimeProvider'}"));
			List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, list);
			maps.forEach(map -> map.put("created", DateUtil.getDate(new Date())));
			return maps;
		}
		return projectComplete.getPerformance();
	}

	@Override
	public void insertWithCheck(ProjectComplete projectComplete) throws ProjectCompleteException {
		// 验证项目是否已结项
		Project project = this.projectMapper.selectByPrimaryKey(projectComplete.getProjectId());
		if (project == null) {
			throw new ProjectCompleteException("项目不存在");
		}
		if (project.getCloseTime() != null) {
			throw new ProjectCompleteException("该项目已结项，不能重复结项");
		}
		ProjectComplete pcQuery = DTOUtils.newInstance(ProjectComplete.class);
		pcQuery.setProjectId(projectComplete.getProjectId());
		List<ProjectComplete> list = this.getActualDao().select(pcQuery);
		if (CollectionUtils.isNotEmpty(list)) {
			ProjectComplete target = list.stream().filter(pc -> ((!pc.getStatus().equals(ApplyState.NOPASS.getCode())) && (!pc.getStatus().equals(ApplyState.REFUSED.getCode())))).findFirst()
					.orElse(null);
			if (target != null) {
				throw new ProjectCompleteException("不能重复提交结项申请");
			}
		}
		int rows = this.getActualDao().insertSelective(projectComplete);
		if (rows <= 0) {
			throw new ProjectCompleteException("新增结项申请失败");
		}
	}
}