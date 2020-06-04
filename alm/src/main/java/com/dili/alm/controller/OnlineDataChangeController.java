package com.dili.alm.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.OnlineDataChangeLog;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.TaskDto;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.OnlineDataChangeException;
import com.dili.alm.rpc.MyTasksRpc;
import com.dili.alm.rpc.RuntimeApiRpc;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.OnlineDataChangeLogService;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 *由MyBatis Generator工具自动生成
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
    
    @Autowired
	private ProjectService   projectService;
    @Autowired
   	private   ProjectMapper  projectMapper;
    
    @Autowired
	FilesService filesService;
    
    @Autowired
   	private    OnlineDataChangeLogService onlineDataChangeLogService;
     
    @ApiOperation("跳转到index页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
    	
    	UserTicket user = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("userId", user.getId());
		
        return "onlineDataChange/index";
    }
	@ApiOperation("跳转到dataChange页面")
	@RequestMapping(value = "/dataChangeManager.html", method = RequestMethod.GET)
	public String dataChange1(ModelMap modelMap,@RequestParam String  taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		
		 TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
         //taskDto.setProcessInstanceBusinessKey(approve.getId().toString());
    //     BaseOutput<List<TaskMapping>> outputList = tasksRpc.listTaskMapping(taskDto);
         
		getModelmap(modelMap, taskId,isNeedClaim);
		return "onlineDataChange/dataChange1";
		
	}
	
	@ApiOperation("跳转到dataChange页面")
	@RequestMapping(value = "/dataChangeTest.html", method = RequestMethod.GET)
	public String dataChange2(ModelMap modelMap,String  taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		getModelmap(modelMap, taskId,isNeedClaim);
		return "onlineDataChange/dataChange2";
	}
	
	@ApiOperation("跳转到dataChange页面")
	@RequestMapping(value = "/dataChangeDba.html", method = RequestMethod.GET)
	public String projectOverview(ModelMap modelMap,String  taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		getModelmap(modelMap, taskId,isNeedClaim);
		return "onlineDataChange/dataChange3";
	}
	
	@ApiOperation("跳转到dataChange页面")
	@RequestMapping(value = "/indexDataChange.html", method = RequestMethod.GET)
	public String dbaDataChange(ModelMap modelMap,String  taskId ,@RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		getModelmap(modelMap, taskId,isNeedClaim);
		return "onlineDataChange/indexDataChange";
	}
	@ApiOperation("OnlineDbaDataChange.html")
	@RequestMapping(value = "/OnlineDbaDataChange.html", method = RequestMethod.GET)
	public String onlineDbaDataChange(ModelMap modelMap,String  taskId ,@RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		getModelmap(modelMap, taskId,isNeedClaim);
		return "onlineDataChange/onlineDbaDataChange";
	}
	
    @ApiOperation(value="分页查询OnlineDataChange", notes = "查询OnlineDataChange，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "查询OnlineDataChange，返回列表信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<OnlineDataChange> list(@ModelAttribute OnlineDataChange onlineDataChange) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChange.setApplyUserId(id);
        return onlineDataChangeService.list(onlineDataChange);
    }

    @ApiOperation(value="分页查询OnlineDataChange", notes = "分页查询HardwareResourceApply，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "分页查询HardwareResourceApply，返回easyui分页信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(@ModelAttribute OnlineDataChange onlineDataChange,   @RequestParam(value="projectIdcc", required = false) String projectIdcc) throws Exception {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	return onlineDataChangeService. listPageOnlineData(onlineDataChange, projectIdcc, id);
       // return onlineDataChangeService.listEasyuiPageByExample(onlineDataChange, true).toString();
    }
	

    @ApiOperation("保存OnlineDataChange")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(@ModelAttribute OnlineDataChange onlineDataChange) throws ApplicationException {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChangeService.insertOnLineData(onlineDataChange, id);
        return BaseOutput.success("提交申请成功");    
    }
    @RequestMapping(value="/insertSave.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insertSave(@ModelAttribute OnlineDataChange onlineDataChange) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChange.setApplyUserId(id);
    	onlineDataChange.setDataStatus((byte)0);
    	 onlineDataChange.setCreateDate(new Date());
        onlineDataChangeService.insertSelective(onlineDataChange);

        return BaseOutput.success("保存成功");    
    }
    
    @ApiOperation("返回版本id的信息")
    @RequestMapping(value="/getOnlineDataChange.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getOnlineDataChange(Long id ) {
        OnlineDataChange  object=	onlineDataChangeService.get(id);
        return object.getVersionId().toString();    
        
    }
    
    

    @ApiOperation("修改OnlineDataChange")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(@ModelAttribute OnlineDataChange onlineDataChange, 
            HttpServletRequest reques) throws OnlineDataChangeException {
    	  Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	  onlineDataChangeService. updateOnlineDate(onlineDataChange, id);
        return BaseOutput.success("修改成功");
    }

    @RequestMapping(value="/updateSvae.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput updateSvae(@ModelAttribute OnlineDataChange onlineDataChange,
            HttpServletRequest reques) {
        onlineDataChangeService.updateSelective(onlineDataChange);
        return BaseOutput.success("修改成功");
    }
    
    

    @ApiOperation("删除OnlineDataChange")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "OnlineDataChange根据id删除", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        onlineDataChangeService.delete(id);
        return BaseOutput.success("删除成功");
    }

    @RequestMapping(value="/getOnlineDataChangeById.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getOnlineDataChangeById(Long id) {
       OnlineDataChange obj=  onlineDataChangeService.get(id);
        return obj.getApplyUserId().toString();
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
    public @ResponseBody BaseOutput agreeDeptOnlineData(  @RequestParam(value="taskId", required = false) String taskId,@RequestParam(defaultValue = "false") Boolean isNeedClaim,@RequestParam(defaultValue = "") String description) throws OnlineDataChangeException, ApplicationException {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChangeService.agreeDeptOnlineDataChange(taskId,isNeedClaim,description);
    	return BaseOutput.success("执行成功");
    }

    @RequestMapping(value="/notAgreeDeptOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput notAgreeDeptOnlineData(  @RequestParam(value="taskId", required = false) String taskId,@RequestParam(defaultValue = "false") Boolean isNeedClaim,@RequestParam(defaultValue = "") String description) throws OnlineDataChangeException, ApplicationException {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChangeService.notAgreeDeptOnlineDataChange(taskId,isNeedClaim,description);
        return BaseOutput.success("执行成功");
    }
	
    @RequestMapping(value="/agreeTestOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput agreeTestOnlineData(  @RequestParam(value="taskId", required = false) String taskId,@RequestParam(defaultValue = "false") Boolean isNeedClaim,@RequestParam(defaultValue = "") String description) throws OnlineDataChangeException, ApplicationException {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChangeService.agreeTestOnlineDataChange(taskId,isNeedClaim,description);
    	return BaseOutput.success("执行成功");
    }

    @RequestMapping(value="/notAgreeTestOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput notAgreeTestOnlineData(  @RequestParam(value="taskId", required = false) String taskId,@RequestParam(defaultValue = "false") Boolean isNeedClaim,@RequestParam(defaultValue = "") String description) throws OnlineDataChangeException, ApplicationException {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	
    	onlineDataChangeService.notAgreeTestOnlineDataChange(taskId,isNeedClaim,description);
        return BaseOutput.success("执行成功");
    }

    
    @RequestMapping(value="/agreeDBAOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput agreeDBAOnlineData(  @RequestParam(value="taskId", required = false) String taskId,@RequestParam(defaultValue = "false") Boolean isNeedClaim,@RequestParam(defaultValue = "") String description) throws OnlineDataChangeException, ApplicationException {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	
    	
    	onlineDataChangeService.agreeDBAOnlineDataChange(taskId,isNeedClaim,description);
    	return BaseOutput.success("执行成功");
    }
    @RequestMapping(value="/notAgreeDBAOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput notAgreeDBAOnlineData(  @RequestParam(value="taskId", required = false) String taskId ,@RequestParam(defaultValue = "false") Boolean isNeedClaim,@RequestParam(defaultValue = "") String description) throws OnlineDataChangeException, ApplicationException {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	//onlineDataChangeService.notAgreeDBAOnlineDataChange(taskId, isNeedClaim);
        return BaseOutput.success("执行成功");
    }
    @RequestMapping(value="/indexOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput indexOnlineData(@ModelAttribute OnlineDataChange onlineDataChange  ,@RequestParam(value="taskId", required = false) String taskId,@RequestParam(defaultValue = "") String description) throws OnlineDataChangeException {
    	onlineDataChangeService.indexOnlineDataChange(taskId,onlineDataChange);
    	return BaseOutput.success("执行成功");
    }
    
    @RequestMapping(value="/agreeOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput agreeOnlineData(  @RequestParam(value="taskId", required = false) String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim,@RequestParam(defaultValue = "") String description) throws OnlineDataChangeException, ApplicationException {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChangeService.agreeOnlineDataChange(taskId,isNeedClaim,description);
    	return BaseOutput.success("执行成功");
    }
	
    @RequestMapping(value="/notAgreeOnlineData.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput notAgreeOnlineData(  @RequestParam(value="taskId", required = false) String taskId ,@RequestParam(defaultValue = "false") Boolean isNeedClaim,@RequestParam(defaultValue = "") String description) throws OnlineDataChangeException, ApplicationException {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChangeService.notAgreeOnlineDataChange(taskId,isNeedClaim,description);
        return BaseOutput.success("执行成功");
    }
    
	@ApiOperation("跳转到dataChange页面")
	@RequestMapping(value = "/openView", method = RequestMethod.GET)
	public String openView(ModelMap modelMap,Long id) {
		
	    OnlineDataChange  odc=  onlineDataChangeService.get(id);
	    modelMap.addAttribute("odc",odc);
	    if(odc!=null&&userRpc.findUserById(odc.getApplyUserId())!=null) {
	        modelMap.addAttribute("applyUserIdName",userRpc.findUserById(odc.getApplyUserId()).getData().getRealName());
	    }
	   Project  proId=  projectMapper.selectByPrimaryKey(odc.getProjectId());
	    ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(odc.getProjectId());
		List<ProjectVersion> list = projectVersionService.list(projectVersion);
		
		if(list!=null&&list.size()>0) {
			for (ProjectVersion projectVersion2 : list) {
				// if(projectVersion2.getVersion().equals(odc.getVersionId())) {
					 modelMap.addAttribute("versionTask",projectVersion2.getVersion());
					 break;
				// }
			}
		}
		if(proId!=null)
		  modelMap.addAttribute("proName",proId.getName());
		if(null!=odc.getSqlFileId()&&odc.getSqlFileId()!=""&&StringUtils.isNotEmpty(odc.getSqlFileId())) {
		      Files  fileName=filesService.get(Long.parseLong(odc.getSqlFileId()));
		
		    modelMap.addAttribute("fileName",fileName.getName());
	    }else {
	    	  modelMap.addAttribute("fileName","");
	    }
		return "onlineDataChange/view";
		
	}
    private void getModelmap(ModelMap modelMap, String taskId,Boolean isNeedClaim) {
    	
		BaseOutput<Map<String, Object>>  map=tasksRpc.getVariables(taskId);
		String id = (String) map.getData().get("businessKey");
	    OnlineDataChange  odc=  onlineDataChangeService.get(Long.parseLong(id));
	    modelMap.addAttribute("odc",odc);
	    if(odc!=null&&userRpc.findUserById(odc.getApplyUserId())!=null) {
	        modelMap.addAttribute("applyUserIdName",userRpc.findUserById(odc.getApplyUserId()).getData().getRealName());
	    }
	   Project  proId=  projectMapper.selectByPrimaryKey(odc.getProjectId());
	    ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(odc.getProjectId());
		List<ProjectVersion> list = projectVersionService.list(projectVersion);
		
		if(list!=null&&list.size()>0) {
			for (ProjectVersion projectVersion2 : list) {
				// if(projectVersion2.getVersion().equals(odc.getVersionId())) {
					 modelMap.addAttribute("versionTask",projectVersion2.getVersion());
					 break;
				// }
			}
		}
		if(proId!=null)
		  modelMap.addAttribute("proName",proId.getName());
		

	    if(null!=odc.getSqlFileId()&&odc.getSqlFileId()!=""&&StringUtils.isNotEmpty(odc.getSqlFileId())) {
		      Files  fileName=filesService.get(Long.parseLong(odc.getSqlFileId()));
		
		    modelMap.addAttribute("fileName",fileName.getName());
	    }else {
	    	  modelMap.addAttribute("fileName","");
	    }
		
		
	
		modelMap.addAttribute("taskId",taskId);
	    modelMap.addAttribute("isNeedClaim",isNeedClaim);
	}
    
}