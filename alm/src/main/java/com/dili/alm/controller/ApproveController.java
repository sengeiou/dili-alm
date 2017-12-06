package com.dili.alm.controller;

import com.alibaba.fastjson.JSON;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.service.ApproveService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-04 17:39:37.
 */
@Api("/approve")
@Controller
@RequestMapping("/approve")
public class ApproveController {
    @Autowired
    ApproveService approveService;


    @ApiOperation("跳转到Approve页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index() {
        return "approveApply/index";
    }

    @RequestMapping(value="/apply/{id}", method = RequestMethod.GET)
    public String apply(ModelMap modelMap, @PathVariable("id") Long id) {

        approveService.buildApplyApprove(modelMap,id);

        return "approveApply/approve";
    }

    @RequestMapping("/loadDesc")
    @ResponseBody
    public List<Map> loadDesc(Long id) throws Exception {
        Approve approve = approveService.get(id);
        Map<Object, Object> metadata = new HashMap<>(2);
        metadata.put("userId", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("approveDate", JSON.parse("{provider:'datetimeProvider'}"));
        List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, JSON.parseArray(approve.getDescription(),ApplyApprove.class));
        return maps;
    }

    @RequestMapping("/applyApprove")
    @ResponseBody
    public BaseOutput applyApprove(Long id, String opt, String notes) {
        return approveService.applyApprove(id, opt, notes);
    }

    @ApiOperation(value="查询Approve", notes = "查询Approve，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Approve", paramType="form", value = "Approve的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Approve> list(Approve approve) {
        return approveService.list(approve);
    }

    @ApiOperation(value="分页查询Approve", notes = "分页查询Approve，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Approve", paramType="form", value = "Approve的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Approve approve) throws Exception {
        return approveService.listEasyuiPageByExample(approve, true).toString();
    }

    @ApiOperation("新增Approve")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Approve", paramType="form", value = "Approve的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Approve approve) {
        approveService.insertSelective(approve);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改Approve")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Approve", paramType="form", value = "Approve的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Approve approve) {
        approveService.updateSelective(approve);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除Approve")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Approve的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        approveService.delete(id);
        return BaseOutput.success("删除成功");
    }
}