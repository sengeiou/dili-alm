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
    public ModelAndView getDescById(String id) {
    	ModelAndView mv = new ModelAndView();
    	
    	ProjectWeeklyDto pd=weeklyService.getProjectWeeklyDtoById(Long.parseLong(id));
		mv.addObject("pd", pd);
		
		
		// ������Ŀ�汾
		List<String> projectVersion=weeklyService.selectProjectVersion(Long.parseLong(pd.getProjectId()));
		mv.addObject("pv", StringUtils.join(projectVersion.toArray(),","));
		//������Ŀ�׶�
		List<String> projectPhase=weeklyService.selectProjectPhase(Long.parseLong(pd.getProjectId()));
		mv.addObject("pp", StringUtils.join(projectPhase.toArray(),","));
		
		//������Ŀ�׶�
		List<String> nextprojectPhase=weeklyService.selectNextProjectPhase(Long.parseLong(pd.getProjectId()));
		mv.addObject("npp", StringUtils.join(nextprojectPhase.toArray(),","));
		
		//���ܽ�չ��� 
		List<TaskDto> td=weeklyService.selectWeeklyProgress(Long.parseLong(pd.getProjectId()));
		mv.addObject("td", td);
				
		//���ܹ����ƻ�
		List<NextWeeklyDto> wk=weeklyService.selectNextWeeklyProgress(Long.parseLong(pd.getProjectId()));
		mv.addObject("wk", wk);
		
		
		
		WeeklyPara weeklyPara=  new WeeklyPara();
		weeklyPara.setId(Long.parseLong(id));
		
		//��ǰ��Ҫ����
		String weeklyRist=weeklyService.selectWeeklyRist(id);
		JSONArray  weeklyRistJson=JSON.parseArray(weeklyRist);
		mv.addObject("wr", weeklyRistJson.toJavaList(WeeklyJson.class));
		
		//��ǰ��Ҫ����
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