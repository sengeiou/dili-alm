package com.dili.alm.manager.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.MailManager;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.WorkOrderExecutionRecordMapper;
import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.dao.WorkOrderOperationRecordMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.User;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderExecutionRecord;
import com.dili.alm.domain.WorkOrderOperationRecord;
import com.dili.alm.domain.WorkOrderOperationType;
import com.dili.alm.domain.WorkOrderState;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.alm.manager.WorkOrderManager;
import com.dili.alm.provider.MemberProvider;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.WorkOrderService;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.dto.IMybatisForceParams;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.DateUtils;
import com.dili.ss.util.POJOUtils;
import com.google.common.collect.Sets;

public abstract class BaseWorkOrderManager implements WorkOrderManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderManager.class);

	@Autowired
	private WorkOrderMapper workOrderMapper;
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

	@Autowired
	private WorkOrderExecutionRecordMapper woerMapper;

	public BaseWorkOrderManager() {
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
	public void add(WorkOrder workOrder) throws WorkOrderException {
		int rows = this.workOrderMapper.insertSelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("新增工单失败");
		}
	}

	@Override
	public void allocate(WorkOrder workOrder, Long executorId, Integer workOrderType, Integer priority,
			OperationResult result, String description) throws WorkOrderException {
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
		// 更新工单状态
		User mailReceiver = null;
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
		workOrder.setModifyTime(new Date());
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
	public void close(WorkOrder workOrder, Long operatorId, OperationResult result, String description)
			throws WorkOrderException {
		// 检查状态
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.SOLVED.getValue())) {
			throw new WorkOrderException("当前状态不能执行分配操作");
		}
		// 验证操作人
		if (!workOrder.getApplicantId().equals(operatorId)) {
			throw new WorkOrderException("只有申请人才能关闭工单");
		}
		if (OperationResult.SUCCESS.equals(result)) {
			workOrder.setCloseTime(new Date());
			workOrder.setWorkOrderState(WorkOrderState.CLOSED.getValue());
		} else if (OperationResult.FAILURE.equals(result)) {
			workOrder.setWorkOrderState(WorkOrderState.SOLVING.getValue());

		}
		// 更新工单
		workOrder.setModifyTime(new Date());
		int rows = this.workOrderMapper.updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		}
		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woor.setOperatorId(workOrder.getApplicantId());
		woor.setOperationName(WorkOrderOperationType.CONFIRM.getName());
		woor.setOperationType(WorkOrderOperationType.CONFIRM.getValue());
		woor.setOperationResult(result.getValue());
		woor.setDescription(description);
		woor.setWorkOrderId(workOrder.getId());
		rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}
	}

	@Override
	public List<User> getReceivers() {
		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.OUTSIDE_WORK_ORDER_RECEIVERS_CODE);
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

	@Override
	public void solve(WorkOrder workOrder, Date startDate, Date endDate, Integer taskHours, Integer overtimeHours,
			String workContent) throws WorkOrderException {
		// 检查状态
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.SOLVING.getValue())) {
			throw new WorkOrderException("当前状态不能执行分配操作");
		}

		// 判断工时
		int days = DateUtils.differentDays(startDate, endDate) + 1;
		if (taskHours > days * 8) {
			throw new WorkOrderException("常规工时超过最大值，每日常规工时最多为8小时！");
		}

		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woor.setOperationType(WorkOrderOperationType.EXECUTOR.getValue());
		woor.setOperationName(WorkOrderOperationType.EXECUTOR.getName());
		woor.setOperationResult(OperationResult.SUCCESS.getValue());
		woor.setOperatorId(workOrder.getExecutorId());
		woor.setDescription(workContent);
		woor.setWorkOrderId(workOrder.getId());
		int rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}

		// 插入工单执行记录
		WorkOrderExecutionRecord woer = DTOUtils.newDTO(WorkOrderExecutionRecord.class);
		woer.setWorkOrderId(workOrder.getId());
		woer.setStartDate(startDate);
		woer.setEndDate(endDate);
		woer.setTaskHours(taskHours);
		woer.setOvertimeHours(overtimeHours);
		woer.setWorkContent(workContent);
		rows = this.woerMapper.insertSelective(woer);
		if (rows <= 0) {
			throw new WorkOrderException("插入工单执行记录失败");
		}

		// 更新工单
		workOrder.setWorkOrderState(WorkOrderState.SOLVED.getValue());
		workOrder.setModifyTime(new Date());
		rows = this.workOrderMapper.updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		}
		// 发邮件
		User applicant = AlmCache.getInstance().getUserMap().get(workOrder.getApplicantId());
		if (applicant == null) {
			throw new WorkOrderException("申请人不存在");
		}
		this.sendMail(workOrder, "工单执行", Sets.newHashSet(applicant.getEmail()));
	}

	protected int updateExactSimple(WorkOrder workOrder) {
		try {
			buildExactDomain(workOrder, "setForceParams");
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
		}
		return this.workOrderMapper.updateByPrimaryKeyExact(workOrder);
	}

	/**
	 * 构建精确更新实体,仅支持DTO
	 * 
	 * @param domain
	 *            DTO接口
	 * @param fieldName
	 *            setForceParams或insertForceParams
	 */
	private void buildExactDomain(WorkOrder domain, String fieldName) throws Exception {
		// 如果不是DTO接口，不构建
		if (!DTOUtils.isProxy(domain)) {
			return;
		}
		// 如果未实现IMybatisForceParams接口不构建
		if (!IMybatisForceParams.class.isAssignableFrom(DTOUtils.getDTOClass(domain))) {
			return;
		}
		Map params = new HashMap();
		// 构建dto
		Method[] dtoMethods = DTOUtils.getDTOClass(domain).getMethods();
		Map dtoMap = DTOUtils.go(domain);
		for (Method dtoMethod : dtoMethods) {
			// 只判断getter方法
			if (POJOUtils.isGetMethod(dtoMethod)) {
				// 如果dtoMap中有该字段，并且值为null
				if (dtoMap.containsKey(POJOUtils.getBeanField(dtoMethod)) && dtoMethod.invoke(domain) == null) {
					Id id = dtoMethod.getAnnotation(Id.class);
					// 不允许将主键改为null
					if (id != null) {
						continue;
					}
					Column column = dtoMethod.getAnnotation(Column.class);
					String columnName = column == null ? POJOUtils.humpToLine(POJOUtils.getBeanField(dtoMethod))
							: column.name();
					params.put(columnName, null);
				}
			}
		}
		domain.aset(fieldName, params);
	}

	protected void sendMail(WorkOrder workOrder, String subject, Set<String> emails) {

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
		workOrder = this.getOperationRecordsViewModel(workOrder);
		Template template = this.groupTemplate.getTemplate(this.contentTemplate);
		try {
			template.binding("model", WorkOrderService.parseViewModel(workOrder));
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

	@Override
	public WorkOrder getOperationRecordsViewModel(WorkOrder workOrder) {
		if (StringUtils.isNotBlank(workOrder.getCopyUserId())) {
			List<Long> copyUserIds = JSON.parseArray(workOrder.getCopyUserId(), Long.class);
			StringBuilder sb = new StringBuilder();
			copyUserIds.forEach(cid -> sb.append(this.memberProvider.getDisplayText(cid, null, null)).append(","));
			workOrder.aset("copyUsers", sb.substring(0, sb.length() - 1));
		}
		WorkOrderOperationRecord woorQuery = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woorQuery.setWorkOrderId(workOrder.getId());
		List<WorkOrderOperationRecord> opRecords = this.woorMapper.select(woorQuery);
		try {
			workOrder.aset("opRecords", parseOperationRecordViewModel(opRecords));
			return workOrder;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
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

		metadata.put("operationResult", "operationResultProvider");

		return ValueProviderUtils.buildDataByProvider(metadata, opRecords);
	}

	@Override
	public void systemClose(WorkOrder workOrder) throws WorkOrderException {
		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woor.setOperatorId(workOrder.getApplicantId());
		woor.setOperationName(WorkOrderOperationType.SYSTEM_CLOSE.getName());
		woor.setOperationType(WorkOrderOperationType.SYSTEM_CLOSE.getValue());
		woor.setOperationResult(OperationResult.SUCCESS.getValue());
		woor.setWorkOrderId(workOrder.getId());
		woor.setDescription("系统关闭");
		int rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}
		// 更新工单
		workOrder.setCloseTime(new Date());
		workOrder.setWorkOrderState(WorkOrderState.CLOSED.getValue());
		workOrder.setModifyTime(new Date());
		rows = this.workOrderMapper.updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		}
	}

}
