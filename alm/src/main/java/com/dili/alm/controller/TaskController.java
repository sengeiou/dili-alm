package com.dili.alm.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants.TaskStatus;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.TaskQueryInProjectId;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.TaskException;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.alm.service.TaskDetailsService;
import com.dili.alm.service.TaskService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;


import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:05.
 */

@Controller
@RequestMapping("/task")
public class TaskController {

	private static final String DATA_AUTH_TYPE = "project";

	@Autowired
	TaskService taskService;

	@Autowired
	ProjectService projectService;
	@Autowired
	ProjectApplyService projectApplyService;
	@Autowired
	ProjectVersionService projectVersionService;
	@Autowired
	TaskDetailsService taskDetailsService;


	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(@RequestParam(required = false) Long projectId, @RequestParam(required = false) String backUrl, ModelMap modelMap) throws UnsupportedEncodingException {
		modelMap.put("user", SessionContext.getSessionContext().getUserTicket());
		if (projectId != null) {
			Project project = this.projectService.get(projectId);
			modelMap.put("project", project);
		}
		if (StringUtils.isNotBlank(backUrl)) {
			modelMap.addAttribute("backUrl", URLDecoder.decode(backUrl, "UTF-8"));
		}
		return "task/index";
	}
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Task> list(Task task) {

		return taskService.list(task);
	}

	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(Task task) throws Exception {
		return taskService.listByTeam(task).toString();
	}

	@RequestMapping(value = "/listTeamPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listTeamPage(Task task) throws Exception {
		@SuppressWarnings({ "rawtypes" })
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			new EasyuiPageOutput(0L, Collections.emptyList()).toString();
		}
		List<Long> projectIds = new ArrayList<>();
		TaskQueryInProjectId query = DTOUtils.as(task, TaskQueryInProjectId.class);
		query.setInProjectIds(projectIds);
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
		query.setSort("created");
		query.setOrder("desc");
		EasyuiPageOutput listByTeam = taskService.listByTeam(query);
		return listByTeam.toString();
	}

	@RequestMapping(value = "/listTaskPageTab", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listTaskPageTab(Task task) throws Exception {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setOwner(userTicket.getId());// 设置登录人员信息
		task.setSort("created");
		task.setOrder("desc");
		return taskService.listByTeam(task).toString();
	}

	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(Task task, String planTimeStr, String startDateShow, String endDateShow, String flowSt) throws ParseException {

		Short planTime = Short.parseShort(planTimeStr.trim());
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			return BaseOutput.failure("用户登录超时！");
		}
		try {
			this.taskService.addTask(task, planTime, fmt.parse(startDateShow), fmt.parse(endDateShow), flowSt.equals("0") ? true : false, userTicket.getId());
		} catch (TaskException e) {
			return BaseOutput.failure(e.getMessage());
		}

		return BaseOutput.success("新增成功").setData(String.valueOf(task.getId() + ":" + task.getName()));
	}

	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(Task task, String planTimeStr, String startDateShow, String endDateShow, String flowSt) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			return BaseOutput.failure("用户登录超时！");
		}
		Short planTime = Short.parseShort(planTimeStr.trim());
		try {
			this.taskService.updateTask(task, userTicket.getId(), planTime, fmt.parse(startDateShow), fmt.parse(endDateShow), flowSt.equals("0") ? true : false);
		} catch (ParseException e) {
			return BaseOutput.failure("日期格式不正确");
		} catch (TaskException e) {
			return BaseOutput.failure(e.getMessage());
		}

		return BaseOutput.success("修改成功").setData(String.valueOf(task.getId() + ":" + task.getName()));
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		Task task = this.taskService.get(id);
		try {
			this.taskService.deleteTask(id);
		} catch (TaskException e) {
			return BaseOutput.failure(e.getMessage());
		}
		return BaseOutput.success("删除成功").setData(String.valueOf(task.getId() + ":" + task.getName()));
	}

	// 查询前置任务
	@ResponseBody
	@RequestMapping(value = "/listTree.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Task> listTree(Long projectId) {
		return taskService.listTaskByProjectId(projectId);
	}

	// 查询变更项目
	@ResponseBody
	@RequestMapping(value = "/listTreeProjectChange.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<ProjectChange> listChangeProject(Long projectId) {
		List<ProjectChange> list = taskService.projectChangeList(projectId);
		return list;
	}

	// 查询项目
	@ResponseBody
	@RequestMapping(value = "/listTreeProject.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Project> listProject(@RequestParam(required = false, defaultValue = "false") Boolean queryAll) {
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return new ArrayList<>(0);
		}
		List<Long> projectIds = new ArrayList<>(dataAuths.size());
		dataAuths.forEach(dataAuth -> projectIds.add(Long.valueOf(dataAuth.get("value").toString())));
		Example example = new Example(Project.class);
		Criteria criteria = example.createCriteria();
		if (!queryAll) {
			criteria.andNotEqualTo("projectState", ProjectState.CLOSED.getValue());
		}
		criteria.andIn("id", projectIds);
		List<Project> list = this.projectService.selectByExample(example);
		return list;
	}

	// 查询版本
	@ResponseBody
	@RequestMapping(value = "/listTreeVersion.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<ProjectVersion> listVersion(Long id) {
		ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(id);
		List<ProjectVersion> list = projectVersionService.list(projectVersion);
		return list;
	}

	// 根据登录用户查询版本信息
	@ResponseBody
	@RequestMapping(value = "/listTreeVersionByTeam.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<ProjectVersion> listTreeVersionByTeam() {
		List<ProjectVersion> list = taskService.listProjectVersionByTeam();
		return list;
	}

	// 根据项目查找用户
	@ResponseBody
	@RequestMapping(value = "/listTreeUserByProject.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<User> listTreeUserByProject(Long projectId) {
		List<User> list = taskService.listUserByProjectId(projectId);
		return list;
	}

	// 查询用户列表
	@ResponseBody
	@RequestMapping(value = "/listTreeMember.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<User> listTreeMember() {
		return taskService.listUserByTeam();
	}

	// 查询任务详细信息
	@ResponseBody
	@RequestMapping(value = "/listTaskDetail.json", method = { RequestMethod.GET, RequestMethod.POST })
	public TaskDetails listTaskDetail(Long id) {
		TaskDetails taskDetails = taskService.selectDetails(id);
		return taskDetails;
	}

	// 是否是任务所有人，传入任务ID
	@ResponseBody
	@RequestMapping(value = "/isOwner.json", method = { RequestMethod.GET, RequestMethod.POST })
	public boolean isOwner(Long id) {
		Task task = taskService.get(id);
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();

		if (task.getOwner().equals(userTicket.getId())) {// 判断是否是任务所有人
			return true;
		}
		return false;
	}

	// 判断是否是项目经理，用于显示按钮
	// 是否是任务所有人，传入任务ID
	@ResponseBody
	@RequestMapping(value = "/isProjectManger.json", method = { RequestMethod.GET, RequestMethod.POST })
	public boolean isProjectManger(@RequestParam Long projectId) {
		return taskService.isProjectManager(projectId);
	}

	@ResponseBody
	@RequestMapping("/isCurrentWeek.json")
	public boolean isCurrentWeek(@RequestParam Long taskId) {
		return taskService.isInCurrentWeek(taskId);

	}

	// 判断是否是创建人，用户
	@ResponseBody
	@RequestMapping(value = "/isCreater.json", method = { RequestMethod.GET, RequestMethod.POST })
	public boolean isCreater(Long id) {
		Task task = taskService.get(id);
		return taskService.isCreater(task);
	}

	// 更新任务信息
	@RequestMapping(value = "/updateTaskDetails", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput updateTaskDetails(TaskDetails taskDetails, String planTimeStr, String overHourStr, String taskHourStr) {

		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();

		if (userTicket == null) {
			return BaseOutput.failure("用户登录超时！");
		}
		short taskHour = 0;
		short overHour = 0;
		/* 2018-1-8 优化:工时，加班工时可任意填写一个 */
		try {
			taskHour = WebUtil.strIsEmpty(taskHourStr) ? 0 : Short.parseShort(taskHourStr.trim());
		} catch (Exception e) {
			return BaseOutput.failure("工时填写有误！");
		}
		try {
			overHour = WebUtil.strIsEmpty(overHourStr) ? 0 : Short.parseShort(overHourStr.trim());
		} catch (Exception e) {
			return BaseOutput.failure("加班工时填写有误！");
		}
		if (taskHour == 0 && overHour == 0) {
			return BaseOutput.failure("任务工时或者加班工时填必须填写其中一个！");
		}
		Date taskDate = taskDetails.getModified() == null ? new Date() : taskDetails.getModified();
		try {
			this.taskService.submitWorkingHours(taskDetails.getTaskId(), userTicket.getId(), taskDate, taskHour, overHour, taskDetails.getDescribe());
			Task task = this.taskService.get(taskDetails.getTaskId());
			return BaseOutput.success().setData(task.getName());
		} catch (TaskException e) {
			return BaseOutput.failure(e.getMessage());
		}

	}

	// 开始执行任务
	@RequestMapping(value = "/updateTaskStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> updateTaskDetails(Long id) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			return BaseOutput.failure("用户登录超时！");
		}
		try {
			taskService.startTask(id, userTicket.getId());
			Task task = taskService.get(id);
			return BaseOutput.success("执行任务成功").setData(String.valueOf(task.getId() + ":" + task.getName()));
		} catch (ApplicationException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	// 暂停任务
	@RequestMapping(value = "/pauseTaskStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput pauseTask(Long id) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			return BaseOutput.failure("用户登录超时！");
		}
		Task task = taskService.get(id);
		task.setStatus(TaskStatus.PAUSE.code);// 更新状态为暂停
		task.setModified(new Date());
		task.setModifyMemberId(userTicket.getId());
		taskService.update(task);
		return BaseOutput.success("暂停任务成功").setData(String.valueOf(task.getId() + ":" + task.getName()));
	}

	// 完成任务
	@RequestMapping(value = "/complateTask", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> complateTask(Long id) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			return BaseOutput.failure("用户登录超时！");
		}
		try {
			this.taskService.completeTask(id, userTicket.getId());
			Task task = this.taskService.get(id);
			return BaseOutput.success("更新状态为完成").setData(String.valueOf(task.getId() + ":" + task.getName()));
		} catch (TaskException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	// 是否已经执行过,一次加班工时，或一次任务工时
	@ResponseBody
	@RequestMapping(value = "/isTask.json", method = { RequestMethod.GET, RequestMethod.POST })
	public boolean isTask(Long id) {
		TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
		taskDetails.setTaskId(id);
		List<TaskDetails> list = taskDetailsService.list(taskDetails);

		int taskHour = 0;
		int overHour = 0;

		for (TaskDetails entity : list) {
			taskHour += entity.getTaskHour();
			overHour += entity.getOverHour();
		}
		/* 2018-1-8 优化:工时，加班工时任意填写其中一项，即可更新状态为完成 */
		if (taskHour > 0 || overHour > 0) {
			return true;
		}
		return false;
	}

	// 判断是否是项时间
	@ResponseBody
	@RequestMapping(value = "/isProjectDate.json", method = { RequestMethod.GET, RequestMethod.POST })
	public boolean isProjectDate(Long projectId, String startDateShow, String endDateShow) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = fmt.parse(startDateShow);
			endDate = fmt.parse(endDateShow);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return taskService.validateBeginAndEnd(projectId, startDate, endDate);

	}

	@RequestMapping(value = "/listTaskDetails", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map> list(TaskDetails taskDetails) {
		Map<Object, Object> metadata = new HashMap<>();

		List<TaskDetails> list = taskDetailsService.list(taskDetails);

		JSONObject dateProvider = new JSONObject();
		dateProvider.put("provider", "dateProvider");
		metadata.put("created", dateProvider);
		metadata.put("modified", dateProvider);

		try {
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/task/{id}", method = RequestMethod.GET)
	public String task(@PathVariable("id") Long id, ModelMap modelMap) throws UnsupportedEncodingException {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		modelMap.put("taskId", id);
		return "task/index";
	}
	@ResponseBody
	@RequestMapping(value = "/listTreeVersionByProject.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<ProjectVersion> listTreeVersionByProject(Long id) {
		ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(id);
		List<ProjectVersion> list=new ArrayList<>();
		if(id!=null)
		      list = projectVersionService.list(projectVersion);
		return list;
	}

}
