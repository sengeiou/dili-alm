package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.Area;
import com.dili.alm.service.AreaService;

/*import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;*/

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-04-27 09:27:25.
 */
/*@Api("/area")*/
@Controller
@RequestMapping("/area")
public class AreaController {
	@Autowired
	AreaService areaService;

	@RequestMapping(value = "/select")
	public String select(ModelMap modelMap) {
		return "area/select";
	}

/*	@ApiOperation(value = "查询Area", notes = "查询Area，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Area", paramType = "form", value = "Area的form信息", required = false, dataType = "string") })*/
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Area> list(Area area) {
		return areaService.list(area);
	}

}