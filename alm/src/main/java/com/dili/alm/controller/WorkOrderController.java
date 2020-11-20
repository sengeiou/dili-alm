package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.BpmcUtil;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.WorkOrderExecutionRecordMapper;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderExecutionRecord;
import com.dili.alm.domain.WorkOrderSource;
import com.dili.alm.domain.dto.WorkOrderDto;
import com.dili.alm.domain.dto.WorkOrderQueryDto;
import com.dili.alm.domain.dto.WorkOrderUpdateDto;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.alm.service.DemandService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.WorkOrderService;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.rpc.DepartmentRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;
import com.github.pagehelper.Page;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-05-23 11:51:37.
 */
@Controller
@RequestMapping("/workOrder")
public class WorkOrderController {
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderController.class);
	@Autowired
	private WorkOrderService workOrderService;
	@Qualifier("workOrderNumberGenerator")
	@Autowired
	private NumberGenerator numberGenerator;
	@Autowired
	private FilesService filesService;
	@Autowired
	private DepartmentRpc deptRpc;
	@Autowired
	private DemandService demandService;
	@Autowired
	private BpmcUtil bpmcUtil;
    @Autowired
	private UserRpc userRpc;
   @Autowired
   	private   TaskRpc  tasksRpc;
   
   @Autowired
	private WorkOrderExecutionRecordMapper woerMapper;
   /*   @Autowired
  	private   RuntimeApiRpcCopy  runtimeRpc;*/
	    
	@GetMapping("/detail")
	public String deital(@RequestParam Long id, ModelMap modelMap) {
		WorkOrder workOrder = this.workOrderService.getDetailViewModel(id);
		try {
			modelMap.addAttribute("opRecords", workOrder.aget("opRecords")).addAttribute("model",
					WorkOrderService.parseViewModel(workOrder));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "workOrder/detail";
	}

	@ResponseBody
	@RequestMapping("/currentUserDepartmentUsers")
	public List<User> currentUserDepartmentUsers() {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		Department department = DTOUtils.newDTO(Department.class);
		department.setParentId(user.getDepartmentId());
		department.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<Department>> output = this.deptRpc.getChildDepartments(user.getDepartmentId());
		if (!output.isSuccess()) {
			return null;
		}
	
		List<User> userList=new ArrayList<>();
		return AlmCache
				.getInstance().getUserMap().values().stream().filter(u -> output.getData().stream()
						.filter(d -> d.getId().equals(u.getDepartmentId())).findFirst().orElse(null) != null)
				.collect(Collectors.toList());
	}

	@ResponseBody
	@RequestMapping(value = "/receivers")
	public List<User> getReceivers(@RequestParam Integer type) {
		return this.workOrderService.getReceivers(WorkOrderSource.intValueOf(type));
	}

	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		modelMap.addAttribute("user", SessionContext.getSessionContext().getUserTicket());
		return "workOrder/index";
	}

	@GetMapping("/add")
	public String addView(ModelMap modelMap) {
		modelMap.addAttribute("serialNumber", this.numberGenerator.get()).addAttribute("applicant",
				SessionContext.getSessionContext().getUserTicket());
		return "workOrder/add";
	}

	@GetMapping("/update")
	public String updateView(@RequestParam Long id, ModelMap modelMap) {
		Map<Object, Object> model = this.workOrderService.getViewModel(id);
		modelMap.addAttribute("model", model);
		List<Demand> showDemandList = this.demandService.queryDemandListByProjectIdOrVersionIdOrWorkOrderId(id, AlmConstants.DemandType.WORKORDER.getCode());
		modelMap.addAttribute("showDemandList", showDemandList);
		return "workOrder/update";
	}

	@ResponseBody
	@PostMapping("/saveOrUpdate")
	public BaseOutput<Object> saveOrUpdate(WorkOrderUpdateDto dto, Long[] copyUserIds, MultipartFile attachment,String[] demandIds) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return BaseOutput.failure("请先登录");
		}
		dto.setApplicantId(user.getId());
		if (copyUserIds != null && copyUserIds.length > 0) {
			dto.setCopyUserIds(Arrays.asList(copyUserIds));
		}
		if (!attachment.isEmpty()) {
			this.filesService.delete(dto.getAttachmentFileId());
			List<Files> files = this.filesService.uploadFile(new MultipartFile[] { attachment });
			dto.setAttachmentFileId(files.get(0).getId());
		}
		try {
			this.workOrderService.saveOrUpdate(dto,demandIds);
			Map<Object, Object> viewModel = this.workOrderService.getViewModel(dto.getId());
			return BaseOutput.success().setData(viewModel);
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ResponseBody
	@PostMapping("/saveAndSubmit")
	public BaseOutput<Object> saveAndSubmit(WorkOrderUpdateDto dto, Long[] copyUserIds, MultipartFile attachment,String[] demandIds) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return BaseOutput.failure("请先登录");
		}
		dto.setApplicantId(user.getId());
		if (copyUserIds != null && copyUserIds.length > 0) {
			dto.setCopyUserIds(Arrays.asList(copyUserIds));
		}
		if (!attachment.isEmpty()) {
			this.filesService.delete(dto.getAttachmentFileId());
			List<Files> files = this.filesService.uploadFile(new MultipartFile[] { attachment });
			dto.setAttachmentFileId(files.get(0).getId());
		}
		try {
			this.workOrderService.saveAndSubmit(dto,demandIds);
			Map<Object, Object> viewModel = this.workOrderService.getViewModel(dto.getId());
			return BaseOutput.success().setData(viewModel);
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@GetMapping("/allocate")
	public String allocateView(@RequestParam Long id, ModelMap modelMap) {
		WorkOrder workOrder = this.workOrderService.getOperationRecordsViewModel(id);
		try {
			modelMap.addAttribute("opRecords", workOrder.aget("opRecords")).addAttribute("model",
					WorkOrderService.parseViewModel(workOrder));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "workOrder/allocate";
	}

	@ResponseBody
	@PostMapping("/allocate")
	public BaseOutput<Object> allocate(@RequestParam Long id, @RequestParam Long executorId,
			@RequestParam Integer workOrderType, @RequestParam Integer priority, @RequestParam Integer result,
			@RequestParam String description) {
		try {
			this.workOrderService.allocate(id, executorId, workOrderType, priority, OperationResult.valueOf(result),
					description);
			return BaseOutput.success().setData(this.workOrderService.getViewModel(id));
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@GetMapping("/solve")
	public String solveView(@RequestParam Long id, ModelMap modelMap) {
		WorkOrder workOrder = this.workOrderService.getOperationRecordsViewModel(id);
		try {
			modelMap.addAttribute("opRecords", workOrder.aget("opRecords")).addAttribute("model",
					WorkOrderService.parseViewModel(workOrder));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "workOrder/solve";
	}

	@ResponseBody
	@PostMapping("/solve")
	public BaseOutput<Object> solve(@RequestParam Long id,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, @RequestParam Integer taskHours,
			@RequestParam(required = false, defaultValue = "0") Integer overtimeHours,
			@RequestParam String workContent) {
		if (startDate.compareTo(endDate) > 0) {
			return BaseOutput.failure("工单开始日期不能大于工单结束日期");
		}
		try {
			this.workOrderService.solve(id, startDate, endDate, taskHours, overtimeHours, workContent);
			Map<Object, Object> viewModel = this.workOrderService.getViewModel(id);
			return BaseOutput.success().setData(viewModel);
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@GetMapping("/close")
	public String closeView(@RequestParam Long id, ModelMap modelMap) {
		WorkOrder workOrder = this.workOrderService.getDetailViewModel(id);
		try {
			modelMap.addAttribute("opRecords", workOrder.aget("opRecords")).addAttribute("model",
					WorkOrderService.parseViewModel(workOrder));
			return "workOrder/close";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@ResponseBody
	@PostMapping("/close")
	public BaseOutput<Object> close(@RequestParam Long id, @RequestParam Integer result,
			@RequestParam(required = false) String description) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		try {
			this.workOrderService.close(id, user.getId(), OperationResult.valueOf(result), description);
			Map<Object, Object> viewModel = this.workOrderService.getViewModel(id);
			return BaseOutput.success().setData(viewModel);
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}


	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<WorkOrder> list(WorkOrder workOrder) {
		return workOrderService.list(workOrder);
	}


	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(WorkOrderQueryDto query) throws Exception {
		query.setSort("modifyTime");
		query.setOrder("desc");
		if (query.getSubmitEndDate() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(query.getSubmitEndDate());
			c.add(Calendar.DAY_OF_MONTH, 1);
			query.setSubmitEndDate(c.getTime());
		}
		
	
//		return workOrderService.listEasyuiPageByExample(query, true).toString();
	   List<WorkOrder>  outTemp=workOrderService.listByExample( query);
	 //  List<WorkOrderDto> targetList = DTOUtils.as(outTemp, WorkOrderDto.class);
	  // List<WorkOrderDto> targetList = BeanConver.copyList(outTemp, WorkOrderDto.class);
	  
	   
	   List<WorkOrderDto> targetList = DTOUtils.as(outTemp, WorkOrderDto.class);
		for (WorkOrderDto workOrderDto : targetList) {
			WorkOrderExecutionRecord woerQuery = DTOUtils.newDTO(WorkOrderExecutionRecord.class);
			woerQuery.setWorkOrderId(workOrderDto.getId());
			List<WorkOrderExecutionRecord> woerList = this.woerMapper.select(woerQuery);
			Integer taskHours = 0;
			Integer overtimeHours = 0;
			for (WorkOrderExecutionRecord ex : woerList) {
				taskHours += ex.getTaskHours();
				overtimeHours += ex.getOvertimeHours();
			}
			workOrderDto.setTaskHours(taskHours);
			workOrderDto.setOvertimeHours(overtimeHours);
		}
	   bpmcUtil.fitLoggedUserIsCanHandledProcess(targetList);
	   @SuppressWarnings("rawtypes")
		long total = outTemp instanceof Page ? ((Page) outTemp).getTotal() : outTemp.size();
		return new EasyuiPageOutput(Long.valueOf(total).intValue(), ValueProviderUtils.buildDataByProvider(query, targetList)).toString();
		
	}


	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		try {
			workOrderService.deleteWorkOrder(id);
			return BaseOutput.success();
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
	

	@RequestMapping(value = "/detailAllocate", method = RequestMethod.GET)
	public String detailAllocate(ModelMap modelMap,String  taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		String id =getModelmap(modelMap, taskId, isNeedClaim);
		WorkOrder workOrder = this.workOrderService.getDetailViewModel(Long.parseLong(id));
		try {
			modelMap.addAttribute("opRecords", workOrder.aget("opRecords")).addAttribute("model",WorkOrderService.parseViewModel(workOrder));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "workOrder/detailAllocate";
		
	}

	@RequestMapping(value = "/detailSolve", method = RequestMethod.GET)
	public String detailSolve(ModelMap modelMap,String  taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		String id =getModelmap(modelMap, taskId, isNeedClaim);
		WorkOrder workOrder = this.workOrderService.getDetailViewModel(Long.parseLong(id));
		try {
			modelMap.addAttribute("opRecords", workOrder.aget("opRecords")).addAttribute("model",
					WorkOrderService.parseViewModel(workOrder));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "workOrder/detailSolve";
		
	}
	

	@RequestMapping(value = "/detailClose", method = RequestMethod.GET)
	public String colseSolve(ModelMap modelMap,String  taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		String id =getModelmap(modelMap, taskId, isNeedClaim);
		WorkOrder workOrder = this.workOrderService.getDetailViewModel(Long.parseLong(id));
		try {
			modelMap.addAttribute("opRecords", workOrder.aget("opRecords")).addAttribute("model",
					WorkOrderService.parseViewModel(workOrder));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "workOrder/detailClose";
		
	}

	@RequestMapping(value = "/editWorkOrder", method = RequestMethod.GET)
	public String editSolve(ModelMap modelMap,String  taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		String idStr =getModelmap(modelMap, taskId, isNeedClaim);
		Long id=Long.parseLong(idStr);
		Map<Object, Object> model = this.workOrderService.getViewModel(id);
		modelMap.addAttribute("model", model);
		List<Demand> showDemandList = this.demandService.queryDemandListByProjectIdOrVersionIdOrWorkOrderId(id,  AlmConstants.DemandType.WORKORDER.getCode());
		modelMap.addAttribute("showDemandList", showDemandList);
		return "workOrder/detailUpdate";
	}
	@ResponseBody
	 @RequestMapping(value="/solveAgree", method = {RequestMethod.GET, RequestMethod.POST})
	public BaseOutput<Object> solveAgree(@RequestParam Long id,@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, @RequestParam Integer taskHours,
			@RequestParam(required = false, defaultValue = "0") Integer overtimeHours,
			@RequestParam String workContent) {
		if (startDate.compareTo(endDate) > 0) {
			return BaseOutput.failure("工单开始日期不能大于工单结束日期");
		}
		try {
			this.workOrderService.solve(id, startDate, endDate, taskHours, overtimeHours, workContent);
			Map<Object, Object> viewModel = this.workOrderService.getViewModel(id);
			workOrderService.solveAgree(taskId, isNeedClaim);
			return BaseOutput.success().setData(viewModel);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	
	@ResponseBody
	@PostMapping("/closeAgree")
	public BaseOutput<Object> closeAgree(@RequestParam Long id, @RequestParam Integer result,@RequestParam String taskId,
			@RequestParam(required = false) String description) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		try {
			this.workOrderService.close(id, user.getId(), OperationResult.valueOf(result), description);
			Map<Object, Object> viewModel = this.workOrderService.getViewModel(id);
		
			workOrderService.closeAgree(taskId,null);
			return BaseOutput.success().setData(viewModel);
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	
	@ResponseBody
	@PostMapping("/allocateAgree")
	public BaseOutput<Object> allocateAgree(@RequestParam Long id, @RequestParam Long executorId,@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim,
			@RequestParam Integer workOrderType, @RequestParam Integer priority, @RequestParam Integer result,
			@RequestParam String description) {
		try {
			 result=1;
			this.workOrderService.allocate(id, executorId, workOrderType, priority, OperationResult.valueOf(result),description);
			
			workOrderService.allocateAgree(executorId, taskId, isNeedClaim);
			return BaseOutput.success().setData(this.workOrderService.getViewModel(id));
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	
	@ResponseBody
	@PostMapping("/allocateNotAgree")
	public BaseOutput<Object> allocateNotAgree(@RequestParam Long id, @RequestParam Long executorId,@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim,
			@RequestParam Integer workOrderType, @RequestParam Integer priority, @RequestParam Integer result,
			@RequestParam String description) {
		try {
			 result=0;
			this.workOrderService.allocate(id, executorId, workOrderType, priority, OperationResult.valueOf(result),description);
			
			workOrderService.allocateNotAgree(id, executorId, taskId, isNeedClaim);
			return BaseOutput.success().setData(this.workOrderService.getViewModel(id));
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	
	
	
	@ResponseBody
	@PostMapping("/saveAndAgainSubmit")
	public BaseOutput<Object> saveAndAgainSubmit(WorkOrderUpdateDto dto, Long[] copyUserIds, MultipartFile attachment,String[] demandIds,@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return BaseOutput.failure("请先登录");
		}
		dto.setApplicantId(user.getId());
		if (copyUserIds != null && copyUserIds.length > 0) {
			dto.setCopyUserIds(Arrays.asList(copyUserIds));
		}
		if (!attachment.isEmpty()) {
			this.filesService.delete(dto.getAttachmentFileId());
			List<Files> files = this.filesService.uploadFile(new MultipartFile[] { attachment });
			dto.setAttachmentFileId(files.get(0).getId());
		}
		try {
			this.workOrderService.saveAndAgainSubmit(dto,demandIds,taskId,isNeedClaim);
			Map<Object, Object> viewModel = this.workOrderService.getViewModel(dto.getId());
			return BaseOutput.success().setData(viewModel);
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
	
	
	private String getModelmap(ModelMap modelMap, String taskId,Boolean isNeedClaim) {
	    BaseOutput<Map<String, Object>>  map=tasksRpc.getVariables(taskId);
	    String id = (String) map.getData().get("businessKey");
	    
	    modelMap.addAttribute("taskId",taskId);
	    modelMap.addAttribute("isNeedClaim",isNeedClaim);
	    return id;
	}
	@ResponseBody
	@RequestMapping(value = "/getDataDictionaryDto",  method = {RequestMethod.GET, RequestMethod.POST})
	public String getDataDictionaryDto() {
		Map<String, String> map = this.workOrderService.getDataDictionaryDto();
		String jsonObject = JSONObject.toJSONString(map);

		String result = jsonObject.toString();
		return result;
	}

	
	
}