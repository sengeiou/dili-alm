package com.dili.alm.controller;

import com.dili.alm.constant.AlmConstants.TaskStatus;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.User;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.alm.service.TaskDetailsService;
import com.dili.alm.service.TaskService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	public String index(@RequestParam(required = false) Long projectId, ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		if (projectId != null) {
			Project project = this.projectService.get(projectId);
			modelMap.put("project", project);
		}
		return "task/index";
	}

	@ApiOperation(value = "查询Task", notes = "查询Task，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Task> list(Task task) {

		return taskService.list(task);
	}
 
	@ApiOperation(value = "分页查询Task", notes = "按群组查询首页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(Task task) throws Exception {
		return taskService.listByTeam(task, null).toString();
	}
	
	@ApiOperation(value = "分页查询Task", notes = "按照群组进行分页查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listTeamPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listTeamPage(Task task, String phaseName) throws Exception {

		return taskService.listByTeam(task, phaseName).toString();
	}

	@ApiOperation(value = "分页查询Task,首页显示", notes = "分页查询Task，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listTaskPageTab", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listTaskPageTab(Task task) throws Exception {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setOwner(userTicket.getId());// 设置登录人员信息
		return taskService.listPageSelectTaskDto(task).toString();
	}

	@ApiOperation("新增Task")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(Task task, String planTimeStr,String startDateShow,String endDateShow) throws ParseException {

		//判断是否是本项目的项目经理
		if (!taskService.isThisProjectManger(task.getProjectId())) {
			return BaseOutput.failure("只有本项目的项目经理可以添加项目！");
		}
		DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
		try {
			Short planTime = Short.parseShort(planTimeStr.trim());
			task.setPlanTime(planTime);
			task.setStartDate(fmt.parse(startDateShow));
			task.setEndDate(fmt.parse(endDateShow));
		} catch (Exception e) {
			e.printStackTrace();
			return BaseOutput.failure("请正确填写工时");
		}

		if (taskService.validateBeginAndEnd(task)) {
			return BaseOutput.failure("开始时间和结束时间不能早于项目起始日期");
		}
		
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setCreateMemberId(userTicket.getId());
		task.setCreated(new Date());
		task.setStatus(TaskStatus.NOTSTART.code);// 新增的初始化状态为0未开始状态
		taskService.insertSelective(task);

		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改Task")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(Task task, String planTimeStr,String startDateShow,String endDateShow) {
		
		if (!taskService.isCreater(task)) {
			return BaseOutput.failure("不是本项目的创建者，不能进行编辑");
		}
		
		DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
		try {
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			// 设置任务修改人为当前登录用户
			task.setModified(new Date());
	        task.setModifyMemberId(userTicket.getId());
			Short planTime = Short.parseShort(planTimeStr.trim());
			task.setPlanTime(planTime);
			task.setStartDate(fmt.parse(startDateShow));
			task.setEndDate(fmt.parse(endDateShow));

		} catch (Exception e) {
			return BaseOutput.failure("请正确填写工时");
		}
		taskService.updateSelective(task);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除Task")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Task的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		
		Task task = taskService.get(id);
		
		if (!taskService.isCreater(task)) {
			return BaseOutput.failure("不是本项目的创建者，不能进行删除");
		}
		taskService.delete(id);
		return BaseOutput.success("删除成功");
	}

	// 查询前置任务
	@ResponseBody
	@RequestMapping(value = "/listTree.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Task> listTree(Long projectId) {
		return taskService.listTaskByProjectId(projectId);
	}
 
	// 查询项目
	@ResponseBody
	@RequestMapping(value = "/listTreeProjectChange.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<ProjectChange> listChangeProject(Long projectId) {
		List<ProjectChange> list = taskService.projectChangeList(projectId);
		return list;
	}
	
	//查询变更项目
	@ResponseBody
	@RequestMapping(value = "/listTreeProject.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Project> listProject() {
		List<Project> list = taskService.projectList();
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
	@RequestMapping(value = "/listTreeVersionByTeam.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public List<ProjectVersion> listTreeVersionByTeam() {
		List<ProjectVersion> list = taskService.listProjectVersionByTeam();
		return list;
	}
	
	

	
	// 查询阶段
	@ResponseBody
	@RequestMapping(value = "/listTreePhase.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Map> listPhase(Long id) {
		ProjectPhase projectPhase = DTOUtils.newDTO(ProjectPhase.class);
		projectPhase.setVersionId(id);
		
		List<Map> list = projectPhaseService.listEasyUiModels(projectPhase);
		return list;
	}
	
	// 根据项目查找用户
	@ResponseBody
	@RequestMapping(value = "/listTreeUserByProject.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public List<User> listTreeUserByProject(Long projectId) {
		List<User> list = taskService.listUserByProjectId(projectId);
		return list;
	}
	
	//查询用户列表
	@ResponseBody
	@RequestMapping(value = "/listTreeMember.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public List<User> listTreeMember() {
		return taskService.listUserByTeam();
	}

	// 查询任务详细信息
	@ResponseBody
	@RequestMapping(value = "/listTaskDetail.json", method = { RequestMethod.GET, RequestMethod.POST })
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
	@RequestMapping(value = "/isOwner.json", method = { RequestMethod.GET, RequestMethod.POST })
	public boolean isOwner(Long id) {
		Task task = taskService.get(id);
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (task.getOwner()==userTicket.getId()) {//判断是否是任务所有人
			return true;
		}
		if(taskService.isManager(task.getProjectId())){//判断是否是项目经理
			return true;
		}
		return false;
	}
	//判断是否是项目经理，用于显示按钮
	//是否是任务所有人，传入任务ID
	@ResponseBody
	@RequestMapping(value = "/isProjectManger.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public boolean isProjectManger() {
		return taskService.isProjectManager();
	}
	
	//判断是否是创建人，用户
	@ResponseBody
	@RequestMapping(value = "/isCreater.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public boolean isCreater(Long id) {
		Task task = taskService.get(id);
		return taskService.isCreater(task);
	}
	
	//剩余工时
	@ResponseBody
	@RequestMapping(value = "/restTaskHour.json", method = {
			RequestMethod.GET, RequestMethod.POST })
	public int restTaskHour() {
		int rest = taskService.restTaskHour();
		return rest;
	}

	// 更新任务信息
	@ApiOperation("填写任务工时")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "updateTaskDetails", paramType = "form", value = "TaskDetails的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/updateTaskDetails", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput updateTaskDetails(TaskDetails taskDetails, String planTimeStr, String overHourStr,
			String taskHourStr) {
		short taskHour = Optional.ofNullable(Short.parseShort(taskHourStr)).orElse((short) 0);
		short overHour = Optional.ofNullable(Short.parseShort(overHourStr)).orElse((short) 0);
		if (taskHour <= 0) {
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
	@ApiImplicitParams({
			@ApiImplicitParam(name = "updateTaskStatus", paramType = "form", value = "TaskDetails的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/updateTaskStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput updateTaskDetails(Long id) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Task task = taskService.get(id);
		task.setModifyMemberId(userTicket.getId());
		if (taskService.startTask(task)==0) {
			BaseOutput.failure("请勿重复添加数据");
		}
		return BaseOutput.success("修改成功");
	}

	// 暂停任务
	@ApiOperation("暂停执行任务")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pauseTaskStatus", paramType = "form", value = "TaskDetails的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/pauseTaskStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput pauseTask(Long id) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Task task = taskService.get(id);
		task.setStatus(TaskStatus.PAUSE.code);// 更新状态为暂停
		task.setModified(new Date());
		task.setModifyMemberId(userTicket.getId());
		taskService.update(task);
		return BaseOutput.success("已暂停任务");
	}
	
	
	

}
