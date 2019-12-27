package com.dili.alm.controller;

import com.dili.alm.domain.MoveLogTable;
import com.dili.alm.service.MoveLogTableService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-27 16:48:24.
 */
@Api("/moveLogTable")
@Controller
@RequestMapping("/moveLogTable")
public class MoveLogTableController {
    @Autowired
    MoveLogTableService moveLogTableService;

    @ApiOperation("跳转到MoveLogTable页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "moveLogTable/index";
    }

    @ApiOperation(value="查询MoveLogTable", notes = "查询MoveLogTable，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="MoveLogTable", paramType="form", value = "MoveLogTable的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<MoveLogTable> list(@ModelAttribute MoveLogTable moveLogTable) {
        return moveLogTableService.list(moveLogTable);
    }

    @ApiOperation(value="分页查询MoveLogTable", notes = "分页查询MoveLogTable，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="MoveLogTable", paramType="form", value = "MoveLogTable的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(@ModelAttribute MoveLogTable moveLogTable) throws Exception {
        return moveLogTableService.listEasyuiPageByExample(moveLogTable, true).toString();
    }

    @ApiOperation("新增MoveLogTable")
    @ApiImplicitParams({
		@ApiImplicitParam(name="MoveLogTable", paramType="form", value = "MoveLogTable的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(@ModelAttribute MoveLogTable moveLogTable) {
        moveLogTableService.insertSelective(moveLogTable);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改MoveLogTable")
    @ApiImplicitParams({
		@ApiImplicitParam(name="MoveLogTable", paramType="form", value = "MoveLogTable的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(@ModelAttribute MoveLogTable moveLogTable) {
        moveLogTableService.updateSelective(moveLogTable);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除MoveLogTable")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "MoveLogTable的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        moveLogTableService.delete(id);
        return BaseOutput.success("删除成功");
    }
}