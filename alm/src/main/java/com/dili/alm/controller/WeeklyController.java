package com.dili.alm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WeeklyJson;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.service.WeeklyService;
import com.dili.alm.service.impl.WeeklyServiceImpl;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-30 12:37:17.
 */
@Api("/weekly")
@Controller
@RequestMapping("/weekly")
public class WeeklyController  {
    @Autowired
    WeeklyService weeklyService;

    @ApiOperation("跳转到Weekly页面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "weekly/index";
    }

    @ApiOperation(value="查询Weekly", notes = "查询Weekly，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Weekly> list(Weekly weekly) {
        return weeklyService.list(weekly);
    }

    @ApiOperation(value="分页查询Weekly", notes = "分页查询Weekly，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(WeeklyPara weeklyPara) throws Exception {
    	
    	//weeklyService.listEasyuiPageByExample(weekly, true).toString();
        return weeklyService.getListPage(weeklyPara).toString();
    }

    @ApiOperation("新增Weekly")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Weekly weekly) {
        weeklyService.insertSelective(weekly);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改Weekly")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Weekly", paramType="form", value = "Weekly的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Weekly weekly) {
        weeklyService.updateSelective(weekly);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除Weekly")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Weekly的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        weeklyService.delete(id);
        return BaseOutput.success("删除成功");
    }
    
    
    @ApiOperation("跳转到getDescById页面")
    @RequestMapping(value="/getDescById", method = RequestMethod.GET)
    public ModelAndView getDescById(String id) {
    	ModelAndView mv = new ModelAndView();
    	
    	ProjectWeeklyDto pd=weeklyService.getProjectWeeklyDtoById(Long.parseLong(id));
		mv.addObject("pd", pd);
		
		
		// 本周项目版本
		List<String> projectVersion=weeklyService.selectProjectVersion(Long.parseLong(pd.getProjectId()));
		mv.addObject("pv", StringUtils.join(projectVersion.toArray(),","));
		//本周项目阶段
		List<String> projectPhase=weeklyService.selectProjectPhase(Long.parseLong(pd.getProjectId()));
		mv.addObject("pp", StringUtils.join(projectPhase.toArray(),","));
		
		//下周项目阶段
		List<String> nextprojectPhase=weeklyService.selectNextProjectPhase(Long.parseLong(pd.getProjectId()));
		mv.addObject("npp", StringUtils.join(nextprojectPhase.toArray(),","));
		
		//本周进展情况 
		List<TaskDto> td=weeklyService.selectWeeklyProgress(Long.parseLong(pd.getProjectId()));
		mv.addObject("td", td);
				
		//下周工作计划
		List<NextWeeklyDto> wk=weeklyService.selectNextWeeklyProgress(Long.parseLong(pd.getProjectId()));
		mv.addObject("wk", wk);
		
		
		
		WeeklyPara weeklyPara=  new WeeklyPara();
		weeklyPara.setId(Long.parseLong(id));
		
		//当前重要风险
		String weeklyRist=weeklyService.selectWeeklyRist(id);
		JSONArray  weeklyRistJson=JSON.parseArray(weeklyRist);
		mv.addObject("wr", weeklyRistJson.toJavaList(WeeklyJson.class));
		
		//当前重要风险
		String weeklyQuestion=weeklyService.selectWeeklyQuestion(id);
		JSONArray  weeklyQuestionJson=JSON.parseArray(weeklyQuestion);
	    mv.addObject("wq", weeklyQuestionJson);
		
		mv.setViewName("weekly/indexDesc");
        return mv;
    }
   public static void main(String[] args) {
	   
	
	  WeeklyJson  wj=new WeeklyJson();
	  wj.setDesc("sss");
	  wj.setName("sssname");
	  wj.setStatus("sssstatus");
	  WeeklyJson  wj2=new WeeklyJson();
	  wj2.setDesc("sss2");
	  wj2.setName("sssname2");
	  wj2.setStatus("sssstatus2");
	  WeeklyJson  wj3=new WeeklyJson();
	  wj3.setDesc("sss3");
	  wj3.setName("sssname3");
	  wj3.setStatus("sssstatus3");
	  
	  List<WeeklyJson> wjwww=new ArrayList<WeeklyJson>();
	  wjwww.add(wj);
	  wjwww.add(wj2);
	  wjwww.add(wj3);
	  
	  Object  json =  JSON.toJSON(wjwww);
	  System.out.println(json);
	  JSONArray  ss=JSON.parseArray(json.toString());
	System.out.println( ss.toJavaList(WeeklyJson.class) ); 
	 // WeeklyJson stu2=JSONObject.to
   } 
 
   
    
}