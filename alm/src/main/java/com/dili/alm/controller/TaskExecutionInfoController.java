package com.dili.alm.controller;

import com.dili.alm.domain.TaskExecutionInfo;
import com.dili.alm.service.TaskExecutionInfoService;
import com.dili.ss.domain.BaseOutput;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:23:06.
 */

@Controller
@RequestMapping("/taskExecutionInfo")
public class TaskExecutionInfoController {
    @Autowired
    TaskExecutionInfoService taskExecutionInfoService;


    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "taskExecutionInfo/index";
    }


    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskExecutionInfo> list(TaskExecutionInfo taskExecutionInfo) {
        return taskExecutionInfoService.list(taskExecutionInfo);
    }


    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(TaskExecutionInfo taskExecutionInfo) throws Exception {
        return taskExecutionInfoService.listEasyuiPageByExample(taskExecutionInfo, true).toString();
    }

 
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(TaskExecutionInfo taskExecutionInfo) {
        taskExecutionInfoService.insertSelective(taskExecutionInfo);
        return BaseOutput.success("新增成功");
    }


    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(TaskExecutionInfo taskExecutionInfo) {
        taskExecutionInfoService.updateSelective(taskExecutionInfo);
        return BaseOutput.success("修改成功");
    }

  
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        taskExecutionInfoService.delete(id);
        return BaseOutput.success("删除成功");
    }
}