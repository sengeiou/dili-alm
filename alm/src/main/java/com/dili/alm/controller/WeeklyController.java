package com.dili.alm.controller;

import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.service.WeeklyService;

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
 * This file was generated on 2017-11-30 12:37:17.
 */
@Api("/weekly")
@Controller
@RequestMapping("/weekly")
public class WeeklyController  {
    @Autowired
    WeeklyService weeklyService;

    @ApiOperation("��ת��Weeklyҳ��")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "weekly/index";
    }

    @ApiOperation(value="��ѯWeekly", notes = "��ѯWeekly�������б���Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Weekly> list(Weekly weekly) {
        return weeklyService.list(weekly);
    }

    @ApiOperation(value="��ҳ��ѯWeekly", notes = "��ҳ��ѯWeekly������easyui��ҳ��Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(WeeklyPara weeklyPara) throws Exception {
    	
    	//weeklyService.listEasyuiPageByExample(weekly, true).toString();
        return weeklyService.getListPage(weeklyPara).toString();
    }

    @ApiOperation("����Weekly")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Weekly weekly) {
        weeklyService.insertSelective(weekly);
        return BaseOutput.success("�����ɹ�");
    }

    @ApiOperation("�޸�Weekly")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Weekly weekly) {
        weeklyService.updateSelective(weekly);
        return BaseOutput.success("�޸ĳɹ�");
    }

    @ApiOperation("ɾ��Weekly")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Weekly������", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        weeklyService.delete(id);
        return BaseOutput.success("ɾ���ɹ�");
    }
    
    
    @ApiOperation("��ת��getDescByIdҳ��")
    @RequestMapping(value="/getDescById", method = RequestMethod.GET)
    public String getDescById(ModelMap modelMap) {
        return "weekly/indexDesc";
    }
    
}