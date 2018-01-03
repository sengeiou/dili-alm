package com.dili.alm.controller;

import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.DataDictionaryValueTreeView;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.DataDictionaryValueService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-08-28 16:44:23.
 */
@Api("/dataDictionaryValue")
@Controller
@RequestMapping("/dataDictionaryValue")
public class DataDictionaryValueController {
	@Autowired
	DataDictionaryValueService dataDictionaryValueService;
	@Autowired
	private DataDictionaryService dataDictionaryService;

	@ApiOperation("跳转到DataDictionaryValue页面")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "dataDictionaryValue/index";
	}

	@ApiOperation(value = "查询DataDictionaryValue", notes = "查询DataDictionaryValue，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DataDictionaryValue", paramType = "form", value = "DataDictionaryValue的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String list(DataDictionaryValue dataDictionaryValue, ModelMap map) {
		map.addAttribute("ddId", dataDictionaryValue.getDdId());
		return "dataDictionaryValue/list";
	}

	@ResponseBody
	@RequestMapping(value = "/listTree.json", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<DataDictionaryValueTreeView> listTree(@RequestParam Long ddId) {
		List<DataDictionaryValueTreeView> list = this.dataDictionaryValueService.listTree(ddId);
		return list;
	}

	@ResponseBody
	@RequestMapping(value = "/listByCode.json", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<DataDictionaryValueDto> listByCode(@RequestParam String code) {
		DataDictionaryDto dto = dataDictionaryService.findByCode(code);
		List<DataDictionaryValueDto> list = dto== null ? null : dto.getValues();
		return list;
	}

	@ApiOperation(value = "分页查询DataDictionaryValue", notes = "分页查询DataDictionaryValue，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DataDictionaryValue", paramType = "form", value = "DataDictionaryValue的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(DataDictionaryValue dataDictionaryValue) throws Exception {
		if(null != dataDictionaryValue.getParentId() && dataDictionaryValue.getParentId().equals(-1L)){
			dataDictionaryValue.setParentId(null);
		}
		return dataDictionaryValueService.listEasyuiPageByExample(dataDictionaryValue, true).toString();
	}

	@ApiOperation("新增DataDictionaryValue")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DataDictionaryValue", paramType = "form", value = "DataDictionaryValue的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(DataDictionaryValue dataDictionaryValue) {
		dataDictionaryValueService.insertSelective(dataDictionaryValue);
		return BaseOutput.success("新增成功").setData(dataDictionaryValue);
	}

	@ApiOperation("修改DataDictionaryValue")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DataDictionaryValue", paramType = "form", value = "DataDictionaryValue的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(DataDictionaryValue dataDictionaryValue) {
		dataDictionaryValueService.updateSelective(dataDictionaryValue);
		return BaseOutput.success("修改成功").setData(dataDictionaryValue);
	}

	@ApiOperation("删除DataDictionaryValue")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "DataDictionaryValue的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		dataDictionaryValueService.delete(id);
		return BaseOutput.success("删除成功");
	}
}