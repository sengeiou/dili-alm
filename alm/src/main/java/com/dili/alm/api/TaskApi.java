package com.dili.alm.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dili.alm.service.TaskService;
import com.dili.ss.domain.BaseOutput;

@RestController
@RequestMapping("/taskApi")
public class TaskApi {

	@Autowired
	private TaskService taskService;

	// 测试未完成任务
	@RequestMapping(value = "/notComplate.api", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput notComplate() {
		taskService.notComplateTask();
		return BaseOutput.success("执行成功");
	}
}
