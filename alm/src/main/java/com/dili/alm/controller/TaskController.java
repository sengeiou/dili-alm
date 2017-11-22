package com.dili.alm.controller;

import com.dili.alm.domain.Task;
import com.dili.alm.service.TaskService;
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
 * This file was generated on 2017-11-22 15:55:41.
 */
@Api("/task")
@Controller
@RequestMapping("/task")
public class TaskController {
    @Autowired
    TaskService taskService;

    @ApiOperation("��ת��Taskҳ��")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "task/index";
    }

    @ApiOperation(value="��ѯTask", notes = "��ѯTask�������б���Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Task", paramType="form", value = "Task��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Task> list(Task task) {
        return taskService.list(task);
    }

    @ApiOperation(value="��ҳ��ѯTask", notes = "��ҳ��ѯTask������easyui��ҳ��Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Task", paramType="form", value = "Task��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Task task) throws Exception {
        return taskService.listEasyuiPageByExample(task, true).toString();
    }

    @ApiOperation("����Task")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Task", paramType="form", value = "Task��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Task task) {
        taskService.insertSelective(task);
        return BaseOutput.success("�����ɹ�");
    }

    @ApiOperation("�޸�Task")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Task", paramType="form", value = "Task��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Task task) {
        taskService.updateSelective(task);
        return BaseOutput.success("�޸ĳɹ�");
    }

    @ApiOperation("ɾ��Task")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Task������", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        taskService.delete(id);
        return BaseOutput.success("ɾ���ɹ�");
    }
}