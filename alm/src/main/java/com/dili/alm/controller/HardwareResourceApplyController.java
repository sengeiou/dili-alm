package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-03-20 17:22:08.
 */
@Api("/hardwareResourceApply")
@Controller
@RequestMapping("/hardwareResourceApply")
public class HardwareResourceApplyController {
    @Autowired
    HardwareResourceApplyService hardwareResourceApplyService;

    @ApiOperation("跳转到HardwareResourceApply页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "hardwareResourceApply/index";
    }

    @ApiOperation(value="查询HardwareResourceApply", notes = "查询HardwareResourceApply，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResourceApply", paramType="form", value = "HardwareResourceApply的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<HardwareResourceApply> list(HardwareResourceApply hardwareResourceApply) {
        return hardwareResourceApplyService.list(hardwareResourceApply);
    }

    @ApiOperation(value="分页查询HardwareResourceApply", notes = "分页查询HardwareResourceApply，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResourceApply", paramType="form", value = "HardwareResourceApply的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(HardwareResourceApply hardwareResourceApply) throws Exception {
        return hardwareResourceApplyService.listEasyuiPageByExample(hardwareResourceApply, true).toString();
    }

    @ApiOperation("新增HardwareResourceApply")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResourceApply", paramType="form", value = "HardwareResourceApply的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(HardwareResourceApply hardwareResourceApply) {
        hardwareResourceApplyService.insertSelective(hardwareResourceApply);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改HardwareResourceApply")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResourceApply", paramType="form", value = "HardwareResourceApply的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(HardwareResourceApply hardwareResourceApply) {
        hardwareResourceApplyService.updateSelective(hardwareResourceApply);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除HardwareResourceApply")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "HardwareResourceApply的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        hardwareResourceApplyService.delete(id);
        return BaseOutput.success("删除成功");
    }
}