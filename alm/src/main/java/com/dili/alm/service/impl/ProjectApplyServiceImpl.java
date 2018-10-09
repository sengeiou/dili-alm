package com.dili.alm.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.ProjectCostMapper;
import com.dili.alm.dao.ProjectEarningMapper;
import com.dili.alm.dao.RoiMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.FileType;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.IndicatorType;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectCost;
import com.dili.alm.domain.ProjectCostType;
import com.dili.alm.domain.ProjectEarning;
import com.dili.alm.domain.Roi;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.RoiUpdateDto;
import com.dili.alm.domain.dto.apply.ApplyFiles;
import com.dili.alm.domain.dto.apply.ApplyImpact;
import com.dili.alm.domain.dto.apply.ApplyMajorResource;
import com.dili.alm.domain.dto.apply.ApplyPlan;
import com.dili.alm.domain.dto.apply.ApplyRisk;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.SystemConfigUtils;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.google.common.collect.Lists;

import tk.mybatis.mapper.entity.Example;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-21 16:19:02.
 */
@Service
public class ProjectApplyServiceImpl extends BaseServiceImpl<ProjectApply, Long> implements ProjectApplyService {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ProjectApplyServiceImpl.class);

	@Autowired
	private NumberGenerator projectNumberGenerator;
	@Autowired
	private DataDictionaryService dataDictionaryService;

	@Autowired
	private FilesService filesService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private ApproveService approveService;
	@Autowired
	private ProjectEarningMapper projectEarningMapper;
	@Autowired
	private ProjectCostMapper projectCostMapper;
	@Autowired
	private RoiMapper roiMapper;

	public ProjectApplyMapper getActualDao() {
		return (ProjectApplyMapper) getDao();
	}

	@Override
	public int insertApply(ProjectApply apply) {
		apply.setNumber(getProjectNumber(apply));
		apply.setName(apply.getNumber());
		apply.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
		apply.setStatus(AlmConstants.ApplyState.APPLY.getCode());
		return getActualDao().insertSelective(apply);
	}

	@Override
	public List<DataDictionaryValueDto> getPlanPhase() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(AlmConstants.APPLY_PLAN_PHASE_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public void submit(ProjectApply projectApply, ApplyFiles files) throws ProjectApplyException {
		if (StringUtils.isNotBlank(files.getDels())) {
			List<Files> filesFromJson = JSON.parseArray(files.getDels(), Files.class);
			filesFromJson.forEach(file -> filesService.delete(file.getId()));
		}
		if (StringUtils.isNotBlank(files.getFiles())) {
			List<Files> filesFromJson = JSON.parseArray(files.getFiles(), Files.class);
			filesFromJson.forEach(file -> {
				file.setType(FileType.APPLY.getValue());
				file.setRecordId(projectApply.getId());
				filesService.updateSelective(file);
			});
		}
		/*
		 * 处理生成审批单
		 */
		if (projectApply.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
			Approve as = DTOUtils.as(this.get(projectApply.getId()), Approve.class);
			as.setId(null);
			as.setCreatedStart(null);
			as.setCreated(new Date());
			as.setProjectApplyId(projectApply.getId());
			as.setStatus(AlmConstants.ApplyState.APPROVE.getCode());
			as.setProjectLeader(as.getProjectLeader());
			as.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
			as.setProjectType(as.getType());
			as.setExtend(as.getDescription());
			as.setType(AlmConstants.ApproveType.APPLY.getCode());

			approveService.insertBefore(as);
			sendMail(this.get(projectApply.getId()));
		}

		int rows = this.updateSelective(projectApply);
		if (rows <= 0) {
			throw new ProjectApplyException("提交立项申请失败");
		}
	}

	public void sendMail(ProjectApply projectApply) {
		try {
			String[] sendTo = projectApply.getEmail().split(",");
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(sendTo);
			message.setFrom(SystemConfigUtils.getProperty("spring.mail.username"));
			message.setSubject("立项申请");
			message.setText(projectApply.getName() + "的立项申请已经提交成功");
			mailSender.send(message);
		} catch (Exception e) {
			LOGGER.error("邮件发送出错:", e);
		}
	}

	@Override
	public Long reApply(Long id) {
		ProjectApply apply = get(id);
		if (apply == null) {
			return -1L;
		}
		if (apply.getRestatus() != null) {
			return -1L;
		}
		if (!apply.getCreateMemberId().equals(SessionContext.getSessionContext().getUserTicket().getId())) {
			return -1L;
		}
		apply.setRestatus(0);
		updateSelective(apply);
		apply.setId(null);
		apply.setStatus(AlmConstants.ApplyState.APPLY.getCode());
		apply.setCreated(new Date());
		apply.setModified(null);
		apply.setNumber(getProjectNumber(apply));
		apply.setRestatus(null);
		insertSelective(apply);
		buildFiles(id, apply.getId());
		return apply.getId();
	}

	private void buildFiles(Long applyId, Long newId) {
		listFiles(applyId).forEach((Files files) -> {
			files.setRecordId(newId);
			files.setId(null);
			filesService.insert(files);
		});
	}

	@Override
	public List<Files> listFiles(Long applyId) {
		Example example = new Example(Files.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("recordId", applyId).andEqualTo("type", FileType.APPLY.getValue());
		return this.filesService.selectByExample(example);
	}

	@Override
	public void buildStepOne(Map modelMap, Map applyDTO) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();
		ApplyMajorResource resourceRequire = JSON.parseObject(Optional.ofNullable(applyDTO.get("resourceRequire")).map(Object::toString).orElse("{}"), ApplyMajorResource.class);
		metadata.clear();
		metadata.put("mainUser", JSON.parse("{provider:'memberProvider'}"));
		List<Map> majorMap = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(resourceRequire));
		metadata.clear();
		metadata.put("relatedUser", JSON.parse("{provider:'memberProvider'}"));
		List<Map> relatedMap = ValueProviderUtils.buildDataByProvider(metadata, Optional.ofNullable(resourceRequire.getRelatedResources()).orElse(new ArrayList<>()));
		modelMap.put("main", majorMap.get(0));
		modelMap.put("related", relatedMap);
	}

	@Override
	public List<Map> loadPlan(Long id) throws Exception {
		ProjectApply projectApply = this.get(id);
		List<ApplyPlan> result = new ArrayList<>();

		if (StringUtils.isNotBlank(projectApply.getPlan())) {
			result = JSON.parseArray(projectApply.getPlan(), ApplyPlan.class);
		} else {
			List<DataDictionaryValueDto> list = this.getPlanPhase();
			List<ApplyPlan> finalResult = result;
			list.forEach(dataDictionaryValueDto -> {
				ApplyPlan plan = new ApplyPlan();
				plan.setPhase(dataDictionaryValueDto.getCode());
				finalResult.add(plan);
			});
		}

		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("startDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("endDate", JSON.parse("{provider:'dateProvider'}"));
		return ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(result));
	}

	@Override
	public List<ApplyImpact> loadImpact(Long id) {
		ProjectApply projectApply = this.get(id);
		List<ApplyImpact> result = new ArrayList<>();

		if (StringUtils.isNotBlank(projectApply.getImpact())) {
			result = JSON.parseArray(projectApply.getImpact(), ApplyImpact.class);
		}
		return result;
	}

	@Override
	public List<ApplyRisk> loadRisk(Long id) {
		ProjectApply projectApply = this.get(id);
		List<ApplyRisk> result = new ArrayList<>();

		if (StringUtils.isNotBlank(projectApply.getRisk())) {
			result = JSON.parseArray(projectApply.getRisk(), ApplyRisk.class);
		} else {
			List<DataDictionaryValueDto> list = dataDictionaryService.findByCode("kind_risk").getValues();
			List<ApplyRisk> finalResult = result;
			list.forEach(dataDictionaryValueDto -> {
				ApplyRisk risk = new ApplyRisk();
				risk.setType(dataDictionaryValueDto.getCode());
				finalResult.add(risk);
			});
		}
		return result;
	}

	/**
	 * 生成规则方式为[级别编号+项目级别]-[2位年]-[4位自增数]
	 *
	 * @param apply
	 * @return
	 */
	private String getProjectNumber(ProjectApply apply) {
		String number = apply.getType() + (apply.getPid() == null ? "1" : "2");
		String yearLast = new SimpleDateFormat("yy", Locale.CHINESE).format(Calendar.getInstance().getTime());
		return number + "-" + yearLast + "-" + projectNumberGenerator.get();
	}

	@Transactional
	@Override
	public void updateRoi(RoiUpdateDto dto) throws ProjectApplyException {
		// 先删除roi
		Roi roiQuery = DTOUtils.newDTO(Roi.class);
		roiQuery.setApplyId(dto.getApplyId());
		this.roiMapper.delete(roiQuery);

		// 删除项目收益
		ProjectEarning peQuery = DTOUtils.newDTO(ProjectEarning.class);
		peQuery.setApplyId(dto.getApplyId());
		this.projectEarningMapper.delete(peQuery);

		// 删除项目成本
		ProjectCost pcQuery = DTOUtils.newDTO(ProjectCost.class);
		pcQuery.setApplyId(dto.getApplyId());
		this.projectCostMapper.delete(pcQuery);

		// 插入roi
		Roi roi = DTOUtils.newDTO(Roi.class);
		roi.setApplyId(dto.getApplyId());
		roi.setTotal(dto.getRoiTotal());
		int rows = this.roiMapper.insertSelective(roi);
		if (rows <= 0) {
			throw new ProjectApplyException("更新roi失败");
		}

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		// 插入项目成本和收益
		for (int i = 0; i < dto.getIndicatorType().size(); i++) {
			ProjectEarning pe = DTOUtils.newDTO(ProjectEarning.class);
			pe.setApplyId(dto.getApplyId());
			if (StringUtils.isNotBlank(dto.getImplemetionDate().get(i))) {
				try {
					pe.setImplemetionDate(df.parse(dto.getImplemetionDate().get(i)));
				} catch (ParseException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			pe.setIndicatorCurrentStatus(dto.getIndicatorCurrentStatus().get(i));
			pe.setIndicatorName(dto.getIndicatorName().get(i));
			pe.setIndicatorType(dto.getIndicatorType().get(i));
			pe.setProjectObjective(dto.getProjectObjective().get(i));
			pe.setRoiId(roi.getId());
			rows = this.projectEarningMapper.insertSelective(pe);
			if (rows <= 0) {
				throw new ProjectApplyException("插入项目收益失败");
			}
		}

		for (int i = 0; i < dto.getCostType().size(); i++) {
			ProjectCost pc = DTOUtils.newDTO(ProjectCost.class);
			pc.setAmount(dto.getAmount().get(i));
			pc.setApplyId(dto.getApplyId());
			pc.setCostType(dto.getCostType().get(i));
			pc.setNote(dto.getNote().get(i));
			pc.setRate(dto.getRate().get(i));
			pc.setRoiId(roi.getId());
			pc.setTotal(dto.getTotal().get(i));
			rows = this.projectCostMapper.insertSelective(pc);
			if (rows <= 0) {
				throw new ProjectApplyException("插入项目成本失败");
			}
		}

		// 清除缓存
		AlmCache.getInstance().getProjectApplyRois().clear();
	}

	@Transactional
	@Override
	public void deleteProjectApply(Long id) throws ProjectApplyException {
		ProjectEarning peQuery = DTOUtils.newDTO(ProjectEarning.class);
		peQuery.setApplyId(id);
		this.projectEarningMapper.delete(peQuery);

		ProjectCost pcQuery = DTOUtils.newDTO(ProjectCost.class);
		pcQuery.setApplyId(id);
		this.projectCostMapper.delete(pcQuery);

		Roi roiQuery = DTOUtils.newDTO(Roi.class);
		roiQuery.setApplyId(id);
		this.roiMapper.delete(roiQuery);

		int rows = this.getActualDao().deleteByPrimaryKey(id);
		if (rows <= 0) {
			throw new ProjectApplyException("删除失败");
		}
		AlmCache.getInstance().getProjectApplyRois().remove(id);
	}

}