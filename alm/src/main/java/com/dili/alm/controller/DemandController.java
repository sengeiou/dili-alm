package com.dili.alm.controller;

import com.dili.alm.domain.Demand;
import com.dili.alm.service.DemandService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    DemandService demandService;

    @ApiOperation("跳转到Demand页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "demand/index";
    }

    @ApiOperation(value="查询Demand", notes = "查询Demand，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> list(Demand demand) {
        return demandService.list(demand);
    }

    @ApiOperation(value="分页查询Demand", notes = "分页查询Demand，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Demand demand) throws Exception {
        return demandService.listEasyuiPageByExample(demand, true).toString();
    }

    @ApiOperation("新增Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Demand demand) {
        demandService.insertSelective(demand);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Demand demand) {
        demandService.updateSelective(demand);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Demand的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        demandService.delete(id);
        return BaseOutput.success("删除成功");
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
    @RequestMapping(value="/queryDemandListByProjectIdOrVersionIdOrWorkOrderId.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id,Integer type) {
    	if(id==null||type==null) {
    		return null;
    	}
        return demandService.queryDemandListByProjectIdOrVersionIdOrWorkOrderId(id,type);
    }
}