package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.Task;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TaskService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.github.pagehelper.Page;

@Api("/home")
@Controller
@RequestMapping("/home")
public class AlmHomeController {
	@Autowired
	private ProjectService projectService;
	
	@ApiOperation("跳转到首面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "home/index";
    }

    @ApiOperation("跳转到我的项目页面")
    @RequestMapping(value="/myProjectIndex", method = RequestMethod.GET)
    public String myProjectIndex(ModelMap modelMap) {
        return "home/myProjectIndex";
    }

    @ApiOperation(value="分页查询我的项目", notes = "分页查询我的项目，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="project", paramType="form", value = "project的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage() throws Exception {
    	return this.projectService.listPageMyProject().toString();
    }
}
