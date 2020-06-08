package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.dto.UserWorkHourDetailDto;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.service.TaskDetailsService;
import com.dili.alm.service.TaskService;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.github.pagehelper.Page;


/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:06.
 */

@Controller
@RequestMapping("/taskDetails")
public class TaskDetailsController {
	@Autowired
	TaskDetailsService taskDetailsService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private StatisticalService statisticalService;


	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(@RequestParam Long userId, @RequestParam Long totalHour, ModelMap modelMap) {
		Task taskQuery = DTOUtils.newDTO(Task.class);
		taskQuery.setOwner(userId);
		List<Task> tasks = this.taskService.list(taskQuery);
		modelMap.addAttribute("user", AlmCache.getInstance().getUserMap().get(userId)).addAttribute("tasks", tasks)
				.addAttribute("totalHour", this.statisticalService.getUserTotalTaskHour(userId));
		return "taskDetails/index";
	}


	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<TaskDetails> list(TaskDetails taskDetails) {
		return taskDetailsService.list(taskDetails);
	}

	
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@RequestParam Long userId, BaseDomain query) throws Exception {
		Page<UserWorkHourDetailDto> page = this.taskDetailsService.listUserWorkHourDetail(userId, query);
		return new EasyuiPageOutput(Long.valueOf(page.getTotal()).intValue(), page.getResult()).toString();
	}

	
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(TaskDetails taskDetails) {
		taskDetailsService.insertSelective(taskDetails);
		return BaseOutput.success("新增成功");
	}

	
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(TaskDetails taskDetails) {
		taskDetailsService.updateSelective(taskDetails);
		return BaseOutput.success("修改成功");
	}

	
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		taskDetailsService.delete(id);
		return BaseOutput.success("删除成功");
	}
}