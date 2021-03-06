package com.dili.alm.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.AlarmConfig;
import com.dili.alm.domain.Project;
import com.dili.alm.provider.ProjectProvider;
import com.dili.alm.service.AlarmConfigService;
import com.dili.alm.service.ProjectService;
import com.dili.ss.domain.BaseOutput;

/*import io.swagger.annotations.Api;*/

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-09 15:41:26.
 */
/*@Api("/alarmConfig")*/
@Controller
@RequestMapping("/alarmConfig")
public class AlarmConfigController {
	@Autowired
	AlarmConfigService alarmConfigService;
	@Autowired
	private ProjectService projectService;

	@ResponseBody
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public BaseOutput<Object> saveOrUpdate(AlarmConfig alarmConfig) {
		return this.alarmConfigService.saveOrUpdateWithOutput(alarmConfig);
	}

	@RequestMapping(value = "/config.html", method = RequestMethod.GET)
	public String config(@RequestParam Long projectId, ModelMap map) {
		Project project = this.projectService.get(projectId);
		try {
			map.addAttribute("project", ProjectProvider.parseEasyUiModel(project));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "alarm/config";
	}

}