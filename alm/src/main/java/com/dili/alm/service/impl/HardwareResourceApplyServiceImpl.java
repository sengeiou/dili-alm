package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.BpmcUtil;
import com.dili.alm.component.MailManager;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.BpmConsts.HardwareApplyConstant;
import com.dili.alm.dao.HardwareApplyOperationRecordMapper;
import com.dili.alm.dao.HardwareResourceApplyMapper;
import com.dili.alm.dao.HardwareResourceRequirementMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.ApproveResult;
import com.dili.alm.domain.HardwareApplyOperationRecord;
import com.dili.alm.domain.HardwareApplyOperationType;
import com.dili.alm.domain.HardwareApplyState;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.HardwareResourceRequirement;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.HardwareResourceApplyProcessDto;
import com.dili.alm.domain.dto.HardwareResourceApplyUpdateDto;
import com.dili.alm.domain.dto.HardwareResourceRequirementDto;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.HardwareResourceApplyException;
import com.dili.alm.provider.EnvironmentProvider;
import com.dili.alm.rpc.resolver.MyRuntimeRpc;
import com.dili.alm.rpc.resolver.MyTaskRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.restful.RuntimeRpc;
import com.dili.bpmc.sdk.rpc.restful.TaskRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.domain.dto.UserDepartmentRole;
import com.dili.uap.sdk.domain.dto.UserDepartmentRoleQuery;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;
import com.github.pagehelper.Page;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-20 17:22:08.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class HardwareResourceApplyServiceImpl extends BaseServiceImpl<HardwareResourceApply, Long> implements HardwareResourceApplyService {

	private final String OP_DEPT_MAIL_GROUP = "yunwei@diligrp.com";

	private String contentTemplate;
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private EnvironmentProvider envProvider;
	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;
	@Autowired
	private HardwareApplyOperationRecordMapper haorMapper;
	@Autowired
	private HardwareResourceRequirementMapper hrrMapper;
	@Value("${spring.mail.username:}")
	private String mailFrom;
	@Autowired
	private MailManager mailManager;
	@Qualifier("hardwareApplyNumberGenerator")
	@Autowired
	private NumberGenerator numberGenerator;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	RuntimeRpc runtimeRpc;
	@Autowired
	TaskRpc taskRpc;
	@Autowired
	private BpmcUtil bpmcUtil;

	@Autowired 
	private MyRuntimeRpc myRuntimeRpc;
	
	@Autowired
	private MyTaskRpc myTaskRpc;
	
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> buildApplyViewModel(HardwareResourceApply apply) {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("businessOwnerId", memberProvider);
		metadata.put("projectManagerId", memberProvider);
		metadata.put("productManagerId", memberProvider);
		metadata.put("testManagerId", memberProvider);
		metadata.put("developmentManagerId", memberProvider);
		metadata.put("applicantId", memberProvider);

		JSONObject depProvider = new JSONObject();
		depProvider.put("provider", "depProvider");
		metadata.put("applicationDepartmentId", depProvider);

		JSONObject almDateProvider = new JSONObject();
		almDateProvider.put("provider", "almDateProvider");
		metadata.put("onlineDate", almDateProvider);
		metadata.put("actualOnlineDate", almDateProvider);
		metadata.put("submitTime", almDateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("created", datetimeProvider);

		JSONObject applyStateProvider = new JSONObject();
		applyStateProvider.put("provider", "hardwareResourceApplyStateProvider");
		metadata.put("applyState", applyStateProvider);

		JSONObject filesProvider = new JSONObject();
		filesProvider.put("provider", "filesProvider");
		metadata.put("sqlFileId", filesProvider);
		metadata.put("startupScriptFileId", filesProvider);
		metadata.put("dependencySystemFileId", filesProvider);

		try {
			@SuppressWarnings("rawtypes")
			List<Map> viewModelList = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(apply));
			if (CollectionUtils.isEmpty(viewModelList)) {
				return null;
			}
			return viewModelList.get(0);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	public HardwareResourceApplyServiceImpl() {
		super();
		InputStream in = null;
		try {
			Resource res = new ClassPathResource("conf/hardwareApplyMailContentTemplate.html");
			in = res.getInputStream();
			this.contentTemplate = IOUtils.toString(in, Charset.defaultCharset().toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void deleteHardwareResourceApply(Long applyId) throws HardwareResourceApplyException {
		// 判断记录是否存在
		HardwareResourceApply old = this.getActualDao().selectByPrimaryKey(applyId);
		if (old == null) {
			throw new HardwareResourceApplyException("记录不存在");
		}

		// 判断状态
		if (!old.getApplyState().equals(HardwareApplyState.APPLING.getValue())) {
			throw new HardwareResourceApplyException("当前状态不能删除");
		}

		// 删除申请
		int rows = this.getActualDao().deleteByPrimaryKey(applyId);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("删除申请失败");
		}

		// 删除配置要求
		HardwareResourceRequirement hrrQuery = DTOUtils.newDTO(HardwareResourceRequirement.class);
		hrrQuery.setApplyId(applyId);
		this.hrrMapper.delete(hrrQuery);
	}

	public HardwareResourceApplyMapper getActualDao() {
		return (HardwareResourceApplyMapper) getDao();
	}

	@Override
	public HardwareResourceApply getDetailViewModel(Long id) {
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(id);
		List<Integer> envList = JSON.parseArray(apply.getServiceEnvironment(), Integer.class);
		StringBuilder sb = new StringBuilder();
		envList.forEach(v -> sb.append(this.envProvider.getDisplayText(v, null, null)).append(","));
		apply.aset("serviceEnvironmentText", sb.substring(0, sb.length() - 1));
		HardwareResourceRequirement dto = DTOUtils.newDTO(HardwareResourceRequirement.class);
		dto.setApplyId(id);
		List<HardwareResourceRequirement> requirementList = this.hrrMapper.select(dto);
		apply.aset("requirementList", requirementList);
		HardwareApplyOperationRecord hraQuery = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		hraQuery.setApplyId(id);
		List<HardwareApplyOperationRecord> opRecords = this.haorMapper.select(hraQuery);
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("operatorId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("operateTime", datetimeProvider);

		metadata.put("opertateResult", "operationResultProvider");

		try {
			apply.aset("opRecords", ValueProviderUtils.buildDataByProvider(metadata, opRecords));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return apply;
	}

	@Transactional(readOnly = true)
	@Override
	public EasyuiPageOutput listEasyuiPageByExample(HardwareResourceApply domain, boolean useProvider) throws Exception {

		List<HardwareResourceApply> list = listByExample(domain);
		list.forEach(h -> {
			h.aset("envList", this.parseEnvToString(h.getServiceEnvironment()));
			Object dd = h.getMetadata("submitTime");
			this.setOperationColumn(h);
		});
		// 流程参数设置
		List<HardwareResourceApplyProcessDto> targetList = DTOUtils.as(list, HardwareResourceApplyProcessDto.class);
		this.bpmcUtil.fitLoggedUserIsCanHandledProcess(targetList);
		this.setOperationColumnList(targetList);
		long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
		List results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, targetList) : targetList;
		return new EasyuiPageOutput(total, results);
	}

	@Override
	public List<HardwareResourceRequirement> listRequirement(Long applyId) throws HardwareResourceApplyException {
		HardwareResourceRequirement dto = DTOUtils.newDTO(HardwareResourceRequirement.class);
		dto.setApplyId(applyId);
		return this.hrrMapper.select(dto);

	}

	@Override
	public void operationManagerApprove(Long applyId, Long operationManagerId, Set<Long> executors, String description) throws HardwareResourceApplyException {
		// 判断记录是否存在
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new HardwareResourceApplyException("记录不存在");
		}

		// 判断状态
		if (!apply.getApplyState().equals(HardwareApplyState.OPERATION_MANAGER_APPROVING.getValue())) {
			throw new HardwareResourceApplyException("当前状态运维部经理不能审批");
		}

		// 检查是否是运维部经理
		User operationManager = this.queryOperationManager();
		if (!operationManager.getId().equals(operationManagerId)) {
			throw new HardwareResourceApplyException("当前状态运维部经理不能审批");
		}

		// 检查实施人是否是运维部员工
		if (CollectionUtils.isEmpty(executors)) {
			throw new HardwareResourceApplyException("执行人不能为空");
		}
		if (executors.size() > 2) {
			throw new HardwareResourceApplyException("最多只能分配2个执行人");
		}
		for (Long value : executors) {
			UserDepartmentRoleQuery dto = new UserDepartmentRoleQuery();
			dto.setUserId(value);
			BaseOutput<List<UserDepartmentRole>> output = this.userRpc.findUserContainDepartmentAndRole(dto);
			if (!output.isSuccess()) {
				throw new HardwareResourceApplyException("查询执行人信息失败");
			}
			if (CollectionUtils.isEmpty(output.getData())) {
				throw new HardwareResourceApplyException("执行人不存在");
			}
			if (!AlmConstants.OPERATION_DEPARTMENT_CODE.equals(output.getData().get(0).getDepartment().getCode())) {
				throw new HardwareResourceApplyException("执行人不是运维部员工");
			}
		}
		apply.setExecutors(JSONObject.toJSONString(executors));
		// 更新状态
		apply.setApplyState(HardwareApplyState.IMPLEMENTING.getValue());
		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("更新申请状态失败");
		}

		// 插入申请操作记录
		HardwareApplyOperationRecord record = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		record.setApplyId(applyId);
		record.setDescription(description);
		record.setOperationName(HardwareApplyOperationType.OPERATION_MANAGER.getName());
		record.setOperationType(HardwareApplyOperationType.OPERATION_MANAGER.getValue());
		record.setOperatorId(operationManagerId);
		record.setOpertateResult(ApproveResult.APPROVED.getValue());
		rows = this.haorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("插入操作记录失败");
		}
		// 发邮件给执行人
		Set<String> emails = new HashSet<>(executors.size());
		executors.forEach(e -> emails.add(AlmCache.getInstance().getUserMap().get(e).getEmail()));
		this.sendMail(apply, "IT资源申请", emails);
	}

	public void operatorManagerTask(Long applyId) throws HardwareResourceApplyException {

		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(applyId);

		String valProcess = apply.getId().toString();
		// 根据业务号查询任务
		TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
		// taskDto.setProcessInstanceBusinessKey(valProcess);
		taskDto.setProcessDefinitionId(apply.getProcessDefinitionId());
		taskDto.setProcessInstanceId(apply.getProcessInstanceId());
		BaseOutput<List<TaskMapping>> outputList = taskRpc.list(taskDto);
		if (!outputList.isSuccess()) {
			throw new HardwareResourceApplyException("用户错误！" + outputList.getMessage());
		}

		List<TaskMapping> taskMappings = outputList.getData();
		// 获取formKey
		TaskMapping selected = taskMappings.get(0);
		/** 个人信息 **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		// 先签收
		BaseOutput out = taskRpc.claim(selected.getId(), userTicket.getId() + "");
		if (!out.isSuccess()) {
			throw new HardwareResourceApplyException("用户错误！" + out.getMessage());
		}

		out = this.submitApprove(apply.getId(), selected.getId(), "");
		if (!out.isSuccess()) {
			throw new HardwareResourceApplyException("用户错误！" + out.getMessage());
		}
	}

	@Override
	public void operatorManagerLog(Long applyId, Long operationManagerId, Set<Long> executors, String description) throws HardwareResourceApplyException {
		// 保存信息操作
		this.operationManagerApprove(applyId, operationManagerId, executors, description);
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		// 流程里的操作
		this.operatorManagerTask(applyId);

	}

	@Override
	public void operatorExecute(Long applyId, Long executorId, String description) throws HardwareResourceApplyException {
		// 判断记录是否存在
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new HardwareResourceApplyException("记录不存在");
		}

		// 判断状态
		if (!apply.getApplyState().equals(HardwareApplyState.IMPLEMENTING.getValue())) {
			throw new HardwareResourceApplyException("当前状态不能执行该操作");
		}

		// 检查是否是执行人操作
		if (StringUtils.isBlank(apply.getExecutors())) {
			throw new HardwareResourceApplyException("未分配执行人");
		}
		List<Long> executors = JSONObject.parseObject(apply.getExecutors(), new TypeReference<List<Long>>() {
		});
		if (!executors.contains(executorId)) {
			throw new HardwareResourceApplyException("当前用户没有权限执行该操作");
		}

		// 更新状态
		apply.setApplyState(HardwareApplyState.COMPLETED.getValue());
		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("更新申请状态失败");
		}
		String valProcess = apply.getId().toString();

		// 根据业务号查询任务
		TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
		// taskDto.setProcessInstanceBusinessKey(valProcess);
		taskDto.setProcessDefinitionId(apply.getProcessDefinitionId());
		taskDto.setProcessInstanceId(apply.getProcessInstanceId());
		BaseOutput<List<TaskMapping>> outputList = taskRpc.list(taskDto);
		if (!outputList.isSuccess()) {
			throw new HardwareResourceApplyException("用户错误！" + outputList.getMessage());
		}

		List<TaskMapping> taskMappings = outputList.getData();
		// 获取formKey
		TaskMapping selected = taskMappings.get(0);

		BaseOutput out = this.submitApprove(applyId, selected.getId(), executorId.toString());
		if (!out.isSuccess()) {
			throw new HardwareResourceApplyException("用户错误！" + out.getMessage());
		}
		// 插入申请操作记录
		HardwareApplyOperationRecord record = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		record.setApplyId(applyId);
		record.setDescription(description);
		record.setOperationName(HardwareApplyOperationType.EXECUTOR.getName());
		record.setOperationType(HardwareApplyOperationType.EXECUTOR.getValue());
		record.setOperatorId(executorId);
		record.setOpertateResult(ApproveResult.APPROVED.getValue());
		rows = this.haorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("插入操作记录失败");
		}
		// 发邮件给申请人
		// 查询数据字典配置
		User user = AlmCache.getInstance().getUserMap().get(apply.getApplicantId());
		if (user == null) {
			throw new HardwareResourceApplyException("申请人不存在");
		}
		this.sendMail(apply, "IT资源申请", Arrays.asList(user.getEmail()));
	}

	@Override
	public void projectManagerApprove(Long applyId, Long projectManagerId, ApproveResult result, String description) throws HardwareResourceApplyException {
		// 判断记录是否存在
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new HardwareResourceApplyException("记录不存在");
		}

		// 判断状态
		if (!apply.getApplyState().equals(HardwareApplyState.PROJECT_MANAGER_APPROVING.getValue())) {
			throw new HardwareResourceApplyException("当前状态项目经理不能审批");
		}

		// 检查是否是项目经理
		if (!apply.getProjectManagerId().equals(projectManagerId)) {
			throw new HardwareResourceApplyException("当前用户没有权限执行该操作");
		}
		String valProcess = apply.getId().toString();

		// 根据业务号查询任务
		TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
		// taskDto.setProcessInstanceBusinessKey(valProcess);
		taskDto.setProcessDefinitionId(apply.getProcessDefinitionId());
		taskDto.setProcessInstanceId(apply.getProcessInstanceId());
		BaseOutput<List<TaskMapping>> outputList = taskRpc.list(taskDto);
		if (!outputList.isSuccess()) {
			throw new HardwareResourceApplyException("用户错误！" + outputList.getMessage());
		}

		List<TaskMapping> taskMappings = outputList.getData();
		// 获取formKey
		TaskMapping selected = taskMappings.get(0);

		String taskId = selected.getId();
		// 获取任务
		if (ApproveResult.APPROVED.equals(result)) {
			// 完成流程操作
			BaseOutput outPut = this.submitApprove(apply.getId(), taskId, "");
			if (!outPut.getCode().equals("200")) {
				throw new HardwareResourceApplyException(outPut.getMessage());
			}
			;
			// 审批通过流转到运维部经理审批
			apply.setApplyState(HardwareApplyState.OPERATION_MANAGER_APPROVING.getValue());
		} else if (ApproveResult.FAILED.equals(result)) {
			// 完成流程操作
			BaseOutput outPut = this.rejectApprove(apply.getId(), taskId);
			if (!outPut.getCode().equals("200")) {
				throw new HardwareResourceApplyException("信息不全，流程无法启动");
			}
			;
			// 未通过审批退回到编辑状态让用户重新编辑
			/*
			 * apply.setSubmitTime(null);
			 * apply.setApplyState(HardwareApplyState.APPLING.getValue());
			 */
		} else {
			throw new IllegalArgumentException("未知的审批结果");
		}
		int rows = this.getActualDao().updateByPrimaryKey(apply);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("更新申请状态失败");
		}

		// 插入申请操作记录
		HardwareApplyOperationRecord record = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		record.setApplyId(applyId);
		record.setDescription(description);
		record.setOperationName(HardwareApplyOperationType.PROJECT_MANAGER.getName());
		record.setOperationType(HardwareApplyOperationType.PROJECT_MANAGER.getValue());
		record.setOperatorId(projectManagerId);
		record.setOpertateResult(result.getValue());
		rows = this.haorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("插入操作记录失败");
		}
		if (ApproveResult.APPROVED.equals(result)) {
			// 发邮件给研发中心总经理
			// 查询数据字典配置
			DataDictionaryDto ddDto = this.ddService.findByCode(AlmConstants.DEPARTMENT_MANAGER_ROLE_CONFIG_CODE);
			if (ddDto == null) {
				throw new HardwareResourceApplyException("请先配置部门经理数据字典");
			}
			User user = this.queryOperationManager();
			this.sendMail(apply, "IT资源申请", Arrays.asList(user.getEmail()));
		}
	}

	@Override
	public void saveAndSubmit(HardwareResourceApplyUpdateDto hardwareResourceApply) throws HardwareResourceApplyException {
		this.saveOrUpdate(hardwareResourceApply);
		this.submit(hardwareResourceApply.getId());
	}

	@Override
	public void saveOrUpdate(HardwareResourceApplyUpdateDto dto) throws HardwareResourceApplyException {
		if (dto.getId() == null) {
			this.insertHardwareApply(dto);
		} else {
			this.updateHardwareApply(dto);
		}
	}

	@Override
	public void submit(Long applyId) throws HardwareResourceApplyException {
		// 判断记录是否存在
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new HardwareResourceApplyException("记录不存在");
		}

		// 判断状态
		if (!apply.getApplyState().equals(HardwareApplyState.APPLING.getValue())) {
			throw new HardwareResourceApplyException("当前状态不能提交");
		}

		apply.setSubmitTime(new Date());
		apply.setApplyState(HardwareApplyState.PROJECT_MANAGER_APPROVING.getValue());
		/***
		 * 
		 * 启动流程相关begin，没有流程参数启动流程，有流程参数为重新提交
		 * 
		 */
		if (apply.getProcessDefinitionId() == null) {
			/** 个人信息 **/
			// TODO:启动流程
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			// 流程启动参数设置
			Map<String, Object> variables = new HashMap<>(2);
			variables.put(HardwareApplyConstant.HARDWARE_APPLY_CODE.getName(), apply.getId().toString());
			variables.put("AssignExecutorId", apply.getProjectManagerId().toString());
			// 启动流程
			BaseOutput<ProcessInstanceMapping> processInstanceOutput = myRuntimeRpc.startProcessInstanceByKey(HardwareApplyConstant.PROCESS_DEFINITION_KEY.getName(), apply.getId() + "",
					userTicket.getId().toString(), variables);
			if (!processInstanceOutput.isSuccess()) {
				throw new HardwareResourceApplyException("开启审批错误" + processInstanceOutput.getMessage());
			}
			// 回调，写入相关流程任务数据
			ProcessInstanceMapping processInstance = processInstanceOutput.getData();
			apply.setProcessDefinitionId(processInstance.getProcessDefinitionId());
			apply.setProcessInstanceId(processInstance.getProcessInstanceId());

		} else {
			// 根据业务号查询任务
			TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
			// taskDto.setProcessInstanceBusinessKey(apply.getId().toString());
			taskDto.setProcessDefinitionId(apply.getProcessDefinitionId());
			taskDto.setProcessInstanceId(apply.getProcessInstanceId());
			BaseOutput<List<TaskMapping>> outputList = taskRpc.list(taskDto);
			if (!outputList.isSuccess()) {
				throw new HardwareResourceApplyException("用户错误！" + outputList.getMessage());
			}

			List<TaskMapping> taskMappings = outputList.getData();
			// 获取formKey
			TaskMapping selected = taskMappings.get(0);

			String taskId = selected.getId();

			this.submitApprove(apply.getId(), taskId, apply.getProjectManagerId().toString());
		}

		/***
		 * 
		 * 启动流程相关end
		 * 
		 */

		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("更新申请状态失败");
		}

		// 发邮件给项目经理,研发部总经理，运维部经理，研发经理，申请人
		List<String> to = new ArrayList<>(5);
		// 查询项目经理
		User projectManager = AlmCache.getInstance().getUserMap().get(apply.getProjectManagerId());
		if (projectManager == null) {
			throw new HardwareResourceApplyException("项目经理不存在");
		}
		to.add(projectManager.getEmail());
		// 查询运维部经理
		to.add(this.queryOperationManager().getEmail());
		// 查询研发经理
		to.add(this.queryProjectDevelopmentManager(apply).getEmail());
		// 查询申请人
		User applicant = AlmCache.getInstance().getUserMap().get(apply.getApplicantId());
		if (applicant == null) {
			throw new HardwareResourceApplyException("申请人不存在");
		}
		// 发送邮件
		this.sendMail(apply, "IT资源申请", to);
	}

	@SuppressWarnings("rawtypes")
	private List<Map> buildOperationRecordViewModel(List<HardwareApplyOperationRecord> list) {
		Map<Object, Object> metadata = new HashMap<>();
		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("operatorId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("operateTime", datetimeProvider);
		try {
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	private void insertHardwareApply(HardwareResourceApplyUpdateDto dto) throws HardwareResourceApplyException {
		// 判断申请日期
		if (dto.getApplyDate() == null) {
			throw new HardwareResourceApplyException("申请日期不能为空");
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (dto.getApplyDate().getTime() < calendar.getTimeInMillis()) {
			throw new HardwareResourceApplyException("申请上线日期不能小于当前日期");
		}

		// 插入申请
		Project project = this.projectMapper.selectByPrimaryKey(dto.getProjectId());
		if (project == null) {
			throw new HardwareResourceApplyException("项目不存在");
		}
		dto.setSerialNumber(this.numberGenerator.get());
		dto.setProjectName(project.getName());
		dto.setProjectManagerId(project.getProjectManager());
		dto.setProjectSerialNumber(project.getSerialNumber());
		dto.setServiceEnvironment(JSONObject.toJSONString(dto.getServiceEnvironments()));
		int rows = this.getActualDao().insertSelective(dto);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("新建申请失败");
		}

		// 插入配置要求
		if (CollectionUtils.isEmpty(dto.getConfigurationRequirement())) {
			throw new HardwareResourceApplyException("配置要求不能为空");
		}
		dto.getConfigurationRequirement().forEach(c -> c.setApplyId(dto.getId()));

		List<HardwareResourceRequirementDto> list = dto.getConfigurationRequirement();
		List<HardwareResourceRequirement> as = new ArrayList<HardwareResourceRequirement>(list.size());
		list.forEach(cc -> as.add(DTOUtils.switchEntityToDTO(cc, HardwareResourceRequirement.class)));

		rows = this.hrrMapper.insertList(as);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("插入配置要求失败");
		}
	}

	private Collection<String> parseEnvToString(String json) {
		List<Long> envList = JSONObject.parseObject(json, new TypeReference<List<Long>>() {
		});
		List<String> target = new ArrayList<>(envList.size());
		envList.forEach(e -> target.add(this.envProvider.getDisplayText(e, null, null)));
		return target;
	}

	private User queryOperationManager() throws HardwareResourceApplyException {
		// 查询数据字典配置
		DataDictionaryDto ddDto = this.ddService.findByCode(AlmConstants.DEPARTMENT_MANAGER_ROLE_CONFIG_CODE);
		if (ddDto == null) {
			throw new HardwareResourceApplyException("请先配置部门经理数据字典");
		}
		DataDictionaryValueDto ddValue = ddDto.getValues().stream().filter(v -> v.getCode().equals(AlmConstants.OPERATION_MANAGER_CODE)).findFirst().orElse(null);
		if (ddValue == null) {
			throw new HardwareResourceApplyException("请先配置运维部经理数据字典");
		}
		Map.Entry<Long, User> entry = AlmCache.getInstance().getUserMap().entrySet().stream().filter(e -> e.getValue().getUserName().equals(ddValue.getValue())).findFirst().orElse(null);
		if (entry == null) {
			throw new HardwareResourceApplyException("运维部经理账号不存在，请确认数据字典配置正确");
		}
		return entry.getValue();
	}

	private User queryProjectDevelopmentManager(HardwareResourceApply apply) throws HardwareResourceApplyException {
		Project project = this.projectMapper.selectByPrimaryKey(apply.getProjectId());
		if (project == null) {
			throw new HardwareResourceApplyException("项目不存在");
		}
		User user = AlmCache.getInstance().getUserMap().get(project.getDevelopManager());
		if (user == null) {
			throw new HardwareResourceApplyException("项目研发经理不存在");
		}
		return user;
	}

	private void sendMail(HardwareResourceApply apply, String subject, Collection<String> to) {

		// 构建邮件内容
		Template template = this.groupTemplate.getTemplate(this.contentTemplate);
		HardwareApplyOperationRecord poor = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		poor.setApplyId(apply.getId());
		List<HardwareApplyOperationRecord> list = this.haorMapper.select(poor);
		HardwareResourceRequirement hrrQuery = DTOUtils.newDTO(HardwareResourceRequirement.class);
		hrrQuery.setApplyId(apply.getId());
		List<HardwareResourceRequirement> hrrList = this.hrrMapper.select(hrrQuery);
		apply.aset("hrrList", hrrList);
		apply.aset("envList", this.parseEnvToString(apply.getServiceEnvironment()));
		template.binding("apply", buildApplyViewModel(apply));
		String content = template.render();

		try {
			this.mailManager.sendMail(this.mailFrom, OP_DEPT_MAIL_GROUP, content, true, subject, null);
		} catch (MessagingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		to.forEach(t -> {
			// 发送
			try {
				this.mailManager.sendMail(this.mailFrom, t, content, true, subject, null);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		});
	}

	private void setOperationColumn(HardwareResourceApply apply) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			throw new IllegalArgumentException("用户未登录");
		}

		// 判断界面上用户是否是提交人
		boolean approving = apply.getApplyState().equals(HardwareApplyState.APPLING.getValue());
		// 判断当前登录用户是否是项目经理
		approving = !approving ? approving : user.getId().equals(apply.getApplicantId());
		apply.aset("approving", approving);

		// 判断界面上用户是否可以点击项目经理确认按钮
		// 判断申请状态是否是项目经理确认状态
		boolean projectManagerApprov = apply.getApplyState().equals(HardwareApplyState.PROJECT_MANAGER_APPROVING.getValue());
		// 判断当前登录用户是否是项目经理
		projectManagerApprov = !projectManagerApprov ? projectManagerApprov : user.getId().equals(apply.getProjectManagerId());
		apply.aset("projectManagerApprov", projectManagerApprov);

		try {

			// 判断界面上运维部经理审批
			boolean operactionManagerAppov = apply.getApplyState().equals(HardwareApplyState.OPERATION_MANAGER_APPROVING.getValue());
			User operactionManagerU = this.queryOperationManager();
			// 判断当前登录用户是否是部总经理
			operactionManagerAppov = !operactionManagerAppov ? operactionManagerAppov : operactionManagerU.getId().equals(user.getId());
			apply.aset("operactionManagerAppov", operactionManagerAppov);

			// 实施
			boolean implementing = apply.getApplyState().equals(HardwareApplyState.IMPLEMENTING.getValue());

			if (implementing) {
				List<Long> executors = JSONObject.parseObject(apply.getExecutors(), new TypeReference<List<Long>>() {
				});
				implementing = executors.contains(user.getId());
			}

			apply.aset("implementing", implementing);

		} catch (HardwareResourceApplyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 判断每个节点
	private void setOperationColumnList(List<HardwareResourceApplyProcessDto> dtos) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			throw new IllegalArgumentException("用户未登录");
		}

		dtos.forEach(apply -> {
			// 判断界面上用户是否是提交人
			boolean approving = apply.getApplyState().equals(HardwareApplyState.APPLING.getValue());
			// 判断当前登录用户是否是项目经理
			approving = !approving ? approving : user.getId().equals(apply.getApplicantId());
			apply.setApproving(approving);

			// 判断界面上用户是否可以点击项目经理确认按钮
			// 判断申请状态是否是项目经理确认状态
			boolean projectManagerApprov = apply.getApplyState().equals(HardwareApplyState.PROJECT_MANAGER_APPROVING.getValue());
			// 判断当前登录用户是否是项目经理
			projectManagerApprov = !projectManagerApprov ? projectManagerApprov : user.getId().equals(apply.getProjectManagerId());
			apply.setProjectManagerApprov(projectManagerApprov);

			try {

				// 判断界面上运维部经理审批
				boolean operactionManagerAppov = apply.getApplyState().equals(HardwareApplyState.OPERATION_MANAGER_APPROVING.getValue());
				User operactionManagerU = this.queryOperationManager();
				// 判断当前登录用户是否是部总经理
				operactionManagerAppov = !operactionManagerAppov ? operactionManagerAppov : operactionManagerU.getId().equals(user.getId());
				apply.setOperactionManagerAppov(operactionManagerAppov);
				// 实施
				boolean implementing = apply.getApplyState().equals(HardwareApplyState.IMPLEMENTING.getValue());

				if (implementing) {
					List<Long> executors = JSONObject.parseObject(apply.getExecutors(), new TypeReference<List<Long>>() {
					});
					implementing = executors.contains(user.getId());
				}
				apply.setImplementing(implementing);

			} catch (HardwareResourceApplyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<Long> envList = JSONObject.parseObject(apply.getServiceEnvironment(), new TypeReference<List<Long>>() {
			});
			List<String> target = new ArrayList<>(envList.size());
			envList.forEach(e -> target.add(this.envProvider.getDisplayText(e, null, null)));
			apply.setEnvList(target);
		});

	}

	private void updateHardwareApply(HardwareResourceApplyUpdateDto dto) throws HardwareResourceApplyException {
		// 判断申请日期
		if (dto.getApplyDate() == null) {
			throw new HardwareResourceApplyException("申请日期不能为空");
		}
		/*
		 * Calendar calendar = Calendar.getInstance(); calendar.setTime(new Date());
		 * calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0);
		 * calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0); if
		 * (dto.getApplyDate().getTime() < calendar.getTimeInMillis()) { throw new
		 * HardwareResourceApplyException("申请上线日期不能小于当前日期"); }
		 */

		// 判断记录是否存在
		HardwareResourceApply old = this.getActualDao().selectByPrimaryKey(dto.getId());
		if (old == null) {
			throw new HardwareResourceApplyException("记录不存在");
		}

		// 判断状态
		if (!old.getApplyState().equals(HardwareApplyState.APPLING.getValue())) {
			throw new HardwareResourceApplyException("当前状态不能编辑");
		}

		// 更新申请
		Project project = this.projectMapper.selectByPrimaryKey(dto.getProjectId());
		if (project == null) {
			throw new HardwareResourceApplyException("项目不存在");
		}
		dto.setProjectName(project.getName());
		dto.setProjectManagerId(project.getProjectManager());
		dto.setProjectSerialNumber(project.getSerialNumber());
		dto.setServiceEnvironment(JSONObject.toJSONString(dto.getServiceEnvironments()));
		int rows = this.getActualDao().updateByPrimaryKeySelective(dto);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("修改申请失败");
		}

		// 更新配置要求
		// 先删除
		HardwareResourceRequirement hrrQuery = DTOUtils.newDTO(HardwareResourceRequirement.class);
		hrrQuery.setApplyId(dto.getId());
		this.hrrMapper.delete(hrrQuery);

		// 再插入
		if (CollectionUtils.isEmpty(dto.getConfigurationRequirement())) {
			throw new HardwareResourceApplyException("配置要求不能为空");
		}
		dto.getConfigurationRequirement().forEach(c -> c.setApplyId(dto.getId()));
		List<HardwareResourceRequirementDto> configurationRequirement = dto.getConfigurationRequirement();
		List<HardwareResourceRequirement> as = new ArrayList<HardwareResourceRequirement>(configurationRequirement.size());
		configurationRequirement.forEach(cc -> as.add(DTOUtils.switchEntityToDTO(cc, HardwareResourceRequirement.class)));

		rows = this.hrrMapper.insertList(as);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("插入配置要求失败");
		}
	}

	@Override
	public BaseOutput submitApprove(Long id, String taskId, String assignExecutorId) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("approved", "true");
		if (!assignExecutorId.equals("")) {
			variables.put("AssignExecutorId", assignExecutorId);
		}
		return myTaskRpc.complete(taskId, variables);
	}

	@Override
	public BaseOutput rejectApprove(Long id, String taskId) {
		Map<String, Object> variables = new HashMap<>();
		// 判断记录是否存在
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(id);
		// 未通过审批退回到编辑状态让用户重新编辑
		apply.setSubmitTime(null);
		apply.setApplyState(HardwareApplyState.APPLING.getValue());
		this.update(apply);
		variables.put("approved", "false");
		variables.put("AssignExecutorId", apply.getApplicantId().toString());
		return myTaskRpc.complete(taskId, variables);
	}

	@Override
	public void projectManagerApproveForTask(Long applyId, Long projectManagerId, ApproveResult result, String description) throws HardwareResourceApplyException {
		// 判断记录是否存在
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new HardwareResourceApplyException("记录不存在");
		}
		// 审批通过流转到运维部经理审批
		apply.setApplyState(HardwareApplyState.OPERATION_MANAGER_APPROVING.getValue());
		int rows = this.getActualDao().updateByPrimaryKey(apply);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("更新申请状态失败");
		}
		// 插入申请操作记录
		HardwareApplyOperationRecord record = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		record.setApplyId(applyId);
		record.setDescription(description);
		record.setOperationName(HardwareApplyOperationType.PROJECT_MANAGER.getName());
		record.setOperationType(HardwareApplyOperationType.PROJECT_MANAGER.getValue());
		record.setOperatorId(projectManagerId);
		record.setOpertateResult(result.getValue());
		rows = this.haorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("插入操作记录失败");
		}
		// 发邮件给研发中心总经理
		// 查询数据字典配置
		DataDictionaryDto ddDto = this.ddService.findByCode(AlmConstants.DEPARTMENT_MANAGER_ROLE_CONFIG_CODE);
		if (ddDto == null) {
			throw new HardwareResourceApplyException("请先配置部门经理数据字典");
		}
		User user = this.queryOperationManager();
		this.sendMail(apply, "IT资源申请", Arrays.asList(user.getEmail()));

	}

	@Override
	public void operationManagerApproveForTask(Long applyId, Long operationManagerId, Set<Long> executors, String description) throws HardwareResourceApplyException {
		// 判断记录是否存在
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new HardwareResourceApplyException("记录不存在");
		}
		// 检查实施人是否是运维部员工
		if (CollectionUtils.isEmpty(executors)) {
			throw new HardwareResourceApplyException("执行人不能为空");
		}
		if (executors.size() > 2) {
			throw new HardwareResourceApplyException("最多只能分配2个执行人");
		}

		apply.setExecutors(JSONObject.toJSONString(executors));
		// 更新状态
		apply.setApplyState(HardwareApplyState.IMPLEMENTING.getValue());
		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("更新申请状态失败");
		}

		// 插入申请操作记录
		HardwareApplyOperationRecord record = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		record.setApplyId(applyId);
		record.setDescription(description);
		record.setOperationName(HardwareApplyOperationType.OPERATION_MANAGER.getName());
		record.setOperationType(HardwareApplyOperationType.OPERATION_MANAGER.getValue());
		record.setOperatorId(operationManagerId);
		record.setOpertateResult(ApproveResult.APPROVED.getValue());
		rows = this.haorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("插入操作记录失败");
		}
		// 发邮件给执行人
		Set<String> emails = new HashSet<>(executors.size());
		executors.forEach(e -> emails.add(AlmCache.getInstance().getUserMap().get(e).getEmail()));
		this.sendMail(apply, "IT资源申请", emails);

	}

	@Override
	public void operatorExecuteForTask(Long applyId, Long executorId, String description) throws HardwareResourceApplyException {
		// 判断记录是否存在
		HardwareResourceApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		// 更新状态
		apply.setApplyState(HardwareApplyState.COMPLETED.getValue());
		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("更新申请状态失败");
		}

		// 插入申请操作记录
		HardwareApplyOperationRecord record = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		record.setApplyId(applyId);
		record.setDescription(description);
		record.setOperationName(HardwareApplyOperationType.EXECUTOR.getName());
		record.setOperationType(HardwareApplyOperationType.EXECUTOR.getValue());
		record.setOperatorId(executorId);
		record.setOpertateResult(ApproveResult.APPROVED.getValue());
		rows = this.haorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("插入操作记录失败");
		}
		// 发邮件给申请人
		// 查询数据字典配置
		User user = AlmCache.getInstance().getUserMap().get(apply.getApplicantId());
		if (user == null) {
			throw new HardwareResourceApplyException("申请人不存在");
		}
		this.sendMail(apply, "IT资源申请", Arrays.asList(user.getEmail()));

	}

}
