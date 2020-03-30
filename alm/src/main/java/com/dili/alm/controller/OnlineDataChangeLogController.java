package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.HardwareResource;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.OnlineDataChangeLog;
import com.dili.alm.domain.dto.HardwareResourceQueryDto;
import com.dili.alm.service.OnlineDataChangeLogService;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api("/onlineDataChangeLog")
@Controller
@RequestMapping("/onlineDataChangeLog")
public class OnlineDataChangeLogController {

	 @Autowired
	private    OnlineDataChangeLogService  onlineDataChangeLogService;
/*    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<OnlineDataChangeLog> list(@ModelAttribute OnlineDataChangeLog onlineDataChange) {
    	
        return onlineDataChangeLogService.list(onlineDataChange);
    }
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(@ModelAttribute OnlineDataChangeLog onlineDataChange) throws Exception {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
       return onlineDataChangeLogService.listEasyuiPageByExample(onlineDataChange, true).toString();
    }*/
    
    
    @ApiOperation(value = "查询OnlineDataChangeLog", notes = "查询OnlineDataChangeLog，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "OnlineDataChangeLog", paramType = "form", value = "OnlineDataChangeLog的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String list(OnlineDataChangeLog hardwareResource) throws Exception {
		return onlineDataChangeLogService.listEasyuiPageByExample(hardwareResource, true).toString();
	}

	@ApiOperation(value = "分页查询HardwareResource", notes = "分页查询OnlineDataChangeLog，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "OnlineDataChangeLog", paramType = "form", value = "OnlineDataChangeLog的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(OnlineDataChangeLog query) throws Exception {
		List<OnlineDataChangeLog> list = onlineDataChangeLogService.list(query);
		Map<Object, Object> metadata = null == query.getMetadata() ? new HashMap<>() : query.getMetadata();
		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("operatorId", memberProvider);
		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("operateTime", datetimeProvider);
		List onlineDataChangeList = ValueProviderUtils.buildDataByProvider(metadata, list);
		EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(Integer.valueOf(Integer.parseInt(String.valueOf(list.size()))), onlineDataChangeList);
		return taskEasyuiPageOutput.toString();
		//return onlineDataChangeLogService.listEasyuiPageByExample(query, true).toString();
	}

	
	
}
