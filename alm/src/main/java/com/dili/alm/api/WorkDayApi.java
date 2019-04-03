package com.dili.alm.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.domain.dto.WorkDayRoleDto;
import com.dili.alm.service.WorkDayService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

@Api("/workDayApi")
@Controller
@RequestMapping("/workDayApi")
public class WorkDayApi {
	@Autowired
	private WorkDayService workDayService;

	/**
	 * 上传ecxel解析导入mysql
	 * 
	 * @param file
	 * @param year
	 * @return
	 */
	@ApiImplicitParams({
			@ApiImplicitParam(name = "WorkDay", paramType = "form", value = "WorkDay的form信息", required = true, dataType = "string") })
	@CrossOrigin(origins = { "http://alm.diligrp.com", "null" })
	@RequestMapping(value = "/uploadWordDayDate", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput uploadWordDayDate(@RequestParam("file") MultipartFile file, String year) {
		return workDayService.upload(file, year);
	}

	/**
	 * 查询导入的所有年份
	 * 
	 * @return
	 */
	@ApiImplicitParams({
			@ApiImplicitParam(name = "WorkDay", paramType = "form", value = "WorkDay的form信息", required = true, dataType = "string") })
	@CrossOrigin(origins = { "http://alm.diligrp.com", "null" })
	@RequestMapping(value = "/selectWorkDayYear", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<String> selectWorkDayYear() {
		return workDayService.getWorkDayYaers();
	}

	/**
	 * 查询当前工作时间
	 * 
	 * @return
	 */
	@ApiImplicitParams({
			@ApiImplicitParam(name = "WorkDay", paramType = "form", value = "WorkDay的form信息", required = true, dataType = "string") })
	@CrossOrigin(origins = { "http://alm.diligrp.com", "null" })
	@RequestMapping(value = "/getWorkDay", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<WorkDayRoleDto> getWorkDay(String userId) {
		if (WebUtil.strIsEmpty(userId)) {
			throw new RuntimeException("未登录");
		}
		BaseOutput<WorkDayRoleDto> showWorkDay = workDayService.showWorkDay(Long.parseLong(userId));
		if ("false".equals(showWorkDay.getCode())) {
			return BaseOutput.failure("需要导入新的工作日").setData(showWorkDay.getData());
		} else {
			return BaseOutput.success().setData(showWorkDay.getData());
		}

	}

	/**
	 * 查询当前年份
	 * 
	 * @return
	 */
	@ApiImplicitParams({
			@ApiImplicitParam(name = "WorkDay", paramType = "form", value = "WorkDay的form信息", required = true, dataType = "string") })
	@CrossOrigin(origins = { "http://alm.diligrp.com", "null" })
	@RequestMapping(value = "/isHasWorkDayYear", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody boolean isHasWorkDayYear(String year) {
		List<String> workDayYaers = workDayService.getWorkDayYaers();
		return workDayYaers.contains(year);
	}
}
