package com.dili.alm.controller;

import com.alibaba.fastjson.JSON;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.exceptions.ApproveException;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.service.ApproveService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.session.SessionContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-04 17:39:37.
 */
@Api("/approve")
@Controller
@RequestMapping("/approve")
public class ApproveController {
	@Autowired
	ApproveService approveService;

	@Autowired
	private RoleRpc roleRpc;

	@ApiOperation("跳转到Approve页面")
	@RequestMapping(value = "/apply/index.html", method = RequestMethod.GET)
	public String applyIndex(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "approveApply/index";
	}

	@ApiOperation("跳转到Approve页面")
	@RequestMapping(value = "/change/index.html", method = RequestMethod.GET)
	public String changeIndex(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "approveChange/index";
	}

	@ApiOperation("跳转到Approve页面")
	@RequestMapping(value = "/complete/index.html", method = RequestMethod.GET)
	public String completeIndex(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "approveComplete/index";
	}

	@RequestMapping(value = "/apply/{id}", method = RequestMethod.GET)
	public String apply(ModelMap modelMap, @PathVariable("id") Long id, String viewMode) {

		approveService.buildApplyApprove(modelMap, id);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveApply/approve";
	}

	@RequestMapping(value = "/change/{id}", method = RequestMethod.GET)
	public String change(ModelMap modelMap, @PathVariable("id") Long id, String viewMode) {

		approveService.buildChangeApprove(modelMap, id);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveChange/approve";
	}

	@RequestMapping(value = "/verify/{id}", method = RequestMethod.GET)
	public String verify(ModelMap modelMap, @PathVariable("id") Long id, String viewMode) {

		approveService.buildChangeApprove(modelMap, id);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveChange/verify";
	}

	@RequestMapping(value = "/complete/{id}", method = RequestMethod.GET)
	public String complete(ModelMap modelMap, @PathVariable("id") Long id, String viewMode) {

		approveService.buildCompleteApprove(modelMap, id);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveComplete/approve";
	}

	@RequestMapping("/loadDesc")
	@ResponseBody
	public List<Map> loadDesc(Long id) throws Exception {
		Approve approve = approveService.get(id);
		Map<Object, Object> metadata = new HashMap<>(2);
		metadata.put("userId", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("approveDate", JSON.parse("{provider:'datetimeProvider'}"));
		List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata,
				JSON.parseArray(approve.getDescription(), ApplyApprove.class));
		maps.forEach(map -> {
			Long userId = Long.valueOf(map.get("$_userId").toString());
			map.put("role", roleRpc.listRoleNameByUserId(userId).getData());
		});
		return maps;
	}

	@RequestMapping("/applyApprove")
	@ResponseBody
	public BaseOutput<Object> applyApprove(Long id, String opt, String notes) {
		try {
			approveService.applyApprove(id, opt, notes);
			return BaseOutput.success();
		} catch (ApproveException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping("/verityApprove")
	@ResponseBody
	public BaseOutput verityApprove(Long id, String opt, String notes) {
		return approveService.verity(id, opt, notes);
	}

	@ApiOperation(value = "查询Approve", notes = "查询Approve，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Approve", paramType = "form", value = "Approve的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Approve> list(Approve approve) {
		return approveService.list(approve);
	}

	@ApiOperation(value = "分页查询Approve", notes = "分页查询Approve，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Approve", paramType = "form", value = "Approve的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/apply/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(Approve approve) throws Exception {
		approve.setType(AlmConstants.ApproveType.APPLY.getCode());
		if (approve.getCreated() != null) {
			Date temp = approve.getCreated();
			approve.setCreated(DateUtil.appendDateToEnd(approve.getCreated()));
			approve.setCreatedStart(DateUtil.appendDateToStart(temp));
		}
		return approveService.listEasyuiPageByExample(approve, true).toString();
	}

	@RequestMapping(value = "/change/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String changeListPage(Approve approve) throws Exception {
		approve.setType(AlmConstants.ApproveType.CHANGE.getCode());
		if (approve.getCreated() != null) {
			Date temp = approve.getCreated();
			approve.setCreated(DateUtil.appendDateToEnd(approve.getCreated()));
			approve.setCreatedStart(DateUtil.appendDateToStart(temp));
		}
		return approveService.listEasyuiPageByExample(approve, true).toString();
	}

	@RequestMapping(value = "/complete/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String completeListPage(Approve approve) throws Exception {
		approve.setType(AlmConstants.ApproveType.COMPLETE.getCode());
		if (approve.getCreated() != null) {
			Date temp = approve.getCreated();
			approve.setCreated(DateUtil.appendDateToEnd(approve.getCreated()));
			approve.setCreatedStart(DateUtil.appendDateToStart(temp));
		}
		return approveService.listEasyuiPageByExample(approve, true).toString();
	}

	@ApiOperation("新增Approve")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Approve", paramType = "form", value = "Approve的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(Approve approve) {
		approveService.insertSelective(approve);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改Approve")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Approve", paramType = "form", value = "Approve的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(Approve approve) {
		approveService.updateSelective(approve);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除Approve")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Approve的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		approveService.delete(id);
		return BaseOutput.success("删除成功");
	}

	@RequestMapping(value = "/doc/apply/{id}", method = RequestMethod.GET)
	public void applyDoc(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {

		String fileName = "立项申请文档.docx";
		// 默认使用IE的方式进行编码
		try {
			String userAgent = request.getHeader("User-Agent");
			String rtn = "filename=\"" + fileName + "\"";
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				// IE浏览器，只能采用URLEncoder编码
				if (userAgent.contains("msie")) {
					rtn = "filename=\"" + fileName + "\"";
				}
				// Opera浏览器只能采用filename*
				else if (userAgent.contains("opera")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
				// Safari浏览器，只能采用ISO编码的中文输出
				else if (userAgent.contains("safari")) {
					rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
				} else if (userAgent.contains("mozilla")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" + rtn);
			approveService.downloadProjectDoc(AlmConstants.ApproveType.APPLY, id, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/doc/change/{id}", method = RequestMethod.GET)
	public void changeDoc(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {

		String fileName = "变更申请文档.docx";
		// 默认使用IE的方式进行编码
		try {
			String userAgent = request.getHeader("User-Agent");
			String rtn = "filename=\"" + fileName + "\"";
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				// IE浏览器，只能采用URLEncoder编码
				if (userAgent.contains("msie")) {
					rtn = "filename=\"" + fileName + "\"";
				}
				// Opera浏览器只能采用filename*
				else if (userAgent.contains("opera")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
				// Safari浏览器，只能采用ISO编码的中文输出
				else if (userAgent.contains("safari")) {
					rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
				} else if (userAgent.contains("mozilla")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" + rtn);
			approveService.downloadProjectDoc(AlmConstants.ApproveType.CHANGE, id, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/doc/complete/{id}", method = RequestMethod.GET)
	public void completeDoc(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {

		String fileName = "结项申请文档.docx";
		// 默认使用IE的方式进行编码
		try {
			String userAgent = request.getHeader("User-Agent");
			String rtn = "filename=\"" + fileName + "\"";
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				// IE浏览器，只能采用URLEncoder编码
				if (userAgent.contains("msie")) {
					rtn = "filename=\"" + fileName + "\"";
				}
				// Opera浏览器只能采用filename*
				else if (userAgent.contains("opera")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
				// Safari浏览器，只能采用ISO编码的中文输出
				else if (userAgent.contains("safari")) {
					rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
				} else if (userAgent.contains("mozilla")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" + rtn);
			approveService.downloadProjectDoc(AlmConstants.ApproveType.COMPLETE, id, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}