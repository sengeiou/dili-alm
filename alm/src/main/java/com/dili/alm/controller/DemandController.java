package com.dili.alm.controller;

import com.dili.alm.domain.Demand;
import com.dili.alm.service.DemandService;
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
 * This file was generated on 2019-12-23 17:32:14.
 */
@Api("/demand")
@Controller
@RequestMapping("/demand")
public class DemandController {
    @Autowired
    DemandService demandService;

    @ApiOperation("��ת��Demandҳ��")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "demand/index";
    }

    @ApiOperation(value="��ѯDemand", notes = "��ѯDemand�������б���Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> list(Demand demand) {
        return demandService.list(demand);
    }

    @ApiOperation(value="��ҳ��ѯDemand", notes = "��ҳ��ѯDemand������easyui��ҳ��Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Demand demand) throws Exception {
        return demandService.listEasyuiPageByExample(demand, true).toString();
    }

    @ApiOperation("����Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Demand demand) {
        demandService.insertSelective(demand);
        return BaseOutput.success("�����ɹ�");
    }

    @ApiOperation("�޸�Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Demand demand) {
        demandService.updateSelective(demand);
        return BaseOutput.success("�޸ĳɹ�");
    }

    @ApiOperation("ɾ��Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Demand������", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        demandService.delete(id);
        return BaseOutput.success("ɾ���ɹ�");
    }
}