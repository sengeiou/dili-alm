package com.dili.alm.controller;

import com.dili.alm.domain.Message;
import com.dili.alm.service.MessageService;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-05 10:42:19.
 */
@Api("/message")
@Controller
@RequestMapping("/message")
public class MessageController {
    @Autowired
    MessageService messageService;

    @ApiOperation("跳转到Message页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "message/index";
    }

    @ApiOperation(value="查询Message", notes = "查询Message，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Message", paramType="form", value = "Message的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody Map<String,Object> list() {
        return messageService.mapMessagges();
    }
}