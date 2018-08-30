package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.dto.UserTaskDetailsQueryDto;
import com.dili.alm.service.TaskDetailsService;
import com.dili.alm.service.TaskService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:06.
 */
@Api("/taskDetails")
@Controller
@RequestMapping("/taskDetails")
public class TaskDetailsController {
	@Autowired
	TaskDetailsService taskDetailsService;
	@Autowired
	private TaskService taskService;

	@ApiOperation("跳转到TaskDetails页面")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(@RequestParam(required = false) Long userId, ModelMap modelMap) {
		Task taskQuery = DTOUtils.newDTO(Task.class);
		if (userId != null) {
			taskQuery.setOwner(userId);
		}
		List<Task> tasks = this.taskService.list(taskQuery);
		modelMap.addAttribute("userId", userId).addAttribute("tasks", tasks);
		return "taskDetails/index";
	}

	@ApiOperation(value = "查询TaskDetails", notes = "查询TaskDetails，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TaskDetails", paramType = "form", value = "TaskDetails的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<TaskDetails> list(TaskDetails taskDetails) {
		return taskDetailsService.list(taskDetails);
	}

	@ApiOperation(value = "分页查询TaskDetails", notes = "分页查询TaskDetails，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TaskDetails", paramType = "form", value = "TaskDetails的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@RequestParam(required = false) Long userId, UserTaskDetailsQueryDto query)
			throws Exception {
		UserTaskDetailsQueryDto userTaskDetailsQueryDto = DTOUtils.as(query, UserTaskDetailsQueryDto.class);
		if (userId != null) {
			Task taskQuery = DTOUtils.newDTO(Task.class);
			taskQuery.setOwner(userId);
			List<Task> taskList = this.taskService.list(taskQuery);
			List<Long> taskIds = new ArrayList<>(taskList.size());
			taskList.forEach(t -> taskIds.add(t.getId()));
			userTaskDetailsQueryDto.setTaskIds(taskIds);
		}
		if (query.getCreatedEndDate() != null) {
			// 日期格式为年与日所以查询条件要加一天
			long time = query.getCreatedEndDate().getTime();
			time += 24 * 60 * 60 * 1000;
			query.setCreatedEndDate(new Date(time));
		}
		if (query.getModifiedEndDate() != null) {
			// 日期格式为年与日所以查询条件要加一天
			long time = query.getModifiedEndDate().getTime();
			time += 24 * 60 * 60 * 1000;
			query.setModifiedEndDate(new Date(time));
		}
		return this.taskDetailsService.listEasyuiPageByExample(userTaskDetailsQueryDto, true).toString();
	}

	@ApiOperation("新增TaskDetails")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TaskDetails", paramType = "form", value = "TaskDetails的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(TaskDetails taskDetails) {
		taskDetailsService.insertSelective(taskDetails);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改TaskDetails")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TaskDetails", paramType = "form", value = "TaskDetails的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(TaskDetails taskDetails) {
		taskDetailsService.updateSelective(taskDetails);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除TaskDetails")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "TaskDetails的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		taskDetailsService.delete(id);
		return BaseOutput.success("删除成功");
	}
}