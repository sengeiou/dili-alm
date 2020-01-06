package com.dili.alm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.rpc.MyTasksRpc;
import com.dili.alm.rpc.RuntimeApiRpc;
//import com.dili.alm.rpc.RuntimeRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.rpc.RuntimeRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2019-12-25 18:22:44.
 */
@Api("/onlineDataChange")
@Controller
@RequestMapping("/onlineDataChange")
public class OnlineDataChangeController {
    @Autowired
    OnlineDataChangeService onlineDataChangeService;
    @Autowired
	private UserRpc userRpc;
    
    @Autowired
   	private   MyTasksRpc  tasksRpc;
    
    @Autowired
  	private   RuntimeApiRpc  runtimeRpc;
    
    @Autowired
	ProjectVersionService projectVersionService;
    
    @ApiOperation("��ת��indexҳ��")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "onlineDataChange/index";
    }
	@ApiOperation("��ת��dataChangeҳ��")
	@RequestMapping(value = "/dataChange1.html", method = RequestMethod.GET)
	public String dataChange1(ModelMap modelMap,String  taskId) {
		BaseOutput<Map<String, Object>>  map=tasksRpc.getVariables(taskId);
		String id = (String) map.getData().get("businessKey");
	    OnlineDataChange  odc=  onlineDataChangeService.get(Long.parseLong(id));
	    modelMap.addAttribute("odc",odc);
	    modelMap.addAttribute("applyUserIdName",userRpc.findUserById(odc.getApplyUserId()).getData().getRealName());
	    ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(odc.getProjectId());
		List<ProjectVersion> list = projectVersionService.list(projectVersion);
		if(list!=null&&list.size()>0) {
			for (ProjectVersion projectVersion2 : list) {
				 if(projectVersion2.getVersion().equals(odc.getVersionId())) {
					 modelMap.addAttribute("version",projectVersion2.getVersion());
					 
				 }
			}
		}
	    modelMap.addAttribute("taskId",taskId);
		return "onlineDataChange/dataChange1";
		
	}
	@ApiOperation("��ת��dataChangeҳ��")
	@RequestMapping(value = "/dataChange2.html", method = RequestMethod.GET)
	public String dataChange2(ModelMap modelMap,String  taskId) {
		BaseOutput<Map<String, Object>>  map=tasksRpc.getVariables(taskId);
		String id = (String) map.getData().get("businessKey");
	    OnlineDataChange  odc=  onlineDataChangeService.get(Long.parseLong(id));
	    modelMap.addAttribute("odc",odc);
	    modelMap.addAttribute("applyUserIdName",userRpc.findUserById(odc.getApplyUserId()).getData().getRealName());
	    ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(odc.getProjectId());
		List<ProjectVersion> list = projectVersionService.list(projectVersion);
		if(list!=null&&list.size()>0) {
			for (ProjectVersion projectVersion2 : list) {
				 if(projectVersion2.getVersion().equals(odc.getVersionId())) {
					 modelMap.addAttribute("version",projectVersion2.getVersion());
					 
				 }
			}
		}
	    modelMap.addAttribute("taskId",taskId);
		return "onlineDataChange/dataChange2";
	}
	
	@ApiOperation("��ת��dataChangeҳ��")
	@RequestMapping(value = "/dataChange3.html", method = RequestMethod.GET)
	public String projectOverview(ModelMap modelMap,String  taskId) {
		BaseOutput<Map<String, Object>>  map=tasksRpc.getVariables(taskId);
		String id = (String) map.getData().get("businessKey");
	    OnlineDataChange  odc=  onlineDataChangeService.get(Long.parseLong(id));
	    modelMap.addAttribute("odc",odc);
	    modelMap.addAttribute("applyUserIdName",userRpc.findUserById(odc.getApplyUserId()).getData().getRealName());
	    ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(odc.getProjectId());
		List<ProjectVersion> list = projectVersionService.list(projectVersion);
		if(list!=null&&list.size()>0) {
			for (ProjectVersion projectVersion2 : list) {
				 if(projectVersion2.getVersion().equals(odc.getVersionId())) {
					 modelMap.addAttribute("version",projectVersion2.getVersion());
					 
				 }
			}
		}
	    modelMap.addAttribute("taskId",taskId);
		return "onlineDataChange/dataChange3";
	}
	
	
    @ApiOperation(value="��ѯOnlineDataChange", notes = "��ѯOnlineDataChange�������б���Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<OnlineDataChange> list(@ModelAttribute OnlineDataChange onlineDataChange) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChange.setApplyUserId(id);
        return onlineDataChangeService.list(onlineDataChange);
    }

    @ApiOperation(value="��ҳ��ѯOnlineDataChange", notes = "��ҳ��ѯOnlineDataChange������easyui��ҳ��Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(@ModelAttribute OnlineDataChange onlineDataChange, @RequestParam String projectIdcc) throws Exception {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChange.setApplyUserId(id);
   	    if(NumberUtils.isNumber(projectIdcc)) {
   	 	   onlineDataChange.setProjectId(Long.parseLong(projectIdcc));
	    }
   	 
    	EasyuiPageOutput   epo=onlineDataChangeService.listEasyuiPageByExample(onlineDataChange, true);
    	List<OnlineDataChange>   list=epo.getRows();
    	 // Page<OnlineDataChange> page =  (Page<OnlineDataChange>) list;
		Map<Object, Object> metadata = null == onlineDataChange.getMetadata() ? new HashMap<>() : onlineDataChange.getMetadata();
		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);
		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("projectVersion", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);
		onlineDataChange.setMetadata(metadata);
		try {
			//List taskList = ValueProviderUtils.buildDataByProvider(onlineDataChange, list);
			  EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(Integer.valueOf(Integer.parseInt(String.valueOf(epo.getTotal()))), list);
			  return taskEasyuiPageOutput.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
       // return onlineDataChangeService.listEasyuiPageByExample(onlineDataChange, true).toString();
    }

    @ApiOperation("����OnlineDataChange")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(@ModelAttribute OnlineDataChange onlineDataChange) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChange.setApplyUserId(id);
        onlineDataChangeService.insertSelective(onlineDataChange);
        try {
    	   Map<String, Object> map=new HashMap<String, Object>();
    	   map.put("dataId", onlineDataChange.getId());
		   BaseOutput<ProcessInstanceMapping>  object= runtimeRpc.startProcessInstanceByKey("almOnlineDataChangeProcess",
				   onlineDataChange.getId().toString(), 74+"",map);
	       System.out.println(object.getCode()+object.getData()+object.getErrorData());
        } catch (Exception e) {
		   e.printStackTrace();
		   System.out.println(e);
	    }
        return BaseOutput.success("保存成功");    
    }

    @ApiOperation("�޸�OnlineDataChange")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(@ModelAttribute OnlineDataChange onlineDataChange,
            HttpServletRequest reques) {
        onlineDataChangeService.updateSelective(onlineDataChange);
        return BaseOutput.success("�޸ĳɹ�");
    }

    @ApiOperation("ɾ��OnlineDataChange")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "OnlineDataChange������", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        onlineDataChangeService.delete(id);
        return BaseOutput.success("ɾ���ɹ�");
    }
    
 
    @ApiOperation("返回当前登录者的信息")
    @RequestMapping(value="/getUserName.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getUserName(Long id ) {
    	if(id==null) {
    		UserTicket user = SessionContext.getSessionContext().getUserTicket();
    		if(user!=null)
    	     return user.getRealName();
    		else
    		  return "请登录";
    	 }else {
    		User user=userRpc.findUserById(id).getData();
    		 return user.getRealName();
    	}
        
    }
/*    @RequestMapping(value="/upload.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput upload(  @RequestParam MultipartFile sqlFile) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	//onlineDataChange.setApplyUserId(id);
    	 String fileName = sqlFile.getOriginalFilename();
     //   onlineDataChangeService.insertSelective(onlineDataChange);
        return BaseOutput.success("11111");
    }*/
    
    @RequestMapping(value="/agreeDeptOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput agreeDeptOnlineData(  @RequestParam(value="taskId", required = false) String taskId) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	Map<String, Object> map=new HashMap<>();
    	map.put("approved", true);
    	tasksRpc.complete(taskId, map);
    	return BaseOutput.success("执行成功");
    }
    @RequestMapping(value="/notAgreeDeptOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput notAgreeDeptOnlineData(  @RequestParam(value="taskId", required = false) String taskId ) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	Map<String, Object> map=new HashMap<>();
    	map.put("approved", false);
    	tasksRpc.complete("202001051851295160000001", map);
        return BaseOutput.success("11111");
    }
    @RequestMapping(value="/agreeTestOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput agreeTestOnlineData(  @RequestParam(value="taskId", required = false) String taskId) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	Map<String, Object> map=new HashMap<>();
    	map.put("approved", true);
    	tasksRpc.complete(taskId, map);
    	return BaseOutput.success("执行成功");
    }
    @RequestMapping(value="/notAgreeTestOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput notAgreeTestOnlineData(  @RequestParam(value="taskId", required = false) String taskId ) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	Map<String, Object> map=new HashMap<>();
    	map.put("approved", false);
    	tasksRpc.complete("202001051851295160000001", map);
        return BaseOutput.success("11111");
    }
    
    
}