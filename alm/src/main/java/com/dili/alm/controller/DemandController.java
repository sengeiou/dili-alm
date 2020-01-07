package com.dili.alm.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.AlmConstants.DemandStatus;
import com.dili.alm.domain.Demand;
import com.dili.uap.sdk.domain.Department;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.SystemDto;
import com.dili.alm.domain.Task;
import com.dili.alm.exceptions.DemandExceptions;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.SysProjectRpc;
import com.dili.alm.service.DemandService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.impl.DemandServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.ss.exception.BusinessException;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.alm.utils.WebUtil;
import com.dili.bpmc.sdk.annotation.BpmTask;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:32:14.
 */
@Api("/demand")
@Controller
@RequestMapping("/demand")
public class DemandController {
	@Autowired
	private DepartmentRpc deptRpc;
    @Autowired
    TaskRpc taskRpc;
    @Autowired
    private SysProjectRpc sysProjectRpc;
    @Autowired
    DemandService demandService;
    @Autowired
    FilesService filesService;
    
	private static final Logger LOGGER = LoggerFactory.getLogger(DemandController.class);
    @ApiOperation("跳转到Demand页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "demand/index";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap modelMap) {
		/** 查询 所有部门 ***/
    	Department department=DTOUtils.newDTO(Department.class);
		department.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		List<Department> departments = this.deptRpc.listByDepartment(department).getData();
		modelMap.addAttribute("departments", departments);

		/** 个人信息 **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
		modelMap.addAttribute("userInfo", userTicket);
		
		modelMap.addAttribute("depName",de.getData().getName());
		return "demand/add";
	}
	/**
	 * 需求详情页面显示
	 * @param id
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(@RequestParam Long id,  ModelMap map) throws Exception {
		DemandDto detailViewData = this.demandService.getDetailViewData(id);
		try {
			map.addAttribute("model",DemandServiceImpl.parseViewModel(detailViewData));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "demand/detail";
	}
	
	@ApiOperation(value = "查询Task", notes = "查询Task，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Task", paramType = "form", value = "Task的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Demand> list(Demand task) {

		return demandService.list(task);
	}

    @ApiOperation(value="分页查询Demand", notes = "分页查询Demand，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Demand demand) throws Exception {
        return demandService.listPageForUser(demand).toString();
    }

    @ApiOperation("新增Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Demand demand) {
        try {
			demandService.addNewDemand(demand);
		} catch (DemandExceptions e) {
			return BaseOutput.failure(e.getMessage());
		}
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("提交Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/submint", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput submint(Demand demand) {
        try {
			demandService.submint(demand);
		} catch (DemandExceptions e) {
			return BaseOutput.failure(e.getMessage());
		}
        return BaseOutput.success("提交成功");
    }
    @ApiOperation("修改Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Demand demand) {
        demandService.updateSelective(demand);
        return BaseOutput.success("修改成功");
    }
    
    @ApiOperation("删除Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Demand的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        demandService.delete(id);
        return BaseOutput.success("删除成功");
    }
    @ApiOperation("新增Demand")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/sendseq.json", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput sendseq(Demand demand) {
		demandService.setDailyBegin();
        return BaseOutput.success("新增成功");
    }
    
    /**
     * 查询未立项以及对应project的需求集合
     * @param projectId可以为空，只查询未立项;不为空，查询未立项以及对应project的需求的并集
     * @return
     */
    @ApiOperation(value="查询Demand未立项", notes = "查询Demand，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/queryDemandListToProject.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListToProject(Long projectId) {
        return demandService.queryDemandListToProject(projectId);
    }
    /**
     * 根据需求id的集合查询对应需求
     * @param ids
     * @return
     */
    @ApiOperation(value="查询Demand根据id集合", notes = "查询Demand，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/queryDemandListByIds.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListByIds(String ids) {
    	if(WebUtil.strIsEmpty(ids)) {
    		return null;
    	}
        return demandService.queryDemandListByIds(ids);
    }
    /**
     * 根据id和type组合查询对应id的需求集合
     * @param id （立项Id,工单Id,版本Id）
     * @param type（立项,工单,版本）
     * @return
     */
    
    @ApiOperation(value="查询已关联Demand", notes = "查询Demand，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/queryDemandListByApplyIdOrVersionIdOrWorkOrderId.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id,Integer type) {
    	if(id==null||type==null) {
    		return null;
    	}
        return demandService.queryDemandListByProjectIdOrVersionIdOrWorkOrderId(id,type);
    }
    /**
     * 查询需求关联的文件
     * @param id
     * @return
     */
    @ApiOperation(value="根据Id获取附件", notes = "查询File返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Demand", paramType="form", value = "Demand的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/files/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Map> filesList(Long id) {
		Demand demand = this.demandService.get(id);
		List<Long> ids = JSONArray.parseArray(demand.getDocumentUrl(), Long.class); 
		List<Files> list=new ArrayList<Files>();
		if(ids!=null&&ids.size()>0) {
			Example example = new Example(Files.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andIn("id", ids);
			list = this.filesService.selectByExample(example);
		}else {
			return null;
		}
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject fileTypeProvider = new JSONObject();
		fileTypeProvider.put("provider", "fileTypeProvider");
		metadata.put("type", fileTypeProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("createMemberId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("created", datetimeProvider);

		try {
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
	//查询系统
	@ResponseBody
	@RequestMapping(value = "/listTree.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<SystemDto> listTree(Long projectId) {
		return sysProjectRpc.list(DTOUtils.newDTO(SystemDto.class)).getData();
	}
    /**departmentApprove
     * 流程控制
     */
    @ApiOperation("跳转到权限页面")
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
        modelMap.put("demand", demandService.getByCode(codeDates));
        modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/departmentApprove";
    }
    /**
     * 同意申请
     * @param code  需求编号
     * @param taskId 任务id
     * @return
     */
    @RequestMapping(value="/demandDepartmentApprove.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> doSubmit(@RequestParam String code, @RequestParam String taskId) {
    	return demandService.submitApprove(code, taskId, null, (byte) DemandStatus.COMPLETE.getCode());
    }
    
    @ApiOperation("跳转到接受需求页面")
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
        modelMap.put("demand", demandService.getByCode(codeDates));
        modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/accept";
    }
    /**
     * 同意申请，并制定对接人
     * @param code  需求编号
     * @param taskId 任务id
     * @return
     */
    @RequestMapping(value="/accept.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> accept(@RequestParam String code, @RequestParam String taskId,@RequestParam Long acceptId) {
    	return demandService.submitApproveAndAccept(code, taskId,acceptId);
    }
 
    
    @ApiOperation("跳转到接受需求对接人")
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
        modelMap.put("demand", demandService.getByCode(codeDates));
        modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/reciprocate";
    }
    /**
     * 需求对接人处理，接入动态表单
     * @param code  需求编号
     * @param taskId 任务id
     * @return
     */
    @RequestMapping(value="/reciprocate.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> reciprocate(@RequestParam String code, @RequestParam String taskId) {
    	return demandService.submitApprove(code, taskId,null,null);
    }
    
    
    
    @ApiOperation("反馈方案")
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
        modelMap.put("demand", demandService.getByCode(codeDates));
        modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/feedback";
    }
    /**
     * 反馈方案
     * @param code  需求编号
     * @param taskId 任务id，反馈的form
     * @return
     */
    @RequestMapping(value="/feedback.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> feedback(@RequestParam String code, @RequestParam String taskId, @RequestParam String content, @RequestParam String documentUrl) {
    	return demandService.submitApprove(code, taskId,null,null);
    }
    @ApiOperation("需求管理员同意")
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
        modelMap.put("demand", demandService.getByCode(codeDates));
        modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
    	return "demand/demandManagerApprove";
    }
    /**
     * 需求管理员同意
     * @param code  需求编号
     * @param taskId 任务id
     * @return
     */
    @RequestMapping(value="/demandManagerApprove.action", method = RequestMethod.POST)
    @ResponseBody
    public BaseOutput<String> demandManagerApprove(@RequestParam String code, @RequestParam String taskId) {
    	return demandService.submitApprove(code, taskId,null,(byte)DemandStatus.COMPLETE.getCode());
    }

    /**
     * 提交申请同意表单页面
     * @param modelMap
     * @param cover 用于是否
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
        String code = taskVariablesOutput.getData().get("businessKey").toString();
        modelMap.put("orders", demandService.getByCode(code));
        modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
        return "departmentApprove";
    }
    
    

}