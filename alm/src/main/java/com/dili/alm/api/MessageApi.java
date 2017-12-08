package com.dili.alm.api;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.Message;
import com.dili.alm.service.MessageService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;

@Api("/messageApi")
@Controller
@RequestMapping("/messageApi")
public class MessageApi {
	@Autowired
	private MessageService messageService;
	
	@ApiOperation("新增message")
	@ApiImplicitParams({
	@ApiImplicitParam(name="Message", paramType="form", value = "Message的form信息", required = true, dataType = "string")
	})
	@RequestMapping(value="/saveMessage", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody BaseOutput insert(String messageUrl,Long sender,Long recipient,Integer type) {
		if(WebUtil.strIsEmpty(messageUrl)|| sender==null|| recipient==null|| type==null){
	        return BaseOutput.failure("新增失败,包含空值messageUrl="+messageUrl.toString()+",sender="+sender+",recipient="+recipient+",type="+type);
        }else{
        	messageService.insertMessage(messageUrl,sender,recipient,type);
		    return BaseOutput.success("新增成功");
        }
		
	}
	
	@ApiOperation("修改message的状态")
	@ApiImplicitParams({
	@ApiImplicitParam(name="Message", paramType="form", value = "Message的form信息", required = true, dataType = "string")
	})
	@CrossOrigin(origins = {"http://almadmin.diligrp.com", "null"})
	@RequestMapping(value="/updateMessageIsRead", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody BaseOutput update(String id) {
		if(WebUtil.strIsEmpty(id)){
	        return BaseOutput.failure("修改状态失败,包含空值id="+id);
        }else{
        	messageService.updateMessageIsRead(Long.parseLong(id));
		    return BaseOutput.success("修改状态成功");
        }
		
	}
	
	@ApiOperation(value="查询Message", notes = "查询Message，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Message", paramType="form", value = "Message的form信息", required = false, dataType = "string")
	})
	@CrossOrigin(origins = {"http://almadmin.diligrp.com", "null"})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody Map<String, Object> list(String userId) {
        return messageService.mapMessagges(userId);
    }
}
