package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.dili.alm.domain.HardwareResource;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.User;
import com.dili.alm.service.HardwareResourceService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-03-20 17:22:08.
 */
@Api("/hardwareResource")
@Controller
@RequestMapping("/hardwareResource")
public class HardwareResourceController {
    @Autowired
    HardwareResourceService hardwareResourceService;
    private static final String DATA_AUTH_TYPE = "Project";

    @ApiOperation("跳转到HardwareResource页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "hardwareResource/index";
    }

    @ApiOperation(value="查询HardwareResource", notes = "查询HardwareResource，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResource", paramType="form", value = "HardwareResource的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String list(HardwareResource hardwareResource)  throws Exception{
        return hardwareResourceService.listEasyuiPageByExample(hardwareResource,true).toString();
    }

    @ApiOperation(value="分页查询HardwareResource", notes = "分页查询HardwareResource，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResource", paramType="form", value = "HardwareResource的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(HardwareResource hardwareResource) throws Exception {
    	@SuppressWarnings({ "rawtypes" })
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
    	List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
        return hardwareResourceService.listEasyuiPageByExample(hardwareResource, projectIds, true).toString();
    }

    @ApiOperation("新增HardwareResource")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResource", paramType="form", value = "HardwareResource的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(HardwareResource hardwareResource) {
//    	  List<HardwareResource> parseArray = JSONArray.parseArray(hardwareResources,HardwareResource.class);

    	return hardwareResourceService.insertOneSelective(hardwareResource);
    }

    @ApiOperation("修改HardwareResource")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResource", paramType="form", value = "HardwareResource的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(HardwareResource hardwareResource) {
    	UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		hardwareResource.setMaintenanceOwner(userTicket.getId());
		hardwareResource.setLastModifyDate(new Date());
        hardwareResourceService.updateSelective(hardwareResource);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除HardwareResource")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "HardwareResource的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        hardwareResourceService.delete(id);
        return BaseOutput.success("删除成功");
    }
    // 查询用户列表
 	@ResponseBody
 	@RequestMapping(value = "/listTreeMember.json", method = { RequestMethod.GET, RequestMethod.POST })
 	public List<User> listTreeMember() {
 		return hardwareResourceService.listUserByDepartment();
 	}
 	
 	// 查询项目列表
  	@ResponseBody
  	@RequestMapping(value = "/listProjectList.json", method = { RequestMethod.GET, RequestMethod.POST })
  	public List<Project> listProjectList() {
  		return hardwareResourceService.projectList();
  	}
  	
  	// 查询项目列表
   	@ResponseBody
   	@RequestMapping(value = "/prjectNum.json", method = { RequestMethod.GET, RequestMethod.POST })
   	public Project prjectNum(String id) {
   		return hardwareResourceService.projectNumById(id);
   	}
   	
  	//判断是否提交
  	@ResponseBody
	@RequestMapping(value = "/isOperation.json", method = { RequestMethod.GET, RequestMethod.POST })
	public boolean isOperation(Long id) {
		return hardwareResourceService.isOperation(id);
	}
  	
  	@ApiOperation(value="查询HardwareResource", notes = "查询HardwareResource，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResource", paramType="form", value = "HardwareResource的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listByProjectId", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<HardwareResource> listByProjectId(Long projectId) {
  		HardwareResource hardwareResource=DTOUtils.newDTO(HardwareResource.class);
  		hardwareResource.setProjectId(projectId);
  		hardwareResource.setSort("last_modify_date");
  		hardwareResource.setOrder("DESC");
        return hardwareResourceService.list(hardwareResource);
    }
  	
  	@RequestMapping(value="/submit", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput submit(Long projectId) {
    	UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}

        hardwareResourceService.submit(projectId,userTicket.getId());
        return BaseOutput.success("提交成功");
    }
}