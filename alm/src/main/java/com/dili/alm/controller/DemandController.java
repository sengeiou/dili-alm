package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants.DemandProcessStatus;
import com.dili.alm.constant.AlmConstants.DemandStatus;
import com.dili.alm.domain.ApproveResult;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.DemandOperationRecord;
import com.dili.alm.domain.DemandOperationType;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.HardwareApplyOperationRecord;
import com.dili.alm.domain.SystemDto;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.exceptions.DemandExceptions;
import com.dili.alm.rpc.SysProjectRpc;
import com.dili.alm.service.DemandService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.impl.DemandServiceImpl;
import com.dili.alm.utils.WebUtil;
import com.dili.bpmc.sdk.annotation.BpmTask;
import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.restful.BpmcFormRpc;
import com.dili.bpmc.sdk.rpc.restful.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.rpc.DepartmentRpc;
import com.dili.uap.sdk.rpc.FirmRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;

/*import io.swagger.annotations.Api;*/
/*import io.swagger.annotations.ApiImplicitParam;*/
/*import io.swagger.annotations.ApiImplicitParams;*/
/*import io.swagger.annotations.ApiOperation;*/
import tk.mybatis.mapper.entity.Example;

/**
 * ???MyBatis Generator??????????????????
 * This file was generated on 2019-12-23 17:32:14.
 */
/*@Api("/demand")
*/
@Controller
@RequestMapping("/demand")
public class DemandController {
	@Autowired
	private FirmRpc firmRpc;
	@Autowired
	private DepartmentRpc deptRpc;
    @Autowired
    TaskRpc taskRpc;
    @Autowired
    UserRpc userRpc;
    @Autowired
    private SysProjectRpc sysProjectRpc;
    @Autowired
    DemandService demandService;
    @Autowired
    FilesService filesService;
    @Autowired
    BpmcFormRpc bpmcFormRpc;
    
	private static final Logger LOGGER = LoggerFactory.getLogger(DemandController.class);
    /*@ApiOperation("?????????Demand??????")*/
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "demand/index";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap modelMap) {
		/** ?????? ???????????? ***/
/*    	Department department=DTOUtils.newDTO(Department.class);
		department.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		
		List<Department> departments = this.deptRpc.listByDepartment(department).getData();
		modelMap.addAttribute("departments", departments);*/

		/** ???????????? **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		
		modelMap.addAttribute("userInfo", userTicket);
		if (userTicket.getFirmCode()!=null) {
			BaseOutput<Firm> firm = firmRpc.getByCode(userTicket.getFirmCode());
			modelMap.addAttribute("firmName",firm.getData().getName());
		}else {
			modelMap.addAttribute("firmName","");
		}
		return "demand/add";
	}
  
	/**
	 * ????????????????????????
	 * @param id
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(@RequestParam Long id,  ModelMap map) throws Exception {
		DemandDto detailViewData = this.demandService.getDetailViewData(id);
		try {
			Object parseViewModel = DemandServiceImpl.parseViewModel(detailViewData);
			map.addAttribute("model",parseViewModel);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "demand/detail";
	}
	
/*	@ApiOperation(value = "??????Task", notes = "??????Task?????????????????????")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Task", paramType = "form", value = "Task???form??????", required = false, dataType = "string") })*/
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Demand> list(Demand task) {

		return demandService.list(task);
	}

/*    @ApiOperation(value="????????????Demand", notes = "????????????Demand?????????easyui????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(DemandDto demand)  {
    	EasyuiPageOutput listPageForUser = null;
    	try {
    		listPageForUser = demandService.listPageForUser(demand);
		} catch (Exception e) {
		  return	BaseOutput.failure(e.getMessage()).toString();
		}
    	
        return listPageForUser.toString();
    }

/*    @ApiOperation("??????Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = true, dataType = "string")
	})*/
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Demand demand) {
    	if (demand.getType()==null) {
    		return BaseOutput.failure("???????????????????????????");
		}

    	if(demand.getType()!=1&&demand.getBelongProId()==null) {
    		return BaseOutput.failure("???????????????????????????????????????");
    	}
        try {
			demandService.addNewDemand(demand);
		} catch (DemandExceptions e) {
			return BaseOutput.failure(e.getMessage());
		}
        return BaseOutput.success("????????????");
    }

/*    @ApiOperation("??????Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = true, dataType = "string")
	})*/
    @RequestMapping(value="/submint", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput submint(Demand demand) {
    	if (demand.getType()==null) {
    		return BaseOutput.failure("???????????????????????????");
		}
    	if(demand.getType()!=1&&demand.getBelongProId()==null) {
    		return BaseOutput.failure("???????????????????????????????????????");
    	}
        try {
			demandService.submint(demand);
		} catch (DemandExceptions e) {
			return BaseOutput.failure(e.getMessage());
		}
        return BaseOutput.success("????????????");
    }
    
	//????????????????????????
	@ResponseBody
	@RequestMapping(value = "/demandInfo.json", method = { RequestMethod.GET, RequestMethod.POST })
	public Demand updateDemandInfo(Long id) {
        Demand demand = new Demand();
        demand = demandService.get(id);
		return demand;
	}
/*    @ApiOperation("??????Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = true, dataType = "string")
	})*/
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Demand demand) {
        demandService.updateSelective(demand);
        return BaseOutput.success("????????????");
    }
    
/*    @ApiOperation("??????????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = true, dataType = "string")
	})*/
    @RequestMapping(value="/editFlag.json", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput isEdit(Long id) {
    	Demand demand = demandService.get(id);
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
    	if (!userTicket.getId().equals(demand.getUserId())) {
    		 return BaseOutput.failure("????????????????????????????????????");
		}
        return BaseOutput.success().setCode("1");
    }
    
/*    @ApiOperation("??????Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Demand?????????", required = true, dataType = "long")
	})*/
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
    	try {
    		demandService.logicDelete(id);
		} catch (Exception e) {
			 return  BaseOutput.failure("???????????????"+e.getMessage());
		}
        return BaseOutput.success("????????????");
    }
/*    @ApiOperation("???????????????????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = true, dataType = "string")
	})*/
    @RequestMapping(value="/sendseq.json", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput sendseq(Demand demand) {
		demandService.setDailyBegin();
        return BaseOutput.success("????????????");
    }
    
    /**
     * ???????????????????????????project???????????????
     * @param projectId?????????????????????????????????;???????????????????????????????????????project??????????????????
     * @return
     */
/*    @ApiOperation(value="??????Demand?????????", notes = "??????Demand?????????????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/queryDemandListToProject.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListToProject(Long projectId) {
        return demandService.queryDemandListToProject(projectId);
    }
    /**
     * ????????????id???????????????????????????
     * @param ids
     * @return
     */
/*    @ApiOperation(value="??????Demand??????id??????", notes = "??????Demand?????????????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/queryDemandListByIds.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListByIds(String ids) {
    	if(WebUtil.strIsEmpty(ids)) {
    		return null;
    	}
        return demandService.queryDemandListByIds(ids);
    }
    /**
     * ??????id???type??????????????????id???????????????
     * @param id ?????????Id,??????Id,??????Id???
     * @param type?????????,??????,?????????
     * @return
     */
    
/*    @ApiOperation(value="???????????????Demand", notes = "??????Demand?????????????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/queryDemandListByApplyIdOrVersionIdOrWorkOrderId.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id,Integer type) {
    	if(id==null||type==null) {
    		return null;
    	}
        return demandService.queryDemandListByProjectIdOrVersionIdOrWorkOrderId(id,type);
    }
    /**
     * ???????????????????????????
     * @param id
     * @return
     */
/*    @ApiOperation(value="??????Id????????????", notes = "??????File??????????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/files/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Map> filesList(Long id) {
		Demand demand = this.demandService.get(id);
		
		List<Files> list=new ArrayList<Files>();
		//List<Long> ids = JSONArray.parseArray(demand.getDocumentUrl(), Long.class);
		Map<Object, Object> metadata = new HashMap<>();

    	JSONObject fileTypeProvider = new JSONObject();
		fileTypeProvider.put("provider", "fileTypeProvider");
		metadata.put("type", fileTypeProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberForAllProvider");
		metadata.put("createMemberId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("created", datetimeProvider);
		
		try {
			String urlStr = demand.getDocumentUrl();
			if (urlStr==null) {
				return ValueProviderUtils.buildDataByProvider(metadata, list);
			}
			//???????????????
			String[] strArr = urlStr.split(",");
			List<Long> ids = Arrays.stream(strArr)
			        .map(s ->Long.parseLong(s.trim())).collect(Collectors.toList());
			

			if(ids!=null&&ids.size()>0) {
				Example example = new Example(Files.class);
				Example.Criteria criteria = example.createCriteria();
				criteria.andIn("id", ids);
				list = this.filesService.selectByExample(example);
			}
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * ??????????????????
     * @param id
     * @return
     */
/*    @ApiOperation(value="??????Id????????????", notes = "??????File??????????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/files/fdFile", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody Files fdFile(Long id) {
		
		try {
			Files files = this.filesService.get(id);
			return files;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	//????????????
	@ResponseBody
	@RequestMapping(value = "/listTree.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<SystemDto> listTree(Long projectId) {
		return sysProjectRpc.list(DTOUtils.newDTO(SystemDto.class)).getData();
	}
    /**departmentApprove
     * ????????????
     */
   /* @ApiOperation("?????????????????????")*/
    @RequestMapping(value="/departmentApprove.html", method = RequestMethod.GET)
    public String departmentApprove(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
    	BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        Demand demand = new Demand();
        demand = demandService.getByCode(codeDates);
        
        String demandJsonStr = JSONObject.toJSONString(demand);
        modelMap.put("demand", demand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(demand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/departmentApprove";
    }
    /**
     * ?????????????????????
     * @param code  ????????????
     * @param taskId ??????id
     * @return
     */
    @RequestMapping(value="/demandDepartmentApprove.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> doSubmit(@RequestParam String code, @RequestParam String taskId,String description) {
      try {
			  demandService.saveOprationRecord(code, description, DemandOperationType.DEMAND_MANAGER.getValue(), DemandOperationType.DEMAND_MANAGER.getName(), ApproveResult.APPROVED);
			} catch (DemandExceptions e) {
				e.printStackTrace();
			}
    	return demandService.submitApprove(code, taskId,null,DemandProcessStatus.DEPARTMENTMANAGER.getCode(),null);
    }
    
   /* @ApiOperation("???????????????????????????")*/
    @RequestMapping(value="/accept.html", method = RequestMethod.GET)
    public String accept(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        Demand demand = new Demand();
        demand = demandService.getByCode(codeDates);
        
        String demandJsonStr = JSONObject.toJSONString(demand);
        modelMap.put("demand", demand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(demand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/accept";
    }
    /**
     * ????????????????????????
     * @param code  ????????????
     * @param taskId ??????id
     * @return
     */
    @RequestMapping(value="/accept.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> accept(@RequestParam String code, @RequestParam String taskId,String description,Long acceptId,String isForword) {
        try {
  			 demandService.saveOprationRecord(code, description, DemandOperationType.ACCEPT.getValue(), DemandOperationType.ACCEPT.getName(), ApproveResult.APPROVED);
  			} catch (DemandExceptions e) {
  				e.printStackTrace();
  			}
    	return demandService.submitApproveForAccept(code,taskId,acceptId);
    }
 
    /*@ApiOperation("?????????????????????")*/
    @RequestMapping(value="/assignPrincipal.html", method = RequestMethod.GET)
    public String assignPrincipal(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        Demand demand = new Demand();
        demand = demandService.getByCode(codeDates);
        
        String demandJsonStr = JSONObject.toJSONString(demand);
        modelMap.put("demand", demand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(demand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/assignPrincipal";
    }
    /**assignPrincipal
     * ??????????????????
     * @param code  ????????????
     * @param taskId ??????id
     * @return
     */
    @RequestMapping(value="/assignPrincipal.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> doAssignPrincipal(@RequestParam String code, @RequestParam String taskId, 
			Long acceptId,String description) {
    	BaseOutput<String> out = new BaseOutput<String>();
/*		if (operDepartmentUsers == null) {
			return BaseOutput.failure("????????????");
		}
		Set<Long> executors = new HashSet<Long>();
		for (int i = 0; i < operDepartmentUsers.length; i++) {
			executors.add(Long.parseLong(operDepartmentUsers[i]));
		}*/
        try {
  				demandService.saveOprationRecord(code, description, DemandOperationType.ASSIGN.getValue(), DemandOperationType.ASSIGN.getName(), ApproveResult.APPROVED);
  			} catch (DemandExceptions e) {
  				e.printStackTrace();
  			}
        out = demandService.submitApproveForAssign(acceptId, taskId);
        return out;
    	
    }
 
   /* @ApiOperation("??????????????????????????????")*/
    @RequestMapping(value="/reciprocate.html", method = RequestMethod.GET)
    public String reciprocate(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        Demand demand = new Demand();
        demand = demandService.getByCode(codeDates);
        
        String demandJsonStr = JSONObject.toJSONString(demand);
        modelMap.put("demand", demand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(demand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/reciprocate";
    }
    /**
     * ??????????????????????????????????????????
     * @param code  ????????????
     * @param taskId ??????id
     * @return
     */
    @RequestMapping(value="/reciprocate.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> reciprocate(@RequestParam String code, @RequestParam String taskId,@RequestParam Long acceptId,String description) {
		if (acceptId == null) {
			return BaseOutput.failure("????????????");
		}
/*		Set<Long> executors = new HashSet<Long>();
		for (int i = 0; i < operDepartmentUsers.length; i++) {
			executors.add(Long.parseLong(operDepartmentUsers[i]));
		}*/
        try {
  				demandService.saveOprationRecord(code, description, DemandOperationType.RECIPROCATE.getValue(), DemandOperationType.RECIPROCATE.getName(), ApproveResult.APPROVED);
  			} catch (DemandExceptions e) {
  				e.printStackTrace();
  			}
    	return demandService.submitApproveAndAccept(acceptId, taskId);
    }
    
    
    
   /* @ApiOperation("????????????")*/
    @RequestMapping(value="/feedback.html", method = RequestMethod.GET)
    public String feedback(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        Demand demand = new Demand();
        demand = demandService.getByCode(codeDates);
        
        String demandJsonStr = JSONObject.toJSONString(demand);
        modelMap.put("demand", demand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(demand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/feedback";
    }
    /**
     * ????????????
     * @param code  ????????????
     * @param taskId ??????id????????????form
     * @return
     */
    @RequestMapping(value="/feedback.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> feedback(@RequestParam String code, @RequestParam String taskId, @RequestParam String content,String documentUrl,String description) {
    	Demand demand = demandService.getByCode(code);
    	demand.setFeedbackContent(content);
    	demand.setFeedbackFile(documentUrl);
    	demandService.saveOrUpdate(demand);
        try {
  				demandService.saveOprationRecord(code,"??????????????????", DemandOperationType.FEEDBACK.getValue(), DemandOperationType.FEEDBACK.getName(), ApproveResult.APPROVED);
  			} catch (DemandExceptions e) {
  				e.printStackTrace();
  			}
    	return demandService.submitApprove(code, taskId,null,DemandProcessStatus.FEEDBACK.getCode(),null);
    }
    /*@ApiOperation("?????????????????????")*/
    @RequestMapping(value="/demandManagerApprove.html", method = RequestMethod.GET)
    public String demandManagerApprove(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        Demand demand = new Demand();
        demand = demandService.getByCode(codeDates);
        
        String demandJsonStr = JSONObject.toJSONString(demand);
        modelMap.put("demand", demand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(demand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/demandManagerApprove";
    }
    /**
     * ?????????????????????
     * @param code  ????????????
     * @param taskId ??????id
     * @return
     */
    @RequestMapping(value="/demandManagerApprove.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> demandManagerApprove(@RequestParam String code, @RequestParam String taskId,String description,Integer imperative) {
    	if(null == imperative) {
        	return BaseOutput.failure("???????????????");
        }
    	try {
  			 demandService.saveOprationRecord(code, description, DemandOperationType.DEMAND_ADMIN.getValue(), DemandOperationType.DEMAND_ADMIN.getName(), ApproveResult.APPROVED);
  			} catch (DemandExceptions e) {
  				e.printStackTrace();
  			}
    	return demandService.submitApprove(code, taskId, (byte) DemandStatus.COMPLETE.getCode(),DemandProcessStatus.DEMANDMANAGER.getCode(),imperative);
    }
    
    /**
     * ????????????
     * @param code  ????????????
     * @param taskId ??????id
     * @return
     */
    @RequestMapping(value="/rejectDemand.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> rejectDemand(@RequestParam String code, @RequestParam String taskId,String description,String rejectType) {
    	
        try {
  			  demandService.saveOprationRecord(code, description, DemandOperationType.REJECT.getValue(), DemandOperationType.REJECT.getName(), ApproveResult.FAILED);
  			} catch (DemandExceptions e) {
  				e.printStackTrace();
  			}
    	return demandService.rejectApprove(code, taskId,rejectType);
    }
    /**
     * ???????????????????????????????????????????????????
     * @param code  ????????????
     * @param taskId ??????id
     * @return
     */
    @RequestMapping(value="/rejectDemandForFeedback.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> rejectDemandForFeedback(@RequestParam String code, @RequestParam String taskId,@RequestParam Long acceptId,String description,String rejectType) {
    	
        try {
  			  demandService.saveOprationRecord(code, "??????????????????", DemandOperationType.REJECTFEEDBACK.getValue(), DemandOperationType.REJECTFEEDBACK.getName(), ApproveResult.FORWORD);
  			} catch (DemandExceptions e) {
  				e.printStackTrace();
  			}
    	return demandService.rejectApproveForFeedback(code, taskId, rejectType, acceptId);
    }
    /**
     * ??????????????????????????????
     * @param modelMap
     * @param cover ????????????
     * @return
     */
    @BpmTask(formKey = "almDemandDepartmentApproveForm", defKey = "departmentManagerApprove")
    @RequestMapping(value="/departmentManagerApprove.html", method = RequestMethod.GET)
    public String submit(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        Demand demand = new Demand();
        demand = demandService.getByCode(codeDates);
        
        String demandJsonStr = JSONObject.toJSONString(demand);
        modelMap.put("demand", demand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(demand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
        return "departmentApprove";
    }
    
    

    
    @RequestMapping(value = "/editForTask.html", method = RequestMethod.GET)
	public String editForTask(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        Demand demand = new Demand();
        demand = demandService.getByCode(codeDates);
        
        String demandJsonStr = JSONObject.toJSONString(demand);
        modelMap.put("demand", demand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(demand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
    	modelMap.put("taskId", taskId);
		return "demand/editForTask";
	}
    /*@ApiOperation("??????????????????????????????")*/
    @RequestMapping(value = "/editForTaskByAlm.html", method = RequestMethod.GET)
	public String editForDemandList(@RequestParam Long id, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
    	Demand selectDemand = new Demand();
    	selectDemand=demandService.get(id);
    	String valProcess = selectDemand.getSerialNumber();
    	
        //???????????????????????????
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(valProcess);
        BaseOutput<List<TaskMapping>> outputList = taskRpc.list(taskDto);
        if(!outputList.isSuccess()){
        	return "???????????????"+outputList.getMessage(); 
        }
        
        List<TaskMapping> taskMappings = outputList.getData();
        
        String demandJsonStr = JSONObject.toJSONString(selectDemand);
        modelMap.put("demand", selectDemand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(selectDemand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
    	modelMap.put("taskId", taskMappings.get(0).getId());
		return "demand/editForTask";
	}
/*    @ApiOperation("????????????Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = true, dataType = "string")
	})*/
    @RequestMapping(value="/submintForTask.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput submintForTask(@RequestParam String taskId, Demand demand) {
        try {
        	demandService.saveOprationRecord(demand.getSerialNumber(),"??????????????????", DemandOperationType.RESUBMIT.getValue(), DemandOperationType.RESUBMIT.getName(), ApproveResult.APPROVED);
			demandService.reSubmint(demand, taskId);
		} catch (DemandExceptions e) {
			return BaseOutput.failure(e.getMessage());
		}
        return BaseOutput.success("????????????");
    }

   /* @ApiOperation("??????????????????")*/
    @RequestMapping(value = "/submitApproveByAlm", method = RequestMethod.GET)
	public String submitApproveByAlm(@RequestParam Long id, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
    	Demand selectDemand = new Demand();
    	selectDemand=demandService.get(id);
    	String valProcess = selectDemand.getSerialNumber();
    	
        //???????????????????????????
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(valProcess);
        BaseOutput<List<TaskMapping>> outputList = taskRpc.list(taskDto);
        if(!outputList.isSuccess()){
        	return "???????????????"+outputList.getMessage(); 
        }
        
        List<TaskMapping> taskMappings = outputList.getData();
        //??????formKey
        TaskMapping selected = taskMappings.get(0);
        //??????bpmc???form???????????????????????????????????????
        ActForm selectActFrom  = bpmcFormRpc.getByKey(selected.getFormKey()).getData();
        String url = selectActFrom.getActionUrl();
        
        String demandJsonStr = JSONObject.toJSONString(selectDemand);
        modelMap.put("model", selectDemand);
        modelMap.put("modelStr", demandJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(selectDemand.getUserId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	//BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);

		//?????????????????????
		Firm firm = AlmCache.getInstance().getFirmMap().get(userTicket.getFirmCode());
		if (firm!=null) {
			modelMap.addAttribute("depName",firm.getName());
		}else {
			modelMap.addAttribute("depName","");
		}
		
    	modelMap.put("taskId",selected.getId());
		return url+"?taskId="+selected.getId();
	}
    
    
/*    @ApiOperation("????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = true, dataType = "string")
	})*/
    @RequestMapping(value="/doClaimTask.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput doClaimTask(@RequestParam Long id) {
    	Demand selectDemand = new Demand();
    	selectDemand=demandService.get(id);
    	String valProcess = selectDemand.getSerialNumber();
    	
        //???????????????????????????
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(valProcess);
        BaseOutput<List<TaskMapping>> outputList = taskRpc.list(taskDto);
        if(!outputList.isSuccess()){
        	return  BaseOutput.failure("???????????????"+outputList.getMessage()); 
        }
        List<TaskMapping> taskMappings = outputList.getData();
        //??????formKey
        TaskMapping selected = taskMappings.get(0);
		/** ???????????? **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		BaseOutput<String> claimOut = taskRpc.claim(selected.getId(), userTicket.getId()+"");
        return BaseOutput.success(claimOut.getMessage());
    }
    
    
    
/*    @ApiOperation("??????????????????")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand???form??????", required = true, dataType = "string")
	})*/
    @RequestMapping(value="getRecordList", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getRecordList(String code) {
        return this.demandService.getOprationRecordList(code).toString();
    }
}