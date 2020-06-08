package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.EmailAddress;
import com.dili.alm.service.EmailAddressService;
import com.dili.ss.domain.BaseOutput;

/*import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;*/

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:29:09.
 */
/*@Api("/emailAddress")*/
@Controller
@RequestMapping("/emailAddress")
public class EmailAddressController {
	@Autowired
	EmailAddressService emailAddressService;

	/*@ApiOperation("跳转到EmailAddress页面")*/
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "emailAddress/index";
	}

/*	@ApiOperation(value = "查询EmailAddress", notes = "查询EmailAddress，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "EmailAddress", paramType = "form", value = "EmailAddress的form信息", required = false, dataType = "string") 
			})*/
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<EmailAddress> list(EmailAddress emailAddress) {
		return emailAddressService.list(emailAddress);
	}

/*	@ApiOperation(value = "分页查询EmailAddress", notes = "分页查询EmailAddress，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "EmailAddress", paramType = "form", value = "EmailAddress的form信息",
					required = false, dataType = "string") })*/
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(EmailAddress emailAddress) throws Exception {
		return emailAddressService.listEasyuiPageByExample(emailAddress, true).toString();
	}

/*	@ApiOperation("新增EmailAddress")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "EmailAddress", paramType = "form", value = "EmailAddress的form信息",
					required = true, dataType = "string") })*/
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(EmailAddress emailAddress) {
		emailAddressService.insertSelective(emailAddress);
		return BaseOutput.success("新增成功");
	}

/*	@ApiOperation("修改EmailAddress")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "EmailAddress", paramType = "form", value = "EmailAddress的form信息", required = true, 
					dataType = "string") })*/
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(EmailAddress emailAddress) {
		emailAddressService.updateSelective(emailAddress);
		return BaseOutput.success("修改成功");
	}

/*	@ApiOperation("删除EmailAddress")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "EmailAddress的主键", required = true, 
					dataType = "long") })*/
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		emailAddressService.delete(id);
		return BaseOutput.success("删除成功");
	}
}