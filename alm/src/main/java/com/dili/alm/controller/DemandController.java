package com.dili.alm.controller;

import com.dili.alm.domain.Demand;
import com.dili.uap.sdk.domain.Department;
import com.dili.alm.domain.Files;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.exceptions.DemandExceptions;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DemandService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.alm.service.WorkOrderService;
import com.dili.alm.service.impl.DemandServiceImpl;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:32:14.
 */
@Api("/demand")
@Controller
@RequestMapping("/demand")
public class DemandController {
	@Autowired
	private DepartmentRpc deptRpc;
	@Autowired
	private UserRpc userRpc;
    @Autowired
    DemandService demandService;
	private static final Logger LOGGER = LoggerFactory.getLogger(DemandController.class);
    @ApiOperation("跳转到Demand页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "demand/index";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap modelMap) {
		/** 查询 所有部门 ***/
    	Department department=DTOUtils.newDTO(Department.class);
		List<Department> departments = this.deptRpc.listByDepartment(department).getData();
		modelMap.addAttribute("departments", departments);

		/** 个人信息 **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		BaseOutput<List<Department>> de = deptRpc.findByUserId(userTicket.getId());
		modelMap.addAttribute("userInfo", userTicket);
		modelMap.addAttribute("depName", de.getData().get(0).getName());
		return "demand/add";
	}
	
	@RequestMapping(value = "/detail.html", method = RequestMethod.GET)
	public String detail(@RequestParam Long id,  ModelMap map) throws Exception {
		DemandDto detailViewData = this.demandService.getDetailViewData(id);
		try {
			map.addAttribute("model",DemandServiceImpl.parseViewModel(detailViewData));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "project/detail";
	}
	
	@ApiOperation(value = "查询Task", notes = "查询Task，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Demand> list(Demand task) {

		return demandService.list(task);
	}

    @ApiOperation(value="分页查询Demand", notes = "分页查询Demand，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Demand demand) throws Exception {
        return demandService.listEasyuiPageByExample(demand, true).toString();
    }

    @ApiOperation("新增Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Demand demand) {
        try {
			demandService.addNewDemand(demand);
		} catch (DemandExceptions e) {
			return BaseOutput.failure(e.getMessage());
		}
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("提交Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/submint", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput submint(Demand demand) {
        try {
			demandService.submint(demand);
		} catch (DemandExceptions e) {
			return BaseOutput.failure(e.getMessage());
		}
        return BaseOutput.success("提交成功");
    }
    @ApiOperation("修改Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Demand demand) {
        demandService.updateSelective(demand);
        return BaseOutput.success("修改成功");
    }
    
    @ApiOperation("删除Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Demand的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        demandService.delete(id);
        return BaseOutput.success("删除成功");
    }
    @ApiOperation("新增Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/sendseq.json", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput sendseq(Demand demand) {
		demandService.setDailyBegin();
        return BaseOutput.success("新增成功");
    }
    
    
    @ApiOperation(value="查询Demand未立项", notes = "查询Demand，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/queryDemandListToProject.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListToProject(Long projectId) {
        return demandService.queryDemandListToProject(projectId);
    }
    
    @ApiOperation(value="查询Demand根据id集合", notes = "查询Demand，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/queryDemandListByIds.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListByIds(String ids) {
    	if(WebUtil.strIsEmpty(ids)) {
    		return null;
    	}
        return demandService.queryDemandListByIds(ids);
    }
    @ApiOperation(value="查询已关联Demand", notes = "查询Demand，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/queryDemandListByApplyIdOrVersionIdOrWorkOrderId.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id,Integer type) {
    	if(id==null||type==null) {
    		return null;
    	}
        return demandService.queryDemandListByProjectIdOrVersionIdOrWorkOrderId(id,type);
    }
}