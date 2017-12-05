package com.dili.alm.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



import com.dili.alm.service.LogService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
@Api("/logApi")
@Controller
@RequestMapping("/logApi")
public class LogApi {
	@Autowired
	private LogService logService;
	
	@ApiOperation("新增Log")
	@ApiImplicitParams({
	@ApiImplicitParam(name="Log", paramType="form", value = "Log的form信息", required = true, dataType = "string")
	})
	@RequestMapping(value="/saveLog", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody BaseOutput insert(@RequestBody String logText) {
		if(WebUtil.strIsEmpty(logText)){
	        return BaseOutput.failure("新增失败,包含空值logText="+logText.toString());
        }else{
        	logService.insertLog(logText);
		    return BaseOutput.success("新增成功");
        }
		
	}
	   @ApiOperation("修改Log")
	    @ApiImplicitParams({
			@ApiImplicitParam(name="Log", paramType="form", value = "Log的form信息", required = true, dataType = "string")
		})
	    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
	    public @ResponseBody BaseOutput update(@RequestBody Long logId,@RequestBody String logText) {
	        if(WebUtil.strIsEmpty(logText)){
		        return BaseOutput.failure("修改失败,包含空值logId="+logId+",logText="+logText.toString());
	        }else{
	        	logService.updateLog(logId,logText);
		        return BaseOutput.success("修改成功");
	        }
	    }

	    @ApiOperation("删除Log")
	    @ApiImplicitParams({
			@ApiImplicitParam(name="id", paramType="form", value = "Log的主键", required = true, dataType = "long")
		})
	    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
	    public @ResponseBody BaseOutput delete(@RequestBody Long id) {
	    	if(id == null){
	    		return BaseOutput.failure("删除失败，id为空");
	    	}else{
		    	logService.delete(id);
		        return BaseOutput.success("删除成功");
	    	}
	    }
}
