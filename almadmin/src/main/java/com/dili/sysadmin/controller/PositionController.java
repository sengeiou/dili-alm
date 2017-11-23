package com.dili.sysadmin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Position;
import com.dili.sysadmin.domain.dto.DataDictionaryValueDto;
import com.dili.sysadmin.service.PositionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-21 16:14:32.
 */
@Api("/position")
@Controller
@RequestMapping("/position")
public class PositionController {
	@Autowired
	PositionService positionService;

	@ApiOperation("跳转到Position页面")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "position/index";
	}

	@ApiOperation(value = "查询Position", notes = "查询Position，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Position", paramType = "form", value = "Position的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Position> list(@ModelAttribute Position position) {
		return positionService.list(position);
	}

	@ApiOperation(value = "分页查询Position", notes = "分页查询Position，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Position", paramType = "form", value = "Position的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@ModelAttribute Position position) throws Exception {
		return positionService.listEasyuiPageByExample(position, true).toString();
	}

	@ApiOperation("新增Position")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Position", paramType = "form", value = "Position的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(Position position) {
		positionService.insertSelective(position);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改Position")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Position", paramType = "form", value = "Position的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(@ModelAttribute Position position) {
		positionService.updateSelective(position);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除Position")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "Position的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		positionService.delete(id);
		return BaseOutput.success("删除成功");
	}

	@ResponseBody
	@RequestMapping(value = "/rank.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<DataDictionaryValueDto> rankJson() {
		return this.positionService.listRank();
	}
}