package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.MailManager;
import com.dili.alm.component.NumberGenerator;
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
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.HardwareResourceApplyUpdateDto;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.HardwareResourceApplyException;
import com.dili.alm.provider.EnvironmentProvider;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.github.pagehelper.Page;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-20 17:22:08.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class HardwareResourceApplyServiceImpl extends BaseServiceImpl<HardwareResourceApply, Long>
		implements HardwareResourceApplyService {

	private String contentTemplate;
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

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "almDateProvider");
		metadata.put("onlineDate", datetimeProvider);
		metadata.put("actualOnlineDate", datetimeProvider);
		metadata.put("submitTime", datetimeProvider);

		JSONObject applyStateProvider = new JSONObject();
		applyStateProvider.put("provider", "projectOnlineApplyStateProvider");
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
			this.contentTemplate = IOUtils.toString(in);
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

	@Override
	public void generalManagerApprove(Long applyId, Long generalManagerId, ApproveResult result, String description)
			throws HardwareResourceApplyException {
		// TODO Auto-generated method stub

	}

	public HardwareResourceApplyMapper getActualDao() {
		return (HardwareResourceApplyMapper) getDao();
	}

	@Transactional(readOnly = true)
	@Override
	public EasyuiPageOutput listEasyuiPageByExample(HardwareResourceApply domain, boolean useProvider)
			throws Exception {
		List<HardwareResourceApply> list = listByExample(domain);
		list.forEach(h -> {
			h.aset("envList", this.parseEnvToString(h.getServiceEnvironment()));
		});
		long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
		List results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, list) : list;
		return new EasyuiPageOutput(Integer.parseInt(String.valueOf(total)), results);
	}

	@Override
	public void operationManagerApprove(Long applyId, Long operationManagerId, Set<Long> executors, String description)
			throws HardwareResourceApplyException {
		// TODO Auto-generated method stub

	}

	@Override
	public void projectManagerApprove(Long applyId, Long projectManagerId, ApproveResult result, String description)
			throws HardwareResourceApplyException {
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

		if (ApproveResult.APPROVED.equals(result)) {
			// 审批通过流转到研发部总经理审批
			apply.setApplyState(HardwareApplyState.GENERAL_MANAGER_APPROVING.getValue());
		} else if (ApproveResult.FAILED.equals(result)) {
			// 未通过审批退回到编辑状态让用户重新编辑
			apply.setApplyState(HardwareApplyState.APPLING.getValue());
		} else {
			throw new IllegalArgumentException("未知的审批结果");
		}
		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
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
		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (rows <= 0) {
			throw new HardwareResourceApplyException("更新申请状态失败");
		}
		// 发邮件给项目经理
		User user = AlmCache.USER_MAP.get(apply.getProjectManagerId());
		if (user == null) {
			throw new HardwareResourceApplyException("项目经理不存在");
		}
		this.sendMail(apply, "IT资源申请", user.getEmail());
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
		rows = this.hrrMapper
				.insertList(DTOUtils.as(dto.getConfigurationRequirement(), HardwareResourceRequirement.class));
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

	private void sendMail(HardwareResourceApply apply, String subject, String to) {

		// 构建邮件内容
		Template template = this.groupTemplate.getTemplate(this.contentTemplate);
		template.binding("apply", buildApplyViewModel(apply));
		HardwareApplyOperationRecord poor = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		poor.setApplyId(apply.getId());
		List<HardwareApplyOperationRecord> list = this.haorMapper.select(poor);
		template.binding("opRecords", this.buildOperationRecordViewModel(list));

		// 发送
		try {
			this.mailManager.sendMail(this.mailFrom, to, template.render(), true, subject, null);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void updateHardwareApply(HardwareResourceApplyUpdateDto dto) throws HardwareResourceApplyException {
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
		rows = this.hrrMapper
				.insertList(DTOUtils.as(dto.getConfigurationRequirement(), HardwareResourceRequirement.class));
		if (rows <= 0) {
			throw new HardwareResourceApplyException("插入配置要求失败");
		}
	}

}