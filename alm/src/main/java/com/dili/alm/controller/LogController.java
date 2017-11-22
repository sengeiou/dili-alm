package com.dili.alm.controller;

import com.dili.alm.domain.Log;
import com.dili.alm.service.LogService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2017-11-22 16:28:57.
 */
@Api("/log")
@Controller
@RequestMapping("/log")
public class LogController {
    @Autowired
    LogService logService;

    @ApiOperation("��ת��Logҳ��")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "log/index";
    }

    @ApiOperation(value="��ѯLog", notes = "��ѯLog�������б���Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Log", paramType="form", value = "Log��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Log> list(Log log) {
        return logService.list(log);
    }

    @ApiOperation(value="��ҳ��ѯLog", notes = "��ҳ��ѯLog������easyui��ҳ��Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Log", paramType="form", value = "Log��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Log log) throws Exception {
        return logService.listEasyuiPageByExample(log, true).toString();
    }

    @ApiOperation("����Log")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Log", paramType="form", value = "Log��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Log log) {
        logService.insertSelective(log);
        return BaseOutput.success("�����ɹ�");
    }

    @ApiOperation("�޸�Log")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Log", paramType="form", value = "Log��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Log log) {
        logService.updateSelective(log);
        return BaseOutput.success("�޸ĳɹ�");
    }

    @ApiOperation("ɾ��Log")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Log������", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        logService.delete(id);
        return BaseOutput.success("ɾ���ɹ�");
    }
}