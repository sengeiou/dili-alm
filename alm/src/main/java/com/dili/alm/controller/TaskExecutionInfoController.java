package com.dili.alm.controller;

import com.dili.alm.domain.TaskExecutionInfo;
import com.dili.alm.service.TaskExecutionInfoService;
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
 * This file was generated on 2017-11-22 16:02:20.
 */
@Api("/taskExecutionInfo")
@Controller
@RequestMapping("/taskExecutionInfo")
public class TaskExecutionInfoController {
    @Autowired
    TaskExecutionInfoService taskExecutionInfoService;

    @ApiOperation("��ת��TaskExecutionInfoҳ��")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "taskExecutionInfo/index";
    }

    @ApiOperation(value="��ѯTaskExecutionInfo", notes = "��ѯTaskExecutionInfo�������б���Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskExecutionInfo", paramType="form", value = "TaskExecutionInfo��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskExecutionInfo> list(TaskExecutionInfo taskExecutionInfo) {
        return taskExecutionInfoService.list(taskExecutionInfo);
    }

    @ApiOperation(value="��ҳ��ѯTaskExecutionInfo", notes = "��ҳ��ѯTaskExecutionInfo������easyui��ҳ��Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskExecutionInfo", paramType="form", value = "TaskExecutionInfo��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(TaskExecutionInfo taskExecutionInfo) throws Exception {
        return taskExecutionInfoService.listEasyuiPageByExample(taskExecutionInfo, true).toString();
    }

    @ApiOperation("����TaskExecutionInfo")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskExecutionInfo", paramType="form", value = "TaskExecutionInfo��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(TaskExecutionInfo taskExecutionInfo) {
        taskExecutionInfoService.insertSelective(taskExecutionInfo);
        return BaseOutput.success("�����ɹ�");
    }

    @ApiOperation("�޸�TaskExecutionInfo")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskExecutionInfo", paramType="form", value = "TaskExecutionInfo��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(TaskExecutionInfo taskExecutionInfo) {
        taskExecutionInfoService.updateSelective(taskExecutionInfo);
        return BaseOutput.success("�޸ĳɹ�");
    }

    @ApiOperation("ɾ��TaskExecutionInfo")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "TaskExecutionInfo������", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        taskExecutionInfoService.delete(id);
        return BaseOutput.success("ɾ���ɹ�");
    }
}