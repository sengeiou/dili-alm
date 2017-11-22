package com.dili.alm.controller;

import com.dili.alm.domain.TaskDetails;
import com.dili.alm.service.TaskDetailsService;
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
 * This file was generated on 2017-11-22 16:02:19.
 */
@Api("/taskDetails")
@Controller
@RequestMapping("/taskDetails")
public class TaskDetailsController {
    @Autowired
    TaskDetailsService taskDetailsService;

    @ApiOperation("��ת��TaskDetailsҳ��")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "taskDetails/index";
    }

    @ApiOperation(value="��ѯTaskDetails", notes = "��ѯTaskDetails�������б���Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskDetails", paramType="form", value = "TaskDetails��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskDetails> list(TaskDetails taskDetails) {
        return taskDetailsService.list(taskDetails);
    }

    @ApiOperation(value="��ҳ��ѯTaskDetails", notes = "��ҳ��ѯTaskDetails������easyui��ҳ��Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskDetails", paramType="form", value = "TaskDetails��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(TaskDetails taskDetails) throws Exception {
        return taskDetailsService.listEasyuiPageByExample(taskDetails, true).toString();
    }

    @ApiOperation("����TaskDetails")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskDetails", paramType="form", value = "TaskDetails��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(TaskDetails taskDetails) {
        taskDetailsService.insertSelective(taskDetails);
        return BaseOutput.success("�����ɹ�");
    }

    @ApiOperation("�޸�TaskDetails")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskDetails", paramType="form", value = "TaskDetails��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(TaskDetails taskDetails) {
        taskDetailsService.updateSelective(taskDetails);
        return BaseOutput.success("�޸ĳɹ�");
    }

    @ApiOperation("ɾ��TaskDetails")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "TaskDetails������", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        taskDetailsService.delete(id);
        return BaseOutput.success("ɾ���ɹ�");
    }
}