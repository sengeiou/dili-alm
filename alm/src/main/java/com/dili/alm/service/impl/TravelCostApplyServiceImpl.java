package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.MailManager;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.TravelCostApplyMapper;
import com.dili.alm.dao.TravelCostDetailMapper;
import com.dili.alm.dao.TravelCostMapper;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.TravelCost;
import com.dili.alm.domain.TravelCostApply;
import com.dili.alm.domain.TravelCostApplyResult;
import com.dili.alm.domain.TravelCostApplyState;
import com.dili.alm.domain.TravelCostDetail;
import com.dili.alm.domain.TravelCostDetailEntity;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.TravelCostApplyMailDto;
import com.dili.alm.domain.dto.TravelCostApplyUpdateDto;
import com.dili.alm.domain.dto.TravelCostApplyUpdateDtoBean;
import com.dili.alm.domain.dto.TravelCostDto;
import com.dili.alm.domain.dto.TravelCostDtoBean;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.TravelCostApplyException;
import com.dili.alm.provider.AreaProvider;
import com.dili.alm.provider.DepProvider;
import com.dili.alm.provider.MemberProvider;
import com.dili.alm.provider.ProjectProvider;
import com.dili.alm.provider.TravelCostApplyStateProvider;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.TravelCostApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.github.pagehelper.Page;
import com.google.common.collect.Sets;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-04-24 16:43:13.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class TravelCostApplyServiceImpl extends BaseServiceImpl<TravelCostApply, Long>
		implements TravelCostApplyService {

	@Autowired
	private AreaProvider areaProvider;
	private String contentTemplate;
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private DepProvider deptProvider;
	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;
	@Value("${spring.mail.username:}")
	private String mailFrom;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private MemberProvider memberProvider;
	@Qualifier("travelCostApplyNumberGenerator")
	@Autowired
	private NumberGenerator numberGenerator;
	@Autowired
	private ProjectProvider projectProvider;
	@Autowired
	private TravelCostApplyStateProvider stateProvider;
	@Autowired
	private TravelCostDetailMapper travelCostDetailMapper;
	@Autowired
	private TravelCostMapper travelCostMapper;

	@SuppressWarnings("rawtypes")
	public static List<Map> getTraveCostApplyViewModel(List<TravelCostApply> list) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("applicantId", memberProvider);

		JSONObject stateProvider = new JSONObject();
		stateProvider.put("provider", "travelCostApplyStateProvider");
		metadata.put("applyState", stateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("submitDate", datetimeProvider);
		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

	@SuppressWarnings("rawtypes")
	public static List<Map> getTravelCostViewModel(List<TravelCost> costs) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject areaProvider = new JSONObject();
		areaProvider.put("provider", "memberProvider");
		metadata.put("setOutPlace", areaProvider);
		metadata.put("destinationPlace", areaProvider);

		return ValueProviderUtils.buildDataByProvider(metadata, costs);
	}

	public TravelCostApplyServiceImpl() {
		super();
		InputStream in = null;
		try {
			Resource res = new ClassPathResource("conf/travelCostApplyMailTemplate.html");
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
	public void deleteTravelCostApply(Long applyId) throws TravelCostApplyException {
		TravelCostApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new TravelCostApplyException("差旅成本不存在");
		}
		if (!apply.getApplyState().equals(TravelCostApplyState.APPLING.getValue())) {
			throw new TravelCostApplyException("当前状态不能进行删除操作");
		}
		// 删除费用明细
		TravelCost tcQuery = DTOUtils.newDTO(TravelCost.class);
		tcQuery.setApplyId(applyId);
		List<TravelCost> costs = this.travelCostMapper.select(tcQuery);
		if (CollectionUtils.isNotEmpty(costs)) {
			costs.forEach(c -> {
				TravelCostDetail tcdQuery = DTOUtils.newDTO(TravelCostDetail.class);
				tcdQuery.setCostId(c.getId());
				this.travelCostDetailMapper.delete(tcdQuery);
			});
			int rows = this.travelCostMapper.delete(tcQuery);
			if (rows <= 0) {
				throw new TravelCostApplyException("删除费用明细失败");
			}
		}
		int rows = this.getActualDao().deleteByPrimaryKey(applyId);
		if (rows <= 0) {
			throw new TravelCostApplyException("删除失败");
		}
	}

	public TravelCostApplyMapper getActualDao() {
		return (TravelCostApplyMapper) getDao();
	}

	@Override
	public TravelCostApply getUpdateViewData(Long id) throws Exception {
		TravelCostApply apply = this.getActualDao().selectByPrimaryKey(id);
		TravelCostApplyUpdateDtoBean dto = DTOUtils.toEntity(apply, TravelCostApplyUpdateDtoBean.class, false);
		dto.setApplicantName(this.memberProvider.getDisplayText(dto.getApplicantId(), null, null));
		dto.setDepartmentName(this.deptProvider.getDisplayText(dto.getDepartmentId(), null, null));
		dto.setProjectName(this.projectProvider.getDisplayText(dto.getProjectId(), null, null));
		dto.setRootDepartmentName(this.deptProvider.getDisplayText(dto.getRootDepartemntId(), null, null));
		TravelCost tcQuery = DTOUtils.newDTO(TravelCost.class);
		tcQuery.setApplyId(id);
		List<TravelCost> costs = this.travelCostMapper.select(tcQuery);
		if (CollectionUtils.isNotEmpty(costs)) {
			List<TravelCostDtoBean> tcBeanList = new ArrayList<>(costs.size());
			costs.forEach(c -> {
				TravelCostDtoBean cBean = DTOUtils.toEntity(c, TravelCostDtoBean.class, false);
				tcBeanList.add(cBean);
				cBean.setSetOutPlaceText(this.areaProvider.getDisplayText(c.getSetOutPlace(), null, null));
				cBean.setDestinationPlaceText(this.areaProvider.getDisplayText(c.getDestinationPlace(), null, null));
				TravelCostDetail tcdQuery = DTOUtils.newDTO(TravelCostDetail.class);
				tcdQuery.setCostId(c.getId());
				List<TravelCostDetail> details = this.travelCostDetailMapper.select(tcdQuery);
				List<TravelCostDetailEntity> tcdBeanList = new ArrayList<>(details.size());
				details.forEach(td -> tcdBeanList.add(DTOUtils.toEntity(td, TravelCostDetailEntity.class, false)));
				cBean.setTravelCostDetail(tcdBeanList);
			});
			dto.setTravelCost(tcBeanList);
		}
		return dto;
	}

	@Override
	public EasyuiPageOutput listEasyuiPageByExample(TravelCostApply domain, boolean useProvider) throws Exception {
		List<TravelCostApply> list = listByExample(domain);
		@SuppressWarnings("rawtypes")
		long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
		@SuppressWarnings("rawtypes")
		List results = useProvider ? getTraveCostApplyViewModel(list) : list;
		return new EasyuiPageOutput(Integer.parseInt(String.valueOf(total)), results);
	}

	@Override
	public void saveAndSubmit(TravelCostApplyUpdateDto apply) throws TravelCostApplyException {
		this.saveOrUpdate(apply);
		this.submit(apply.getId());
	}

	@Override
	public void saveOrUpdate(TravelCostApplyUpdateDto dto) throws TravelCostApplyException {
		if (dto.getId() == null) {
			this.insertTravelCostApply(dto);
		} else {
			this.updateTravelCostApply(dto);
		}
	}

	@Override
	public void submit(Long applyId) throws TravelCostApplyException {
		TravelCostApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new TravelCostApplyException("差旅成本不存在");
		}
		if (!apply.getApplyState().equals(TravelCostApplyState.APPLING.getValue())) {
			throw new TravelCostApplyException("当前状态不能提交");
		}
		apply.setApplyState(TravelCostApplyState.COMPLETED.getValue());
		apply.setSubmitDate(new Date());
		int rows = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (rows <= 0) {
			throw new TravelCostApplyException("更新状态失败");
		}
		User user = this.queryGeneralAssistant();
		this.sendMail(apply, "差旅成本申请", Sets.newHashSet(user.getEmail()));
	}

	private TravelCostApplyMailDto buildApplyViewModel(TravelCostApply apply) {
		TravelCostApplyMailDto dto = DTOUtils.toEntity(apply, TravelCostApplyMailDto.class, false);
		dto.setApplicantName(this.memberProvider.getDisplayText(dto.getApplicantId(), null, null));
		dto.setDepartmentName(this.deptProvider.getDisplayText(dto.getDepartmentId(), null, null));
		dto.setProjectName(this.projectProvider.getDisplayText(dto.getProjectId(), null, null));
		dto.setRootDepartmentName(this.deptProvider.getDisplayText(dto.getRootDepartemntId(), null, null));
		dto.setApplyStateName(this.stateProvider.getDisplayText(dto.getApplyState(), null, null));
		TravelCost tcQuery = DTOUtils.newDTO(TravelCost.class);
		tcQuery.setApplyId(apply.getId());
		List<TravelCost> costs = this.travelCostMapper.select(tcQuery);
		if (CollectionUtils.isNotEmpty(costs)) {
			List<TravelCostDtoBean> tcBeanList = new ArrayList<>(costs.size());
			costs.forEach(c -> {
				TravelCostDtoBean cBean = DTOUtils.toEntity(c, TravelCostDtoBean.class, false);
				tcBeanList.add(cBean);
				cBean.setSetOutPlaceText(this.areaProvider.getDisplayText(c.getSetOutPlace(), null, null));
				cBean.setDestinationPlaceText(this.areaProvider.getDisplayText(c.getDestinationPlace(), null, null));
				TravelCostDetail tcdQuery = DTOUtils.newDTO(TravelCostDetail.class);
				tcdQuery.setCostId(c.getId());
				List<TravelCostDetail> details = this.travelCostDetailMapper.select(tcdQuery);
				List<TravelCostDetailEntity> tcdBeanList = new ArrayList<>(details.size());
				details.forEach(td -> tcdBeanList.add(DTOUtils.toEntity(td, TravelCostDetailEntity.class, false)));
				cBean.setTravelCostDetail(tcdBeanList);
			});
			dto.setTravelCost(tcBeanList);
		}
		return dto;
	}

	private void insertTravelCostApply(TravelCostApplyUpdateDto dto) throws TravelCostApplyException {
		User applicant = AlmCache.getInstance().getUserMap().get(dto.getApplicantId());
		if (applicant == null) {
			throw new TravelCostApplyException("申请人不存在");
		}
		Department dept = AlmCache.getInstance().getDepMap().get(applicant.getDepartmentId());
		dto.setDepartmentId(dept.getId());
		while (dept.getParentId() != null) {
			dept = AlmCache.getInstance().getDepMap().get(dept.getParentId());
		}
		dto.setRootDepartemntId(dept.getId());
		// 计算总出差天数
		int travelDayAmount = 0;
		for (TravelCost tc : dto.getTravelCost()) {
			travelDayAmount += tc.getTravelDayAmount();
		}
		dto.setTravelDayAmount(travelDayAmount);
		dto.setSerialNumber(this.numberGenerator.get());
		int rows = this.getActualDao().insertSelective(dto);
		if (rows <= 0) {
			throw new TravelCostApplyException("新增差旅成本失败");
		}
		// 插入差旅费明细
		for (TravelCostDto t : dto.getTravelCost()) {
			t.setApplyId(dto.getId());
			rows = this.travelCostMapper.insertSelective(t);
			if (rows <= 0) {
				throw new TravelCostApplyException("插入差旅费明细失败");
			}
			// 插入费用明细
			for (TravelCostDetail tcd : t.getTravelCostDetail()) {
				tcd.setCostId(t.getId());
				rows = this.travelCostDetailMapper.insertSelective(tcd);
				if (rows <= 0) {
					throw new TravelCostApplyException("插入差旅费明细失败");
				}
			}
		}
	}

	private User queryGeneralAssistant() throws TravelCostApplyException {
		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.DEPARTMENT_MANAGER_ROLE_CONFIG_CODE);
		if (dd == null) {
			throw new TravelCostApplyException("请先配置总经理助理用户数据字典配置");
		}
		DataDictionaryValue ddValue = dd.getValues().stream()
				.filter(v -> v.getCode().equals(AlmConstants.GENERAL_MANAGER_ASSISTANT_CODE)).findFirst().orElse(null);
		if (ddValue == null) {
			throw new TravelCostApplyException("请先配置总经理助理用户数据字典配置");
		}
		Map.Entry<Long, User> entry = AlmCache.getInstance().getUserMap().entrySet().stream()
				.filter(e -> e.getValue().getUserName().equals(ddValue.getValue())).findFirst().orElse(null);
		if (entry == null) {
			throw new TravelCostApplyException("用户不存在，请检查配置是否正确");
		}
		return entry.getValue();
	}

	private void sendMail(TravelCostApply apply, String subject, Set<String> emails) {

		// 构建邮件内容
		Template template = this.groupTemplate.getTemplate(this.contentTemplate);
		template.binding("apply", buildApplyViewModel(apply));
		template.binding("costItems", AlmCache.getInstance().getTravelCostItemMap());

		// 发送
		emails.forEach(s -> {
			try {
				this.mailManager.sendMail(this.mailFrom, s, template.render(), true, subject, null);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		});
	}

	private void updateTravelCostApply(TravelCostApplyUpdateDto dto) throws TravelCostApplyException {
		TravelCostApply apply = this.getActualDao().selectByPrimaryKey(dto.getId());
		if (apply == null) {
			throw new TravelCostApplyException("记录不存在");
		}
		// 计算总出差天数
		int travelDayAmount = 0;
		for (TravelCost tc : dto.getTravelCost()) {
			travelDayAmount += tc.getTravelDayAmount();
		}
		dto.setTravelDayAmount(travelDayAmount);
		// 删除差旅明细
		TravelCost tcQuery = DTOUtils.newDTO(TravelCost.class);
		tcQuery.setApplyId(dto.getId());
		List<TravelCost> tcList = this.travelCostMapper.select(tcQuery);
		tcList.forEach(tc -> {
			TravelCostDetail tcdQuery = DTOUtils.newDTO(TravelCostDetail.class);
			tcdQuery.setCostId(tc.getId());
			this.travelCostDetailMapper.delete(tcdQuery);
		});
		this.travelCostMapper.delete(tcQuery);
		// 插入差旅费明细
		for (TravelCostDto t : dto.getTravelCost()) {
			t.setApplyId(dto.getId());
			int rows = this.travelCostMapper.insertSelective(t);
			if (rows <= 0) {
				throw new TravelCostApplyException("插入差旅费明细失败");
			}
			// 插入费用明细
			for (TravelCostDetail tcd : t.getTravelCostDetail()) {
				tcd.setCostId(t.getId());
				rows = this.travelCostDetailMapper.insertSelective(tcd);
				if (rows <= 0) {
					throw new TravelCostApplyException("插入差旅费明细失败");
				}
			}
		}
		apply.setProjectId(dto.getProjectId());
		apply.setTotalAmount(dto.getTotalAmount());
		this.getActualDao().updateByPrimaryKeySelective(apply);
	}
}