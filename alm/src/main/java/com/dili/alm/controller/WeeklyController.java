package com.dili.alm.controller;

import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.service.WeeklyService;

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
 * This file was generated on 2017-11-30 12:37:17.
 */
@Api("/weekly")
@Controller
@RequestMapping("/weekly")
public class WeeklyController  {
    @Autowired
    WeeklyService weeklyService;

    @ApiOperation("跳转到Weekly页面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "weekly/index";
    }

    @ApiOperation(value="查询Weekly", notes = "查询Weekly，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Weekly> list(Weekly weekly) {
        return weeklyService.list(weekly);
    }

    @ApiOperation(value="分页查询Weekly", notes = "分页查询Weekly，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(WeeklyPara weeklyPara) throws Exception {
    	
    	//weeklyService.listEasyuiPageByExample(weekly, true).toString();
        return weeklyService.getListPage(weeklyPara).toString();
    }

    @ApiOperation("新增Weekly")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Weekly weekly) {
        weeklyService.insertSelective(weekly);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改Weekly")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Weekly weekly) {
        weeklyService.updateSelective(weekly);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除Weekly")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Weekly的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        weeklyService.delete(id);
        return BaseOutput.success("删除成功");
    }
    
    
    @ApiOperation("跳转到getDescById页面")
    @RequestMapping(value="/getDescById", method = RequestMethod.GET)
    public String getDescById(ModelMap modelMap) {
        return "weekly/indexDesc";
    }
    
}