package com.dili.alm.controller;

import com.dili.alm.constant.AlmConstants.TaskStatus;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.alm.service.TaskDetailsService;
import com.dili.alm.service.TaskService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:05.
 */
@Api("/task")
@Controller
@RequestMapping("/task")
public class TaskController {
	@Autowired
	TaskService taskService;

	@Autowired
	ProjectService projectService;
	@Autowired
	ProjectApplyService projectApplyService;
	@Autowired
	ProjectVersionService projectVersionService;
	@Autowired
	ProjectPhaseService projectPhaseService;
	@Autowired
	TaskDetailsService taskDetailsService;

	@ApiOperation("跳转到Task页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "task/index";
	}
	

	@ApiOperation(value = "查询Task", notes = "查询Task，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody List<Task> list(Task task) {

		return taskService.list(task);
	}

	@ApiOperation(value = "分页查询Task", notes = "分页查询Task，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody String listPage(Task task) throws Exception {
		return taskService.listPageSelectTaskDto(task).toString();
	}

	@ApiOperation(value = "分页查询Task,首页显示", notes = "分页查询Task，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listTaskPageTab", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody String listTaskPageTab(Task task) throws Exception {
		UserTicket userTicket = SessionContext.getSessionContext()
				.getUserTicket();
		task.setOwner(userTicket.getId());//设置登录人员信息
		return taskService.listPageSelectTaskDto(task).toString();
	}

	@ApiOperation("新增Task")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody BaseOutput insert(Task task, String planTimeStr) {

		try {
			Short planTime = Short.parseShort(planTimeStr.trim());
			task.setPlanTime(planTime);
		} catch (Exception e) {
			e.printStackTrace();
			return BaseOutput.failure("请正确填写工时");
		}

		UserTicket userTicket = SessionContext.getSessionContext()
				.getUserTicket();
		task.setCreateMemberId(userTicket.getId());
		task.setCreated(new Date());
		task.setStatus(TaskStatus.NOTSTART.code);// 新增的初始化状态为0未开始状态
		taskService.insertSelective(task);

		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改Task")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody BaseOutput update(Task task, String planTimeStr) {

		try {
			
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			// 设置任务修改人为当前登录用户
			task.setModified(new Date());
	        task.setModifyMemberId(userTicket.getId());
			Short planTime = Short.parseShort(planTimeStr.trim());
			task.setPlanTime(planTime);
			
		} catch (Exception e) {
			
			return BaseOutput.failure("请正确填写工时");
		}
		taskService.updateSelective(task);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除Task")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "Task的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		taskService.delete(id);
		return BaseOutput.success("删除成功");
	}

	// 查询
	@ResponseBody
	@RequestMapping(value = "/listTree.json", method = { RequestMethod.GET,
			RequestMethod.POST })
	public List<Task> listTree(Task task) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setOwner(userTicket.getId());
		List<Task> list = this.taskService.list(task);
		return list;
	}
 

	// 查询项目
	@ResponseBody
	@RequestMapping(value = "/listTreeProject.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public List<Project> listProject() {
		Project project = DTOUtils.newDTO(Project.class);
		List<Project> list = projectService.list(project);
		return list;
	}

	// 查询版本
	@ResponseBody
	@RequestMapping(value = "/listTreeVersion.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public List<ProjectVersion> listVersion(Long id) {
		ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(id);
		List<ProjectVersion> list = projectVersionService.list(projectVersion);
		return list;
	}

	// 查询阶段
	@ResponseBody
	@RequestMapping(value = "/listTreePhase.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public List<ProjectPhase> listPhase(Long id) {
		ProjectPhase projectPhase = DTOUtils.newDTO(ProjectPhase.class);
		projectPhase.setVersionId(id);
		
		List<ProjectPhase> list = projectPhaseService.list(projectPhase);
		return list;
	}

	// 查询任务详细信息
	@ResponseBody
	@RequestMapping(value = "/listTaskDetail.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public TaskDetails listTaskDetail(Long id) {
		TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
		taskDetails.setTaskId(id);
		List<TaskDetails> list = taskDetailsService.list(taskDetails);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}
	
	//是否是任务所有人，传入任务ID
	@ResponseBody
	@RequestMapping(value = "/isOwner.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public boolean isOwner(Long id) {
		Task task = taskService.get(id);
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (task.getOwner()==userTicket.getId()) {
			return true;
		}
		return false;
	}
 

	// 更新任务信息
	@ApiOperation("填写任务工时")
	@ApiImplicitParams({ @ApiImplicitParam(name = "updateTaskDetails", paramType = "form", value = "TaskDetails的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/updateTaskDetails", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody BaseOutput updateTaskDetails(TaskDetails taskDetails,
			String planTimeStr, String overHourStr, String taskHourStr) {
		
		short taskHour = Optional.ofNullable(Short.parseShort(taskHourStr)).orElse((short)0);
		short overHour = Optional.ofNullable(Short.parseShort(overHourStr)).orElse((short)0);
		if (taskHour<=0) {
			return BaseOutput.failure("工时必须大于0");
		}
		if (taskService.isSetTask(taskDetails.getId(),taskHour)) {
			
			Task task = taskService.get(taskDetails.getTaskId());
			
	        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			/*基础信息设置*/
			task.setModifyMemberId(userTicket.getId());
			taskDetails.setTaskHour(taskHour);
			taskDetails.setOverHour(overHour);
			taskDetails.setCreateMemberId(userTicket.getId());
			/*基础信息设置*/
			taskService.updateTaskDetail(taskDetails,task);//保存任务
			
			return BaseOutput.success("修改成功");
		}
		
		return BaseOutput.failure("今日工时已填写超过8小时！");
		
	}


	// 开始执行任务
	@ApiOperation("开始执行任务")
	@ApiImplicitParams({ @ApiImplicitParam(name = "updateTaskStatus", paramType = "form", value = "TaskDetails的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/updateTaskStatus", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody BaseOutput updateTaskDetails(Long id) {
 		UserTicket userTicket = SessionContext.getSessionContext()
				.getUserTicket(); 
		Task task = taskService.get(id);
		task.setModifyMemberId(userTicket.getId());
		taskService.startTask(task);
		return BaseOutput.success("修改成功");
	}

	// 暂停任务
	@ApiOperation("暂停执行任务")
	@ApiImplicitParams({ @ApiImplicitParam(name = "pauseTaskStatus", paramType = "form", value = "TaskDetails的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/pauseTaskStatus", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody BaseOutput pauseTask(Long id) {
		UserTicket userTicket = SessionContext.getSessionContext()
				.getUserTicket();
		Task task = taskService.get(id);
		task.setStatus(TaskStatus.PAUSE.code);// 更新状态为暂停
		task.setModified(new Date());
		task.setModifyMemberId(userTicket.getId());
		taskService.update(task);
		return BaseOutput.success("已暂停任务");
	}
	

}