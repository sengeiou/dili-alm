package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.Log;
import com.dili.alm.service.LogService;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.rpc.ResourceRpc;
import com.dili.uap.sdk.session.SessionContext;

/*import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;*/

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:19:20.
 */
/*@Api("/log")*/
@Controller
@RequestMapping("/log")
public class LogController {
    @Autowired
    LogService logService;
    @Autowired
    private ResourceRpc resourceRpc;
    
    private final String ALL_USER_LOG_PERMISSIONS="selectAllUsersLog";
    private final String MY_USER_LOG_PERMISSIONS="getMyUserLog";
/*    @ApiOperation("跳转到Log页面")*/
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "log/index";
    }

/*    @ApiOperation(value="查询Log", notes = "查询Log，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Log", paramType="form", value = "Log的form信息", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Log> list(Log log) {
        return logService.list(log);
    }

/*    @ApiOperation(value="分页查询Log", notes = "分页查询Log，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Log", paramType="form", value = "Log的form信息", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Log log,String beginTime,String endTime) throws Exception {
    	UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		List<String> data = resourceRpc.listResourceCodeByUserId(userTicket.getId()).getData();
		if(data.contains(ALL_USER_LOG_PERMISSIONS)){
			return logService.listLogPage(log,beginTime,endTime).toString();
		}else if(data.contains(MY_USER_LOG_PERMISSIONS)){
			log.setOperatorId(userTicket.getId());
			return logService.listLogPage(log,beginTime,endTime).toString();
		}else{
			return null;
		}	
    }
    
//    @ApiOperation("修改Log")
//    @ApiImplicitParams({
//		@ApiImplicitParam(name="Log", paramType="form", value = "Log的form信息", required = true, dataType = "string")
//	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Log log) {
        logService.updateSelective(log);
        return BaseOutput.success("修改成功");
    }

//    @ApiOperation("删除Log")
//    @ApiImplicitParams({
//		@ApiImplicitParam(name="id", paramType="form", value = "Log的主键", required = true, dataType = "long")
//	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        logService.delete(id);
        return BaseOutput.success("删除成功");
    }
}