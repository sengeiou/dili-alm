package com.dili.alm.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.rpc.MyTasksRpc;
import com.dili.alm.rpc.UapUserRpc;
import com.dili.alm.service.DataMigrateService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.uap.sdk.domain.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 */
@Api("/move")
@Controller
@RequestMapping("/move")
public class DataMigrateController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataMigrateController.class);
	
	@Autowired
	MyTasksRpc myTasksRpc;

	@Autowired
	private UapUserRpc userRpc;
	
	@Autowired
	private  DataMigrateService moveService;
	
	@ApiOperation("跳转到drafts页面")
	@RequestMapping(value = "/drafts", method = RequestMethod.GET)
	public String projectOverview(ModelMap modelMap) {
		return "move/drafts";
	}

	@ApiOperation("跳转到submitted页面")
	@RequestMapping(value = "/submitted", method = RequestMethod.GET)
	public String projectProgress(ModelMap modelMap) {
		return "move/submitted";
	}

	@ApiOperation("跳转到pendingDisposal页面")
	@RequestMapping(value = "/pendingDisposal", method = RequestMethod.GET)
	public String projectType(ModelMap modelMap) {
		return "move/pendingDisposal";
	}

	@ApiOperation("跳转到processed页面")
	@RequestMapping(value = "/processed", method = RequestMethod.GET)
	public String taskHoursForUser(ModelMap modelMap) {
		return "move/processed";
	}
	@ApiOperation(value = "刪除用戶", notes = "刪除用戶")
	@RequestMapping(value = "/deleteDrafts", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput deleteDraftsCancel( Long id) {
		return BaseOutput.success("修改成功");
	}
	@ApiOperation(value = "uap用戶遷移", notes = "查询uap用戶返回列表信息")
	@RequestMapping(value = "/updateData", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput updateData( Long id) {
		
		//数据更新，用户相关表
		int  num=moveService.updateData(null,id);
		if(num==1)
			return BaseOutput.success("已经迁移");
		return BaseOutput.success("修改成功");
	}
	@ApiOperation(value = "查询uap用戶", notes = "查询uap用戶返回列表信息")
	@RequestMapping(value = "/myMoveList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String  myTasksList( User user) {
		PageOutput<List<User>> userOutput =userRpc.listByExample(user);
    	EasyuiPageOutput  out= new EasyuiPageOutput(userOutput.getTotal(), userOutput.getData());
	   return out.toString();

	}
	@ApiOperation(value = "替換用戶表--", notes = "替換用戶表--")
	@RequestMapping(value = "/updateUserStrData", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput updateUserStrData() {
		
		//数据更新，用户相关表
		int  num=moveService.updateUserIdStrData();
		if(num==1)
			return BaseOutput.success("執行成功");
		return BaseOutput.success("修改成功");
	}
	@ApiOperation("跳转到pendingDisposal页面")
	@RequestMapping(value = "/dataChange", method = RequestMethod.GET)
	public String dataChange(ModelMap modelMap) {
		return "move/drafts";
	}



/*	@RequestMapping(value = "/getUapUserList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput updates( Long id) {
		return BaseOutput.success("修改成功");
	}

*/
	







}
