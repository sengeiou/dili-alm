package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.MailManager;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.dao.WorkOrderOperationRecordMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.User;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderOperationRecord;
import com.dili.alm.domain.WorkOrderOperationType;
import com.dili.alm.domain.WorkOrderSource;
import com.dili.alm.domain.WorkOrderState;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.WorkOrderUpdateDto;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.alm.provider.MemberProvider;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.WorkOrderService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.google.common.collect.Sets;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-05-23 11:51:37.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class WorkOrderServiceImpl extends BaseServiceImpl<WorkOrder, Long> implements WorkOrderService {

	private String contentTemplate;

	@Autowired
	private FilesMapper filesMapper;
	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;

	@Value("${spring.mail.username:}")
	private String mailFrom;

	@Autowired
	private MailManager mailManager;
	@Autowired
	private MemberProvider memberProvider;

	@Qualifier("workOrderNumberGenerator")
	@Autowired
	private NumberGenerator numberGenerator;
	@Autowired
	private WorkOrderOperationRecordMapper woorMapper;
	@Autowired
	private DataDictionaryService ddService;

	public static Map<Object, Object> parseViewModel(WorkOrder workOrder) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject workOrderTypeProvider = new JSONObject();
		workOrderTypeProvider.put("provider", "workOrderTypeProvider");
		metadata.put("workOrderType", workOrderTypeProvider);

		JSONObject workOrderPriorityProvider = new JSONObject();
		workOrderPriorityProvider.put("provider", "workOrderPriorityProvider");
		metadata.put("priority", workOrderPriorityProvider);

		JSONObject workOrderChannelProvider = new JSONObject();
		workOrderChannelProvider.put("provider", "workOrderChannelProvider");
		metadata.put("channel", workOrderChannelProvider);

		JSONObject workOrderSourceProvider = new JSONObject();
		workOrderSourceProvider.put("provider", "workOrderSourceProvider");
		metadata.put("workOrderSource", workOrderSourceProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("acceptorId", memberProvider);
		metadata.put("applicantId", memberProvider);
		metadata.put("executorId", memberProvider);

		JSONObject filesProvider = new JSONObject();
		filesProvider.put("provider", "filesProvider");
		metadata.put("attachmentFileId", filesProvider);

		JSONObject workOrderStateProvider = new JSONObject();
		workOrderStateProvider.put("provider", "workOrderStateProvider");
		metadata.put("workOrderState", workOrderStateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("creationTime", datetimeProvider);

		List<Map> listMap = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(workOrder));
		return listMap.get(0);

	}

	public WorkOrderServiceImpl() {
		super();
		InputStream in = null;
		try {
			Resource res = new ClassPathResource("conf/workOrderMailContentTemplate.html");
			in = res.getInputStream();
			this.contentTemplate = IOUtils.toString(in, "UTF-8");
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
	public void allocate(Long id, Long executorId, Integer workOrderType, Integer priority, OperationResult result,
			String description) throws WorkOrderException {
		// 检查工单是否存在
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}
		// 检查状态
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.OPENED.getValue())) {
			throw new WorkOrderException("当前状态不能执行分配操作");
		}
		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woor.setOperatorId(workOrder.getAcceptorId());
		woor.setOperationName(WorkOrderOperationType.ACCEPTOR.getName());
		woor.setOperationType(WorkOrderOperationType.ACCEPTOR.getValue());
		woor.setOperationResult(result.getValue());
		woor.setDescription(description);
		woor.setWorkOrderId(workOrder.getId());
		int rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}
		User mailReceiver = null;
		// 更新工单状态
		if (result.equals(OperationResult.FAILURE)) {
			workOrder.setWorkOrderState(WorkOrderState.APPLING.getValue());
			workOrder.setSubmitTime(null);
			mailReceiver = AlmCache.getInstance().getUserMap().get(workOrder.getApplicantId());
		} else {
			workOrder.setWorkOrderState(WorkOrderState.SOLVING.getValue());
			// 更新工单类型和优先级
			workOrder.setWorkOrderType(workOrderType);
			workOrder.setPriority(priority);
			// 更新执行人
			workOrder.setExecutorId(executorId);
			mailReceiver = AlmCache.getInstance().getUserMap().get(executorId);
		}
		rows = this.updateExactSimple(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		}
		// 发邮件
		if (mailReceiver == null) {
			throw new WorkOrderException("执行人不存在");
		}
		this.sendMail(workOrder, "工单执行", Sets.newHashSet(mailReceiver.getEmail()));
	}

	@Override
	public void close(Long id, Long operatorId) throws WorkOrderException {
		// 检查工单是否存在
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}
		// 检查状态
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.SOLVED.getValue())) {
			throw new WorkOrderException("当前状态不能执行分配操作");
		}
		// 验证操作人
		if (!workOrder.getApplicantId().equals(operatorId)) {
			throw new WorkOrderException("只有申请人才能关闭工单");
		}
		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woor.setOperatorId(workOrder.getApplicantId());
		woor.setOperationName(WorkOrderOperationType.CONFIRM.getName());
		woor.setOperationType(WorkOrderOperationType.CONFIRM.getValue());
		woor.setOperationResult(OperationResult.SUCCESS.getValue());
		woor.setWorkOrderId(workOrder.getId());
		int rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}
		// 更新工单
		workOrder.setCloseTime(new Date());
		workOrder.setWorkOrderState(WorkOrderState.CLOSED.getValue());
		rows = this.getActualDao().updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		}
	}

	public WorkOrderMapper getActualDao() {
		return (WorkOrderMapper) getDao();
	}

	@Override
	public WorkOrder getOperatinoRecordsViewModel(Long id) {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		List<Long> copyUserIds = JSON.parseArray(workOrder.getCopyUserId(), Long.class);
		StringBuilder sb = new StringBuilder();
		copyUserIds.forEach(cid -> sb.append(this.memberProvider.getDisplayText(cid, null, null)).append(","));
		workOrder.aset("copyUsers", sb.substring(0, sb.length() - 1));
		WorkOrderOperationRecord woorQuery = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woorQuery.setWorkOrderId(id);
		List<WorkOrderOperationRecord> opRecords = this.woorMapper.select(woorQuery);
		try {
			workOrder.aset("opRecords", parseOperationRecordViewModel(opRecords));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return workOrder;
	}

	@Override
	public Map<Object, Object> getViewModel(Long id) {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		try {
			return parseViewModel(workOrder);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void saveAndSubmit(WorkOrderUpdateDto dto) throws WorkOrderException {
		this.saveOrUpdate(dto);
		this.submit(dto.getId());
	}

	@Override
	public void saveOrUpdate(WorkOrderUpdateDto dto) throws WorkOrderException {
		if (dto.getId() == null) {
			this.insertWorkOrder(dto);
		} else {
			this.updateWorkOrder(dto);
		}
	}

	@Override
	public void solve(Long id, Integer taskHours, Integer overtimeHours, String workContent) throws WorkOrderException {
		// 检查工单是否存在
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}
		// 检查状态
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.SOLVING.getValue())) {
			throw new WorkOrderException("当前状态不能执行分配操作");
		}
		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		if (workOrder.getWorkOrderSource().equals(WorkOrderSource.DEPARTMENT.getValue())) {
			woor.setOperatorId(workOrder.getExecutorId());
		} else if (workOrder.getWorkOrderSource().equals(WorkOrderSource.OUTSIDE.getValue())) {
			woor.setOperatorId(workOrder.getAcceptorId());
		} else {
			throw new WorkOrderException("未知的工单来源");
		}
		woor.setOperationName(WorkOrderOperationType.EXECUTOR.getName());
		woor.setOperationType(WorkOrderOperationType.EXECUTOR.getValue());
		woor.setOperationResult(OperationResult.SUCCESS.getValue());
		woor.setDescription(workContent);
		woor.setWorkOrderId(workOrder.getId());
		int rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}
		// 更新工单
		workOrder.setTaskHours(taskHours);
		workOrder.setOvertimeHours(overtimeHours);
		workOrder.setWorkOrderState(WorkOrderState.SOLVED.getValue());
		rows = this.getActualDao().updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		}
		// 发邮件
		User applicant = AlmCache.getInstance().getUserMap().get(workOrder.getApplicantId());
		if (applicant == null) {
			throw new WorkOrderException("执行人不存在");
		}
		this.sendMail(workOrder, "工单执行", Sets.newHashSet(applicant.getEmail()));
	}

	private void insertWorkOrder(WorkOrderUpdateDto dto) throws WorkOrderException {
		if (CollectionUtils.isNotEmpty(dto.getCopyUserIds())) {
			dto.setCopyUserId(JSON.toJSONString(dto.getCopyUserIds()));
		}
		int rows = this.getActualDao().insertSelective(dto);
		if (rows <= 0) {
			throw new WorkOrderException("新增工单失败");
		}
	}

	private List<Map> parseOperationRecordViewModel(List<WorkOrderOperationRecord> opRecords) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("operatorId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("operationTime", datetimeProvider);

		return ValueProviderUtils.buildDataByProvider(metadata, opRecords);
	}

	private void sendMail(WorkOrder workOrder, String subject, Set<String> emails) {

		// 构建邮件内容
		List<Files> attachements = null;
		if (workOrder.getAttachmentFileId() != null) {
			Files files = this.filesMapper.selectByPrimaryKey(workOrder.getAttachmentFileId());
			attachements = Arrays.asList(files);
		}
		String[] cc = null;
		if (StringUtils.isNotBlank(workOrder.getCopyUserId())) {
			List<Long> copyUserIds = JSON.parseArray(workOrder.getCopyUserId(), Long.class);
			List<String> ccList = new ArrayList<>(copyUserIds.size());
			copyUserIds.forEach(uid -> {
				User user = AlmCache.getInstance().getUserMap().get(uid);
				if (user == null) {
					LOGGER.error("用户缓存数据中未找到抄送人，尝试清空缓存数据后再试");
					return;
				}
				ccList.add(user.getEmail());
			});
			cc = ccList.toArray(new String[] {});
		}
		workOrder = this.getOperatinoRecordsViewModel(workOrder.getId());
		Template template = this.groupTemplate.getTemplate(this.contentTemplate);
		try {
			template.binding("model", parseViewModel(workOrder));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return;
		}
		template.binding("opRecords", workOrder.aget("opRecords"));

		// 发送
		for (String to : emails) {
			try {
				this.mailManager.sendRemoteAttachementMail(this.mailFrom, to, cc, template.render(), true, subject,
						attachements);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private void submit(Long id) throws WorkOrderException {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		User mailReceiver = null;
		if (WorkOrderSource.DEPARTMENT.getValue().equals(workOrder.getWorkOrderSource())) {
			// 部门工单，直接跳转到工单解决
			workOrder.setWorkOrderState(WorkOrderState.SOLVING.getValue());
			mailReceiver = AlmCache.getInstance().getUserMap().get(workOrder.getExecutorId());
		} else if (WorkOrderSource.OUTSIDE.getValue().equals(workOrder.getWorkOrderSource())) {
			// 外部工单
			workOrder.setWorkOrderState(WorkOrderState.OPENED.getValue());
			mailReceiver = AlmCache.getInstance().getUserMap().get(workOrder.getAcceptorId());
		}
		workOrder.setSubmitTime(new Date());
		int rows = this.getActualDao().updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("提交工单失败");
		}
		if (mailReceiver == null) {
			throw new WorkOrderException("受理人不存在");
		}
		this.sendMail(workOrder, "工单申请", Sets.newHashSet(mailReceiver.getEmail()));
	}

	private void updateWorkOrder(WorkOrderUpdateDto dto) throws WorkOrderException {
		if (CollectionUtils.isNotEmpty(dto.getCopyUserIds())) {
			dto.setCopyUserId(JSON.toJSONString(dto.getCopyUserIds()));
		}

		if (dto.getWorkOrderSource().equals(WorkOrderSource.DEPARTMENT.getValue())) {
			dto.setAcceptorId(null);
		} else if (dto.getWorkOrderSource().equals(WorkOrderSource.OUTSIDE.getValue())) {
			dto.setExecutorId(null);
		}
		int rows = super.updateExactSimple(dto);
		if (rows <= 0) {
			throw new WorkOrderException("新增工单失败");
		}
	}

	@Override
	public List<User> getReceivers() {
		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.WORK_ORDER_RECEIVERS_CODE);
		if (dd == null || CollectionUtils.isEmpty(dd.getValues())) {
			return null;
		}
		List<User> target = new ArrayList<>(dd.getValues().size());
		dd.getValues().forEach(v -> {
			Map.Entry<Long, User> entry = AlmCache.getInstance().getUserMap().entrySet().stream()
					.filter(e -> e.getValue().getUserName().equals(v.getValue())).findFirst().orElse(null);
			if (entry != null) {
				target.add(entry.getValue());
			}
		});
		return target;
	}
}