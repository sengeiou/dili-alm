package com.dili.sysadmin.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Resource;
import com.dili.sysadmin.domain.dto.ResourceDto;
import com.dili.sysadmin.service.ResourceService;
import com.dili.sysadmin.service.impl.ResourceException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Api("/resource")
@Controller
@RequestMapping("/resource")
public class ResourceController {
	@Autowired
	ResourceService resourceService;

	@ApiOperation("跳转到Resource页面")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "resource/index";
	}

	@ApiOperation(value = "查询Resource", notes = "查询Resource，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Resource", paramType = "form", value = "Resource的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Resource> list(@RequestParam Long menuId) {
		Resource query = new Resource();
		query.setMenuId(menuId);
		return resourceService.list(query);
	}

	@ApiOperation(value = "分页查询Resource", notes = "分页查询Resource，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Resource", paramType = "form", value = "Resource的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@ModelAttribute Resource resource) throws Exception {
		return resourceService.listEasyuiPageByExample(resource, true).toString();
	}

	@ApiOperation("新增Resource")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Resource", paramType = "form", value = "Resource的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(@Valid ResourceDto dto, BindingResult br) {
		if (br.hasErrors()) {
			BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		try {
			return resourceService.add(dto);
		} catch (ResourceException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("修改Resource")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Resource", paramType = "form", value = "Resource的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(@Valid ResourceDto dto, BindingResult br) {
		if (br.hasErrors()) {
			BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		return resourceService.update(dto);
	}

	@ApiOperation("删除Resource")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Resource的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		return resourceService.deleteWithOutput(id);
	}
}