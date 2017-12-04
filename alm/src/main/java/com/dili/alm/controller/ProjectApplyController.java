package com.dili.alm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.FileType;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.*;
import com.dili.alm.provider.ProjectTypeProvider;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProviderUtils;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    ProjectTypeProvider projectTypeProvider;

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Autowired
    FilesService filesService;

    @ApiOperation("跳转到ProjectApply页面")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "projectApply/index";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add() {
        return "projectApply/add";
    }

    @RequestMapping(value = "/toStep/{step}/{id}", method = RequestMethod.GET)
    public String toStep(ModelMap modelMap, @PathVariable("id") Long id, @PathVariable("step") int step) throws Exception {
        ProjectApply projectApply = DTOUtils.newDTO(ProjectApply.class);
        projectApply.setId(id);

        Map<Object, Object> metadata = new HashMap<>();

        JSONObject projectTypeProvider = new JSONObject();
        projectTypeProvider.put("provider", "projectTypeProvider");
        metadata.put("type", projectTypeProvider);

        List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectApplyService.listByExample(projectApply));

        Map applyDTO = maps.get(0);
        modelMap.put("apply", applyDTO);
        if (step == 1) {
            buildStepOne(modelMap,applyDTO);
        }
        return "projectApply/step" + step;
    }

    @ApiOperation(value = "查询ProjectApply", notes = "查询ProjectApply，返回列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ProjectApply", paramType = "form", value = "ProjectApply的form信息", dataType = "string")
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
        projectApplyService.insertApply(projectApply);
        return BaseOutput.success(String.valueOf(projectApply.getId()));
    }

    @RequestMapping(value = "/insertStep1", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput insertStep1(ProjectApply projectApply, ApplyMajorResource majorResource) {
        projectApply.setResourceRequire(JSON.toJSONString(majorResource));
        projectApplyService.updateSelective(projectApply);
        return BaseOutput.success(String.valueOf(projectApply.getId()));
    }

    @RequestMapping(value = "/insertStep2", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput insertStep2(ProjectApply projectApply, ApplyDescription description) {
        projectApply.setDescription(JSON.toJSONString(description));
        projectApplyService.updateSelective(projectApply);
        return BaseOutput.success(String.valueOf(projectApply.getId()));
    }

    @RequestMapping(value = "/insertStep3", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput insertStep3(ProjectApply projectApply, ApplyGoalsFunctions goalsFunctions) {
        projectApply.setGoalsFunctions(JSON.toJSONString(goalsFunctions));
        projectApplyService.updateSelective(projectApply);
        return BaseOutput.success(String.valueOf(projectApply.getId()));
    }

    @RequestMapping(value = "/insertStep", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput insertStep(ProjectApply projectApply) {
        projectApplyService.updateSelective(projectApply);
        return BaseOutput.success(String.valueOf(projectApply.getId()));
    }

    @RequestMapping(value = "/submit", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    BaseOutput submit(ProjectApply projectApply,ApplyFiles files) {
        if(StringUtils.isNotBlank(files.getDels())){
            List<Files> filesFromJson = JSON.parseArray(files.getDels(),Files.class);
            filesFromJson.forEach(file ->{
                filesService.delete(file.getId());
            });
        }
        if(StringUtils.isNotBlank(files.getFiles())){
            List<Files> filesFromJson = JSON.parseArray(files.getFiles(),Files.class);
            filesFromJson.forEach(file ->{
                    file.setType(FileType.APPLY.getValue());
                    file.setRecordId(projectApply.getId());
                    filesService.updateSelective(file);
            });
        }

        projectApplyService.updateSelective(projectApply);
        return BaseOutput.success(String.valueOf(projectApply.getId()));
    }

    @RequestMapping("/loadImpact")
    @ResponseBody
    public List<ApplyImpact> loadImpact(Long id) {
        ProjectApply projectApply = projectApplyService.get(id);
        List<ApplyImpact> result = new ArrayList<>();

        if (StringUtils.isNotBlank(projectApply.getImpact())) {
            result = JSON.parseArray(projectApply.getImpact(),ApplyImpact.class);
        }
        return result;
    }

    @RequestMapping("/loadFiles")
    @ResponseBody
    public List<Map> loadFiles(Long applyId) throws Exception {
        Files example = DTOUtils.newDTO(Files.class);
        example.setRecordId(applyId);
        example.setType(FileType.APPLY.getValue());


        Map<Object, Object> metadata = new HashMap<>();
        metadata.put("created", JSON.parse("{provider:'datetimeProvider'}"));
        return ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(filesService.listByExample(example)));
    }

    @RequestMapping("/loadPlan")
    @ResponseBody
    public List<Map> loadPlan(Long id) throws Exception {
        ProjectApply projectApply = projectApplyService.get(id);
        List<ApplyPlan> result = new ArrayList<>();

        if (StringUtils.isNotBlank(projectApply.getPlan())) {
            result = JSON.parseArray(projectApply.getPlan(),ApplyPlan.class);
        }else{
            List<DataDictionaryValueDto> list = projectApplyService.getPlanPhase();
            List<ApplyPlan> finalResult = result;
            list.forEach(dataDictionaryValueDto -> {
                ApplyPlan plan = new ApplyPlan();
                plan.setPhase(dataDictionaryValueDto.getCode());
                finalResult.add(plan);
            });
        }

        Map<Object, Object> metadata = new HashMap<>();
        metadata.put("startDate", JSON.parse("{provider:'datetimeProvider'}"));
        metadata.put("endDate",  JSON.parse("{provider:'datetimeProvider'}"));
        return ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(result));
    }

    @RequestMapping("/loadRisk")
    @ResponseBody
    public List<ApplyRisk> loadRisk(Long id) {
        ProjectApply projectApply = projectApplyService.get(id);
        List<ApplyRisk> result = new ArrayList<>();

        if (StringUtils.isNotBlank(projectApply.getRisk())) {
            result = JSON.parseArray(projectApply.getRisk(),ApplyRisk.class);
        }else{
            List<DataDictionaryValueDto> list = dataDictionaryService.findByCode("kind_risk").getValues();
            List<ApplyRisk> finalResult = result;
            list.forEach(dataDictionaryValueDto -> {
                ApplyRisk risk = new ApplyRisk();
                risk.setType(dataDictionaryValueDto.getCode());
                finalResult.add(risk);
            });
        }
        return result;
    }

    @RequestMapping("/loadApply")
    @ResponseBody
    public Map loadApply(Long id ) throws Exception {
        ProjectApply projectApply = DTOUtils.newDTO(ProjectApply.class);
        projectApply.setId(id);

        Map<Object, Object> metadata = new HashMap<>();
        metadata.put("projectLeader", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("productManager",  JSON.parse("{provider:'memberProvider'}"));
        metadata.put("developmentManager", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("testManager", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("businessOwner", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("dep", JSON.parse("{provider:'depProvider'}"));

        List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectApplyService.listByExample(projectApply));
        return maps.get(0);
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

    /**
     * 构建立项申请第一步数据
     * @param modelMap
     * @param applyDTO
     * @throws Exception
     */
    private void  buildStepOne(ModelMap modelMap,Map applyDTO) throws Exception {
        Map<Object, Object> metadata = new HashMap<>();
        ApplyMajorResource resourceRequire = JSON.parseObject(applyDTO.get("resourceRequire").toString(), ApplyMajorResource.class);
        metadata.clear();
        metadata.put("mainUser", JSON.parse("{provider:'memberProvider'}"));
        List<Map> majorMap = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(resourceRequire));
        metadata.clear();
        metadata.put("relatedUser", JSON.parse("{provider:'memberProvider'}"));
        List<Map> relatedMap = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(resourceRequire.getRelatedResources()));
        modelMap.put("main",majorMap.get(0));
        modelMap.put("related",relatedMap);
    }
}