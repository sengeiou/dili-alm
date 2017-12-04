package com.dili.alm.controller;

import com.dili.alm.domain.Weekly;
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

import java.util.List;

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
		mv.addObject("pv", projectVersion);
		
		//������Ŀ�׶�
		List<String> projectPhase=weeklyService.selectProjectPhase(Long.parseLong(pd.getProjectId()));
		mv.addObject("pp", projectPhase);
		
		//���ܹ����ƻ�
		Weekly wk=weeklyService.selectNextWeeklyProgress(Long.parseLong(pd.getProjectId()));
		mv.addObject("wk", wk);
		
		//���ܽ�չ��� 
		TaskDto td=weeklyService.selectWeeklyProgress(Long.parseLong(pd.getProjectId()));
		mv.addObject("td", td);
		
		WeeklyPara weeklyPara=  new WeeklyPara();
		weeklyPara.setId(Long.parseLong(id));
		
		//��ǰ��Ҫ����
		String weeklyRist=weeklyService.selectWeeklyRist(weeklyPara);
		mv.addObject("wr", weeklyRist);
		
		//��ǰ��Ҫ����
		String weeklyQuestion=weeklyService.selectWeeklyQuestion(weeklyPara);
	    mv.addObject("w", weeklyQuestion);
		
		mv.setViewName("weekly/indexDesc");
        return mv;
    }
   
    
}