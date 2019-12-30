package com.dili.alm.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.NumberGenerator;
import com.dili.uap.sdk.domain.Department;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.OperationResult;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderSource;
import com.dili.alm.domain.dto.WorkOrderQueryDto;
import com.dili.alm.domain.dto.WorkOrderUpdateDto;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.WorkOrderService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-05-23 11:51:37.
 */
@Api("/workOrder")
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
		BaseOutput<List<Department>> output = this.deptRpc.listByDepartment(department);
		if (!output.isSuccess()) {
			return null;
		}
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

	@ApiOperation("跳转到WorkOrder页面")
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
		return "workOrder/update";
	}

	@ResponseBody
	@PostMapping("/saveOrUpdate")
	public BaseOutput<Object> saveOrUpdate(WorkOrderUpdateDto dto, Long[] copyUserIds, MultipartFile attachment) {
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
			this.workOrderService.saveOrUpdate(dto);
			Map<Object, Object> viewModel = this.workOrderService.getViewModel(dto.getId());
			return BaseOutput.success().setData(viewModel);
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ResponseBody
	@PostMapping("/saveAndSubmit")
	public BaseOutput<Object> saveAndSubmit(WorkOrderUpdateDto dto, Long[] copyUserIds, MultipartFile attachment) {
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
			this.workOrderService.saveAndSubmit(dto);
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

	@ApiOperation(value = "查询WorkOrder", notes = "查询WorkOrder，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "WorkOrder", paramType = "form", value = "WorkOrder的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<WorkOrder> list(WorkOrder workOrder) {
		return workOrderService.list(workOrder);
	}

	@ApiOperation(value = "分页查询WorkOrder", notes = "分页查询WorkOrder，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "WorkOrder", paramType = "form", value = "WorkOrder的form信息", required = false, dataType = "string") })
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
		return workOrderService.listEasyuiPageByExample(query, true).toString();
	}

	@ApiOperation("删除WorkOrder")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "WorkOrder的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		try {
			workOrderService.deleteWorkOrder(id);
			return BaseOutput.success();
		} catch (WorkOrderException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
}