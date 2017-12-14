package com.dili.alm.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;














import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WeeklyDetails;
import com.dili.alm.domain.WeeklyJson;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.WeeklyDetailsService;
import com.dili.alm.service.WeeklyService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;


/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-30 12:37:17.
 */
@Api("/weekly")
@Controller
@RequestMapping("/weekly")
public class WeeklyController  {
	private  static final  String  WEEKLY="weekly";
    @Autowired
    WeeklyService weeklyService;
    @Autowired
    WeeklyDetailsService  weeklyDetailsService;


    @ApiOperation("跳转到Weekly页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
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
    /**
     * 返回项目经理
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/listUserByWeekly.json", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<WeeklyPara>  listUserByWeekly() throws Exception {
        return weeklyService.getUser();
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
    @RequestMapping(value="/save", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput save(WeeklyDetails WeeklyDetails) {
    	 WeeklyDetails.setIsSubmit(1);
        WeeklyDetails wd=weeklyDetailsService.getWeeklyDetailsByWeeklyId(WeeklyDetails.getWeeklyId());
    	 if(wd==null)
    		 weeklyDetailsService.createInsert(WeeklyDetails); 
    	 else
    		 weeklyDetailsService.updateSelective(WeeklyDetails);
        return BaseOutput.success("保存成功");
    }
    @RequestMapping(value="/updateWeeklyDetails", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput updateWeeklyDetails(WeeklyDetails WeeklyDetails) {
    	
    	weeklyDetailsService.updateSelective(WeeklyDetails);
        return BaseOutput.success("保存成功");
    }
    @RequestMapping(value="/updateWeeklyDetailsCancel", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput updateWeeklyDetailsCancel(WeeklyDetails WeeklyDetails) {
    	
    	weeklyDetailsService.updateSelective(WeeklyDetails);
        return BaseOutput.success("修改成功");
    }
    
    @RequestMapping(value="/saveMaxQu", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput saveMaxQu(String str,String id) {
    	long longid=Long.parseLong(id);
    	weeklyService.updateMaxQuestion(str, longid);
        return BaseOutput.success("重大问题保存成功");
    }
    @RequestMapping(value="/saveMaxRist", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput saveMaxRist(String str,String id) {
    	long longid=Long.parseLong(id);
    	weeklyService.updateMaxRist(str, longid);
        return BaseOutput.success("重要风险保存成功");
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
    	
    	Map<Object,Object> map=weeklyService.getDescById(id);
    	ModelAndView mv = new ModelAndView();
    	//项目周报
		mv.addObject("pd", (ProjectWeeklyDto)map.get("pd"));
		// 本周项目版本
		mv.addObject("pv",(String) map.get("pv"));
		//本周项目阶段
		mv.addObject("pp",(String) map.get("pp"));
		//下周项目阶段
		mv.addObject("npp", (String)map.get("npp"));
		//本周进展情况 
		mv.addObject("td",(List<TaskDto>)map.get("td"));
		//下周工作计划
		mv.addObject("wk",(List<NextWeeklyDto>) map.get("wk"));
		//当前重要风险
		mv.addObject("wr", map.get("wr"));
		//当前重要问题
	    mv.addObject("wq", map.get("wq"));
	    //项目总体情况描述
	    mv.addObject("wDetails", map.get("wDetails"));
	    
		mv.setViewName("weekly/indexDesc");
        return mv;
    }
    
    @RequestMapping("download") 
    public ResponseEntity<byte[]> download(String id) throws IOException {  
    	
    	File file=new File(WEEKLY+".docx");
         weeklyService.downLoad(file,id);
    	
        HttpHeaders headers = new HttpHeaders();    
        String fileName=new String("周报下载.docx".getBytes("UTF-8"),"iso-8859-1");//为了解决中文名称乱码问题  
        headers.setContentDispositionFormData("attachment", fileName);   
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
       
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);    
    }    
   
    @ApiOperation("跳转到AddWeekD页面")
    @RequestMapping(value="/getDescAddByProjectId", method = RequestMethod.GET)
    public ModelAndView getDescAddByProjectId(String projectId) {
    	
    	 ModelAndView mv = new ModelAndView();
    	 
    	
      	 Map<String, Weekly> wkMap=weeklyService.insertWeeklyByprojectId(projectId);
      	 Weekly  wk=wkMap.get("three");
      	 
    	 Map<Object,Object> map=null;
    	 if(wkMap.get("one")==null){
    		  map=weeklyService.getDescById(wkMap.get("two").getId()+"");
    		
    	 }else{
    		 map=weeklyService.getDescById(wkMap.get("one").getId()+"");
    	 }
    	
    	//项目周报
		mv.addObject("pd", (ProjectWeeklyDto)map.get("pd"));
		wk.setProgress(((ProjectWeeklyDto)map.get("pd")).getCompletedProgress());//存项目
		wk.setProjectId(Long.parseLong(projectId));
		wk.setId(Long.parseLong(((ProjectWeeklyDto)map.get("pd")).getId()));
		weeklyService.updateSelective(wk);
		// 本周项目版本
		mv.addObject("pv",(String) map.get("pv"));
		//本周项目阶段
		mv.addObject("pp",(String) map.get("pp"));
		//下周项目阶段
		mv.addObject("npp", (String)map.get("npp"));
		//本周进展情况 
		mv.addObject("td",(List<TaskDto>)map.get("td"));
		//下周工作计划
		mv.addObject("wk",(List<NextWeeklyDto>) map.get("wk"));
		//当前重要风险
		mv.addObject("wr", map.get("wr"));
		//当前重要问题
	    mv.addObject("wq", map.get("wq"));
	    //项目总体情况描述
	    mv.addObject("wDetails", map.get("wDetails"));
	  /*  if(wkMap.get("one")==null){
	    	 mv.setViewName("weekly/indexDesc");
    		 return mv;
	    }*/
		mv.setViewName("weekly/addWeeklyDesc");
        return mv;
    }
}