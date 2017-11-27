package com.dili.alm.controller;

import com.dili.alm.domain.ProjectApply;
import com.dili.alm.service.ProjectApplyService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-21 16:19:02.
 */
@Api("/projectApply")
@Controller
@RequestMapping("/projectApply")
public class ProjectApplyController {
    @Autowired
    ProjectApplyService projectApplyService;

    @ApiOperation("跳转到ProjectApply页面")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "projectApply/index";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(ModelMap modelMap) {
        return "projectApply/add";
    }

    @RequestMapping(value = "/toStep/{step}/{id}", method = RequestMethod.GET)
    public String toStep(ModelMap modelMap, @PathVariable("id") Long id, @PathVariable("step") int step) {
        modelMap.put("apply", projectApplyService.get(id));
        return "projectApply/step" + step;
    }

    @ApiOperation(value = "查询ProjectApply", notes = "查询ProjectApply，返回列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ProjectApply", paramType = "form", value = "ProjectApply的form信息", required = false, dataType = "string")
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    List<ProjectApply> list(ProjectApply projectApply) {
        return projectApplyService.list(projectApply);
    }

    @ApiOperation(value = "分页查询ProjectApply", notes = "分页查询ProjectApply，返回easyui分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ProjectApply", paramType = "form", value = "ProjectApply的form信息", required = false, dataType = "string")
    })
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    String listPage(ProjectApply projectApply) throws Exception {
        return projectApplyService.listEasyuiPageByExample(projectApply, true).toString();
    }

    @RequestMapping(value = "/getProjectList")
    public @ResponseBody
    Object getProjectList(ProjectApply projectApply) {
        List<ValuePair<?>> buffer = new ArrayList<>();
        buffer.add(new ValuePairImpl("请选择", ""));
        projectApplyService.list(projectApply).
                forEach(apply -> buffer.add(new ValuePairImpl<>(apply.getName(), apply.getId())));
        return buffer;
    }

    @ApiOperation("新增ProjectApply")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ProjectApply", paramType = "form", value = "ProjectApply的form信息", required = true, dataType = "string")
    })
    @RequestMapping(value = "/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput insert(ProjectApply projectApply) {
        projectApplyService.insertSelective(projectApply);
        return BaseOutput.success(String.valueOf(projectApply.getId()));
    }

    @ApiOperation("修改ProjectApply")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ProjectApply", paramType = "form", value = "ProjectApply的form信息", required = true, dataType = "string")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput update(ProjectApply projectApply) {
        projectApplyService.updateSelective(projectApply);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除ProjectApply")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "form", value = "ProjectApply的主键", required = true, dataType = "long")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput delete(Long id) {
        projectApplyService.delete(id);
        return BaseOutput.success("删除成功");
    }
}