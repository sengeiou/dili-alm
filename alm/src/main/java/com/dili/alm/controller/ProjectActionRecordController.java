package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.ProjectAction;
import com.dili.alm.domain.dto.ProjectActionRecordGanttDto;
import com.dili.alm.service.ProjectActionRecordService;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-06-21 12:00:46.
 */
@Api("/projectActionRecord")
@Controller
@RequestMapping("/projectActionRecord")
public class ProjectActionRecordController {
	@Autowired
	ProjectActionRecordService projectActionRecordService;

	@ApiOperation("跳转到ProjectActionRecord页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "projectActionRecord/index";
	}

	@ResponseBody
	@RequestMapping("/gantt.json")
	public BaseOutput<Object> gantJson(@RequestParam Long projectId) {
		return BaseOutput.success().setData(this.projectActionRecordService.getGanntData(projectId));
	}

	@ResponseBody
	@RequestMapping("/actions.json")
	public List<Map<String, String>> actions() {
		List<Map<String, String>> list = new ArrayList<>(ProjectAction.values().length);
		for (ProjectAction action : ProjectAction.values()) {
			Map<String, String> map = new HashMap<>(2);
			map.put("code", action.getCode());
			map.put("name", action.getName());
			list.add(map);
		}
		return list;
	}

}