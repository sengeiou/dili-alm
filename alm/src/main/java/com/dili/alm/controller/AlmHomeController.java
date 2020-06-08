package com.dili.alm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.Project;
import com.dili.alm.service.ProjectService;
import com.dili.uap.sdk.session.SessionContext;

/*import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;*/

/*@Api("/home")*/
@Controller
@RequestMapping("/home")
public class AlmHomeController {
	@Autowired
	private ProjectService projectService;

	/*@ApiOperation("跳转到首面")*/
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "home/index";
	}

	/*@ApiOperation("跳转到我的项目页面")*/
	@RequestMapping(value = "/myProjectIndex", method = RequestMethod.GET)
	public String myProjectIndex(ModelMap modelMap) {
		return "home/myProjectIndex";
	}

	/*@ApiOperation("跳转到我的任务")*/
	@RequestMapping(value = "/taskList", method = RequestMethod.GET)
	public String taskList(ModelMap modelMap) {
		modelMap.addAttribute("user", SessionContext.getSessionContext().getUserTicket());
		return "home/taskList";
	}

/*	@ApiOperation(value = "分页查询我的项目", notes = "分页查询我的项目，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "project", paramType = "form", value = "project的form信息", required = false, dataType = "string") })*/
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(Project project) throws Exception {
		return this.projectService.listPageMyProject(project).toString();
	}
}
