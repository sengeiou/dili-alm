package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
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
import com.dili.alm.component.BpmcUtil;
import com.dili.alm.component.MailManager;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.AlmConstants.DemandProcessStatus;
import com.dili.alm.constant.AlmConstants.DemandStatus;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.dao.DemandMapper;
import com.dili.alm.dao.DemandOperationRecordMapper;
import com.dili.alm.dao.DemandProjectMapper;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.SequenceMapper;
import com.dili.alm.domain.ApproveResult;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.DemandOperationRecord;
import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.DemandProjectStatus;
import com.dili.alm.domain.DemandProjectType;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Sequence;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.exceptions.DemandExceptions;
import com.dili.alm.rpc.resolver.MyRuntimeRpc;
import com.dili.alm.rpc.resolver.MyTaskRpc;
import com.dili.alm.service.DemandService;
import com.dili.alm.utils.WebUtil;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.EventReceivedDto;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.restful.EventRpc;
import com.dili.bpmc.sdk.rpc.restful.FormRpc;
import com.dili.bpmc.sdk.rpc.restful.RuntimeRpc;
import com.dili.bpmc.sdk.rpc.restful.TaskRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.rpc.DepartmentRpc;
import com.dili.uap.sdk.rpc.FirmRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;
import com.github.pagehelper.Page;

import tk.mybatis.mapper.entity.Example;

/**
 * ???MyBatis Generator?????????????????? This file was generated on 2019-12-23 17:32:14.
 */
@Transactional(rollbackFor = DemandExceptions.class)
@Service
public class DemandServiceImpl extends BaseServiceImpl<Demand, Long> implements DemandService {

	private static final String DEMAND_NUMBER_GENERATOR_TYPE = "demandNumberGenerator";
	// ?????????????????????
	private static final Long DEMAND_OM_GROUP_ID = Long.parseLong("71");
	// ????????????????????????
	private static final Long DEMAND_DEP_ACCPET_GROUP_ID = Long.parseLong("72");
	// ??????????????????
	private static final Long DEMAND_MANAGER_GROUP_ID = Long.parseLong("74");

	// ????????????????????????
	private static final String FIRECODE_SZPT = "szpt";
	// ???????????????????????????
	private static final String FIRECODE_JYGL = "jygl";

	@Qualifier(DEMAND_NUMBER_GENERATOR_TYPE)
	@Autowired
	private NumberGenerator numberGenerator;
	@Autowired
	RuntimeRpc runtimeRpc;
	@Autowired
	TaskRpc taskRpc;
	@Autowired
	private SequenceMapper sequenceMapper;
	@Autowired
	private DemandOperationRecordMapper operationRecordMapper;
	/**
	 * ????????????
	 */
	@Autowired
	private MailManager mailManager;
	@Value("${spring.mail.username:}")
	private String mailFrom;
	private String contentTemplate;
	@Value("${uap.contextPath:http://uap.diligrp.com}")
	private String uapContextPath;

	/**
	 * ????????????
	 */
	public DemandMapper getActualDao() {
		return (DemandMapper) getDao();
	}

	@Autowired
	private DepartmentRpc departmentRpc;
	@Autowired
	private DemandProjectMapper demandProjectMapper;
	@Autowired
	private ProjectApplyMapper projectApplyMapper;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectVersionMapper projectVesionMapper;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	FormRpc formRpc;

	@Autowired
	private FirmRpc firmRpc;
	@Autowired
	private DemandMapper demandMapper;
	@Autowired
	private BpmcUtil bpmcUtil;
	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;
	
	@Autowired 
	private MyRuntimeRpc myRuntimeRpc;
	
	@Autowired
	private MyTaskRpc myTaskRpc;
	
	@Autowired
	private EventRpc eventRpc;

	public static Object parseViewModel(DemandDto detailViewData) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("belongProId", projectProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberForAllProvider");
		metadata.put("userId", memberProvider);

		JSONObject demandStateProvider = new JSONObject();
		demandStateProvider.put("provider", "demandStateProvider");
		metadata.put("status", demandStateProvider);

		JSONObject demandTypeProvider = new JSONObject();
		demandTypeProvider.put("provider", "demandTypeProvider");
		metadata.put("type", demandTypeProvider);

		JSONObject projectSysProvider = new JSONObject();
		projectSysProvider.put("provider", "projectSysProvider");
		metadata.put("belongSysId", projectSysProvider);

		JSONObject demandProcessProvider = new JSONObject();
		demandProcessProvider.put("provider", "demandProcessProvider");
		metadata.put("processType", demandProcessProvider);

		JSONObject almDateProvider = new JSONObject();
		almDateProvider.put("provider", "almDateProvider");

		metadata.put("createDate", almDateProvider);
		metadata.put("submitDate", almDateProvider);
		metadata.put("finishDate", almDateProvider);

		List<Map> listMap = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(detailViewData));
		return listMap.get(0);
	}

	/**
	 * ????????????
	 * 
	 * @throws DemandExceptions
	 */
	@Override
	public int addNewDemand(Demand newDemand) throws DemandExceptions {
		this.numberGenerator.init();
		/** ???????????? **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		BaseOutput<List<Department>> de = departmentRpc.findByUserId(userTicket.getId());
		/*
		 * String depStr = "";
		 * 
		 * if (de.getData()==null) {//????????????????????? throw new DemandExceptions("?????????????????????"); }
		 * 
		 * if (de.getData() == null) { depStr = "cp"; } else if (de.getData().size() >
		 * 0) { depStr = de.getData().get(0).getName(); if (depStr.indexOf("??????") > -1) {
		 * depStr = depStr.substring(0, depStr.length() - 2); } else if
		 * (depStr.indexOf("???") > -1) { depStr = depStr.substring(0, depStr.length() -
		 * 1); } depStr = GetFirstCharUtil.getFirstSpell(depStr).toUpperCase(); }
		 */

		newDemand.setSerialNumber(this.numberGenerator.get());// ???????????????????????????/*20200703????????????????????????**/
		newDemand.setCreateDate(new Date());
		if (newDemand.getStatus() != null && newDemand.getStatus() == (byte) DemandStatus.APPROVING.getCode()) {
			newDemand.setStatus((byte) DemandStatus.APPROVING.getCode());// ????????????????????????
			newDemand.setProcessType(DemandProcessStatus.SUBMIT.getCode());
		} else {
			newDemand.setStatus((byte) DemandStatus.NOTSUBMIT.getCode());
			newDemand.setProcessType(DemandProcessStatus.CREATE.getCode());
		}

		int rows = this.insertSelective(newDemand);
		if (rows <= 0) {
			throw new DemandExceptions("??????????????????");
		}
		return rows;
	}

	@Override
	public void setDailyBegin() {
		Sequence sequenceQuery = DTOUtils.newDTO(Sequence.class);
		sequenceQuery.setType(DEMAND_NUMBER_GENERATOR_TYPE);
		Sequence sequence = sequenceMapper.selectOne(sequenceQuery);
		sequence.setNumber(1);
		this.sequenceMapper.updateByPrimaryKey(sequence);
	}

	@Override
	public int submint(Demand newDemand) throws DemandExceptions {
		/** ???????????? **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Demand selectDeman = null;
		// ???????????????????????????
		int rows = 0;
		if (newDemand.getId() == null) {
			int testNum = this.addNewDemand(newDemand);
			if (testNum > 0) {
				System.out.println(JSONObject.toJSON(newDemand));
				selectDeman = newDemand; // this.list(newDemand).get(0);
			} else {
				throw new DemandExceptions("??????????????????:??????????????????");
			}
		} else {
			selectDeman = demandMapper.selectByPrimaryKey(newDemand.getId());
			if (selectDeman == null) {
				throw new DemandExceptions("?????????????????????!");
			}
			selectDeman.setBelongProId(newDemand.getBelongProId());
			selectDeman.setBelongSysId(newDemand.getBelongSysId());
			selectDeman.setContent(newDemand.getContent());
			selectDeman.setDocumentUrl(newDemand.getDocumentUrl());
			selectDeman.setReason(newDemand.getReason());
			selectDeman.setName(newDemand.getName());
			selectDeman.setType(newDemand.getType());
			selectDeman.setFinishDate(newDemand.getFinishDate());
			selectDeman.setSubmitDate(new Date());
			this.update(selectDeman);
		}
		// Long departmentManagerId = this.departmentManagerId(selectDeman.getUserId());
		// ????????????????????????
		Map<String, String> variables = new HashMap<>();
		variables.put(BpmConsts.DEMAND_CODE, selectDeman.getSerialNumber());
		// variables.put("departmentManagerId", departmentManagerId.toString());

		// ????????????
		BaseOutput<ProcessInstanceMapping> processInstanceOutput = myRuntimeRpc.startProcessInstanceByKey(BpmConsts.PROCESS_DEFINITION_KEY, selectDeman.getSerialNumber(), userTicket.getId().toString(),
				new HashMap<String,Object>());
		if (!processInstanceOutput.isSuccess()) {
			throw new DemandExceptions(processInstanceOutput.getMessage());
		}
		// ???????????????????????????????????????id???????????????id
		ProcessInstanceMapping processInstance = processInstanceOutput.getData();
		selectDeman.setProcessDefinitionId(processInstance.getProcessDefinitionId());
		selectDeman.setProcessInstanceId(processInstance.getProcessInstanceId());
		selectDeman.setStatus((byte) DemandStatus.APPROVING.getCode());
		selectDeman.setProcessType(DemandProcessStatus.SUBMIT.getCode());
		selectDeman.setModificationTime(new Date());
		rows = this.demandMapper.updateByPrimaryKey(selectDeman);
		if (rows <= 0) {
			throw new DemandExceptions("??????????????????");
		}
		// ?????????????????????
		this.sendMailByGroup(selectDeman.getSerialNumber(), DEMAND_OM_GROUP_ID, "????????????????????????");

		return 1;
	}

	@Override
	public EasyuiPageOutput listPageForUser(DemandDto selectDemand) {
		/** ???????????? **/
		try {
			if (selectDemand.getSubmitDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar c = Calendar.getInstance();
				c.setTime(selectDemand.getSubmitDate());
				selectDemand.setStartTime(sdf.parse(sdf.format(selectDemand.getSubmitDate())));
				c.add(Calendar.DAY_OF_MONTH, 1);
				selectDemand.setEndTime(sdf.parse(sdf.format(c.getTime())));
				selectDemand.setSubmitDate(null);
			}
			selectDemand.setFilterStatus((byte) 5);
			List<Demand> list = this.listByExample(selectDemand);
			List<DemandDto> dtos = new ArrayList<>(list.size());
			// ???????????????
			list.forEach(d -> {
				try {

					User user = AlmCache.getInstance().getAllUserMap().get(d.getUserId());
					// ????????????????????????????????????????????????????????????
					if(user == null) {
						user = AlmCache.getInstance().getAllUserMap().get(618L);
					}
					if (this.ifShowInList(user.getFirmCode())) {
						return;
					}

					DemandDto newDemandDto = new DemandDto();
					BeanUtils.copyProperties(newDemandDto, d);
					if (user != null) {
						/*
						 * Department department =
						 * AlmCache.getInstance().getDepMap().get(user.getDepartmentId()); if
						 * (department != null) { newDemandDto.setDepartmentId(department.getId());
						 * newDemandDto.setDepartmentName(department.getName()); while
						 * (department.getParentId() != null) { department =
						 * AlmCache.getInstance().getDepMap().get(department.getParentId()); } if
						 * (department != null) {
						 * newDemandDto.setDepartmentFirstName(department.getName()); } }
						 */
						// ????????????????????????????????????????????????
						Firm firm = AlmCache.getInstance().getFirmMap().get(user.getFirmCode());
						if (firm != null) {
							newDemandDto.setDepartmentId(firm.getId());
							newDemandDto.setDepartmentName(firm.getName());
							newDemandDto.setDepartmentFirstName(firm.getName());
							/*
							 * while ( firm.getParentId()!= null&&!firm.getParentId().equals("0")) { firm =
							 * firmRpc.getById(firm.getParentId()).getData(); firm =
							 * AlmCache.getInstance().getFirmMap().get(firm.getCode()); } if (firm != null)
							 * { newDemandDto.setDepartmentFirstName(firm.getName()); }
							 */
						}
					}
					// ?????????????????????
					newDemandDto.setProcessFlag(this.isBackEdit(d));
					newDemandDto.setProcessBtnFlag(this.isBtnType(d));
					dtos.add(newDemandDto);
				} catch (Exception e) {
					e.getMessage();
				}

			});
			this.bpmcUtil.fitLoggedUserIsCanHandledProcess(dtos);
			JSONObject projectProvider = new JSONObject();
			projectProvider.put("provider", "projectProvider");
			Map<Object, Object> metadata = new HashMap<Object, Object>();
			metadata.put("belongSysId", projectProvider);

			JSONObject memberProvider = new JSONObject();
			memberProvider.put("provider", "memberForAllProvider");
			metadata.put("userId", memberProvider);

			JSONObject demandStateProvider = new JSONObject();
			demandStateProvider.put("provider", "demandStateProvider");
			metadata.put("status", demandStateProvider);

			JSONObject demandTypeProvider = new JSONObject();
			demandTypeProvider.put("provider", "demandTypeProvider");
			metadata.put("type", demandTypeProvider);

			JSONObject projectSysProvider = new JSONObject();
			projectSysProvider.put("provider", "projectSysProvider");
			metadata.put("belongSysId", projectSysProvider);

			JSONObject datetimeProvider = new JSONObject();
			datetimeProvider.put("provider", "datetimeProvider");
			metadata.put("createDate", datetimeProvider);
			metadata.put("submitDate", datetimeProvider);
			metadata.put("finishDate", datetimeProvider);
			selectDemand.setMetadata(metadata);
			List dtoDatesValue = ValueProviderUtils.buildDataByProvider(selectDemand, dtos);
			long total = dtos instanceof Page ? ((Page) dtos).getTotal() : dtos.size();
			return new EasyuiPageOutput(total, dtoDatesValue);
		} catch (Exception e) {
			e.getMessage();
		}
		return 	new EasyuiPageOutput(0L, Collections.emptyList());


	}

	@Override
	public List<Demand> queryDemandListToProject(Long projectId) {
		return this.getActualDao().selectDemandListToProject(projectId, DemandProjectStatus.NOASSOCIATED.getValue());
	}

	@Override
	public List<Demand> queryDemandListByIds(String ids) {
		DemandDto demandDto = new DemandDto();
		String[] split = ids.split(",");
		List<Long> demandIds = new ArrayList<Long>();
		for (String id : split) {
			if (!WebUtil.strIsEmpty(id)) {
				demandIds.add(Long.valueOf(id));
			}
		}
		demandDto.setIds(demandIds);
		List<Demand> selectByExampleExpand = this.getActualDao().selectByExampleExpand(demandDto);
		return selectByExampleExpand;
	}

	@Override
	public List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id, Integer type) {

		DemandProject demandProject = new DemandProject();
		if (type == DemandProjectType.APPLY.getValue()) {
			ProjectApply apply = projectApplyMapper.selectByPrimaryKey(id);
			if (apply.getStatus() == AlmConstants.ApplyState.PASS.getCode()) {
				Project project = DTOUtils.newDTO(Project.class);
				project.setApplyId(apply.getId());
				Project selectOne = this.projectMapper.selectOne(project);
				ProjectVersion projectVesion = DTOUtils.newDTO(ProjectVersion.class);
				projectVesion.setProjectId(selectOne.getId());
				projectVesion.setOrder("asc");
				projectVesion.setSort("id");
				List<ProjectVersion> selectProjectVesions = this.projectVesionMapper.select(projectVesion);
				if (selectProjectVesions != null && selectProjectVesions.size() > 0) {
					demandProject.setVersionId(selectProjectVesions.get(0).getId());
				} else {
					return null;
				}
			} else if (apply.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
				Project project = DTOUtils.newDTO(Project.class);
				project.setApplyId(apply.getId());
				Project selectOne = this.projectMapper.selectOne(project);
				ProjectVersion projectVesion = DTOUtils.newDTO(ProjectVersion.class);
				projectVesion.setProjectId(selectOne.getId());
				projectVesion.setOrder("asc");
				projectVesion.setSort("id");
				List<ProjectVersion> selectProjectVesions = this.projectVesionMapper.select(projectVesion);
				if (selectProjectVesions != null && selectProjectVesions.size() > 0) {
					demandProject.setVersionId(selectProjectVesions.get(0).getId());
				} else {
					demandProject.setProjectNumber(apply.getNumber());
				}
			} else {
				demandProject.setProjectNumber(apply.getNumber());
			}

		}
		if (type == DemandProjectType.VERSION.getValue()) {
			demandProject.setVersionId(id);
		}
		if (type == DemandProjectType.WORKORDER.getValue()) {
			demandProject.setWorkOrderId(id);
		}
		demandProject.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
		List<DemandProject> selectByExample = demandProjectMapper.select(demandProject);
		List<Long> ids = new ArrayList<Long>();
		for (DemandProject selectDemandProject : selectByExample) {
			ids.add(selectDemandProject.getDemandId());
		}
		if (ids == null || ids.size() <= 0) {
			return null;
		}
		Example example = new Example(Demand.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		List<Demand> selectByExampleExpand = this.getActualDao().selectByExample(example);
		return selectByExampleExpand;
	}

	@Override
	public DemandDto getDetailViewData(Long id) throws Exception {
		Demand demand = this.getActualDao().selectByPrimaryKey(id);
		/*
		 * DemandProject demandProject=new DemandProject();
		 * demandProject.setDemandId(demand.getId());
		 * demandProject.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
		 * DemandProject selectOne = this.demandProjectMapper.selectOne(demandProject);
		 * if(selectOne!=null) {
		 * 
		 * }
		 */
		// ??????dto
		DemandDto demandDto = new DemandDto();
		BeanUtils.copyProperties(demandDto, demand);
		BaseOutput<User> userBase = this.userRpc.findUserById(demand.getUserId());
		if (userBase.getData() != null) {
			User user = userBase.getData();
			demandDto.setUserPhone(user.getCellphone());
			demandDto.setUserName(user.getRealName());
			/*
			 * BaseOutput<Department> departmentBase =
			 * this.departmentRpc.get(user.getDepartmentId()); if (departmentBase.getData()
			 * != null) { demandDto.setDepartmentId(departmentBase.getData().getId());
			 * demandDto.setDepartmentName(departmentBase.getData().getName());
			 * BaseOutput<Department> firstDepartment =
			 * this.departmentRpc.getFirstDepartment(departmentBase.getData().getId()); if
			 * (firstDepartment.getData() != null) {
			 * demandDto.setDepartmentFirstName(firstDepartment.getData().getName()); } }
			 */

			// ?????????????????????
			Firm firm = AlmCache.getInstance().getFirmMap().get(user.getFirmCode());
			if (firm != null) {
				demandDto.setDepartmentId(firm.getId());
				demandDto.setDepartmentName(firm.getName());
				while (firm.getParentId() != null && !firm.getParentId().equals("0")) {
					firm = firmRpc.getById(firm.getParentId()).getData();
					firm = AlmCache.getInstance().getFirmMap().get(firm.getCode());
				}
				if (firm != null) {
					demandDto.setDepartmentFirstName(firm.getName());
				}
			}
			BaseOutput<Firm> firm2 = this.firmRpc.getByCode(user.getFirmCode());
			if (firm2.getData() != null) {
				demandDto.setFirmName(firm2.getData().getName());
			}
		}
		// ?????????????????????????????????
		// ??????????????????????????????????????????????????????
		demandDto.setProcessFlag(this.isBackEdit(demand));

		return demandDto;
	}

	@Override
	public int finishStutas(Long id, boolean approved) throws DemandExceptions {
		Demand selectDemand = new Demand();
		selectDemand = this.get(id);
		String valProcess = selectDemand.getProcessInstanceId();

		// ???????????????????????????
		TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
		taskDto.setProcessInstanceBusinessKey(valProcess);
		BaseOutput<List<TaskMapping>> output = taskRpc.list(taskDto);
		if (!output.isSuccess()) {
			throw new DemandExceptions(output.getMessage());
		}
		List<TaskMapping> taskMappings = output.getData();
		// ??????????????????????????????????????????
		if (CollectionUtils.isEmpty(taskMappings)) {
			throw new DemandExceptions("???????????????????????????");
		}
		// ???????????????????????????????????????????????????
		// ?????????????????????????????????TaskDefKey?????????????????????
		TaskMapping taskMapping = taskMappings.get(0);
		// ????????????????????????
		Map<String, Object> variables = new HashMap<>(1);
		variables.put("approved", approved + "");
		if (valProcess != null) {// ????????????
			// ????????????????????????
			myTaskRpc.complete(taskMapping.getId(), variables);
		}

		selectDemand.setStatus((byte) DemandStatus.COMPLETE.getCode());
		int i = this.update(selectDemand);
		return i;
	}

	@Override
	@Transactional
	public int logicDelete(Long id) throws DemandExceptions {
		Demand selectDemand = new Demand();
		selectDemand = this.get(id);
		if (selectDemand.getStatus().equals((byte) DemandStatus.COMPLETE.getCode())) {
			throw new DemandExceptions("???????????????????????????????????????");
		}
		if (selectDemand.getStatus().equals((byte) DemandStatus.APPROVING.getCode())) {
			throw new DemandExceptions("???????????????????????????????????????");
		}
		String valProcess = selectDemand.getProcessInstanceId();
		BaseOutput<String> outPut = null;
		if (valProcess != null) {// ????????????????????????????????????
			// ????????????????????????
			EventReceivedDto dto = DTOUtils.newDTO(EventReceivedDto.class);
			dto.setEventName("demandDeleteMsg");
			dto.setProcessInstanceId(selectDemand.getProcessInstanceId());
			outPut = eventRpc.messageEventReceived(dto);
			//outPut = taskRpc.messageEventReceived("demandDeleteMsg", selectDemand.getProcessInstanceId(), null);
		}
		selectDemand.setStatus((byte) DemandStatus.DELETE.getCode());
		int i = this.update(selectDemand);
		return i;
	}

	@Override
	public Demand getByCode(String code) {
		Demand condition = new Demand();
		condition.setSerialNumber(code);
		List<Demand> list = listByExample(condition);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	@Override
	public BaseOutput submitApproveAndAccept(Long executorId, String taskId) {
		// ????????????
		BaseOutput<String> output = this.myTaskRpc.complete(taskId, new HashMap<String, Object>() {
			{
				put("AssignExecutorId", executorId.toString());
			}
		});

		return output;
	}

	@Override
	public BaseOutput submitApproveForAccept(String code, String taskId, Long executorId) {

		BaseOutput<String> output;
		output = this.myTaskRpc.complete(taskId, new HashMap<String, Object>() {
			{
				put("AssignExecutorId", executorId.toString());
				put("approved", "true");
			}
		});
		// ????????????????????????
		this.sendMailByGroup(code, DEMAND_OM_GROUP_ID, "???????????????????????????");
		// ????????????????????????
		DemandDto demandDto = null;
		Set<String> emails = new HashSet<>();
		try {
			demandDto = this.getDetailViewData(this.getByCode(code).getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		emails.add(AlmCache.getInstance().getUserMap().get(executorId).getEmail());
		this.sendMail(demandDto, "?????????????????????", emails);
		return output;
	}

	@Override
	public BaseOutput submitApproveForAssign(Long executorId, String taskId) {

		// ????????????
		BaseOutput<String> output = this.myTaskRpc.complete(taskId, new HashMap<String, Object>() {
			{
				put("AssignExecutorId", executorId.toString());
			}
		});

		return output;
	}

	@Override
	public BaseOutput submitApprove(String code, String taskId, Byte status, String processType,Integer imperative) {
		Demand selectDemand = null;
		if (status != null) {
			selectDemand = this.getByCode(code);
			selectDemand.setStatus(status);
			this.update(selectDemand);
		}
		if (processType != null) {
			selectDemand = this.getByCode(code);
			selectDemand.setProcessType(processType);
			this.update(selectDemand);
		}
		if (null != imperative) {
			selectDemand.setImperative(imperative);
			this.update(selectDemand);
		}
		Map<String, Object> variables = new HashMap<>();
		variables.put("approved", "true");
		BaseOutput out = myTaskRpc.complete(taskId, variables);
		// ???????????????????????????????????????
		if (processType.equals(DemandProcessStatus.DEPARTMENTMANAGER.getCode())) {
			this.sendMailByGroup(code, DEMAND_DEP_ACCPET_GROUP_ID, "????????????????????????");
		}
		// ????????????????????????????????????????????????????????????
		if (processType.equals(DemandProcessStatus.FEEDBACK.getCode())) {
			this.sendMailByGroup(code, DEMAND_MANAGER_GROUP_ID, "????????????????????????");
		}
		// ???????????????
		if (processType.equals(DemandProcessStatus.DEMANDMANAGER.getCode())) {
			DemandDto demandDto = null;
			Set<String> emails = new HashSet<>();
			try {
				demandDto = this.getDetailViewData(selectDemand.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			emails.add(AlmCache.getInstance().getAllUserMap().get(selectDemand.getId()).getEmail());
			this.sendMail(demandDto, "?????????????????????", emails);
		}
		return out;
	}

	@Override
	public BaseOutput rejectApprove(String code, String taskId, String rejectType) {
		Map<String, Object> variables = new HashMap<>();
		Demand demand = this.getByCode(code);
		if (rejectType.equals(DemandProcessStatus.BACKANDEDIT_ACCPET.getCode())) {
			demand.setProcessType(DemandProcessStatus.BACKANDEDIT_ACCPET.getCode());// ??????????????????
		} else if (rejectType.equals(DemandProcessStatus.BACKANDEDIT_MANAGER.getCode())) {
			demand.setProcessType(DemandProcessStatus.BACKANDEDIT_MANAGER.getCode());// ??????????????????
		} else {
			demand.setProcessType(DemandProcessStatus.BACKANDEDIT.getCode());// ??????????????????
		}

		this.update(demand);
		variables.put("approved", "false");
		variables.put("AssignExecutorId", demand.getUserId().toString());
		// ???????????????
		DemandDto demandDto = null;
		Set<String> emails = new HashSet<>();
		try {
			demandDto = this.getDetailViewData(demand.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		emails.add(AlmCache.getInstance().getAllUserMap().get(demand.getUserId()).getEmail());
		this.sendMail(demandDto, "???????????????", emails);
		return myTaskRpc.complete(taskId, variables);
	}

	@Override
	public BaseOutput rejectApproveForFeedback(String code, String taskId, String rejectType, Long executorId) {
		// ????????????
		BaseOutput<String> output = this.myTaskRpc.complete(taskId, new HashMap<String, Object>() {
			{
				put("AssignExecutorId", executorId.toString());
				put("approved", "false");
			}
		});
		// ????????????
		Demand selectDemand = this.getByCode(code);
		DemandDto demandDto = null;
		Set<String> emails = new HashSet<>();
		try {
			demandDto = this.getDetailViewData(selectDemand.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		emails.add(AlmCache.getInstance().getAllUserMap().get(executorId).getEmail());
		this.sendMail(demandDto, "????????????", emails);
		this.sendMailByGroup(code, DEMAND_DEP_ACCPET_GROUP_ID, "????????????");
		return output;
	}

	@Override
	public BaseOutput reSubmint(Demand newDemand, String taskId) throws DemandExceptions {
		Demand selectDeman = demandMapper.selectByPrimaryKey(newDemand.getId());
		if (selectDeman == null) {
			throw new DemandExceptions("?????????????????????!");
		}
		selectDeman.setBelongProId(newDemand.getBelongProId());
		selectDeman.setBelongSysId(newDemand.getBelongSysId());
		selectDeman.setContent(newDemand.getContent());
		selectDeman.setDocumentUrl(newDemand.getDocumentUrl());
		selectDeman.setReason(newDemand.getReason());
		selectDeman.setName(newDemand.getName());
		selectDeman.setType(newDemand.getType());
		selectDeman.setFinishDate(newDemand.getFinishDate());
		selectDeman.setProcessType(DemandProcessStatus.DEPARTMENTMANAGER.getCode());
		this.update(selectDeman);
		// Long departmentManagerId = this.departmentManagerId(selectDeman.getUserId());
		// ????????????
		/*
		 * BaseOutput<String> output = this.myTaskRpc.complete(taskId, new HashMap<String,
		 * Object>() { { put("departmentManagerId", departmentManagerId.toString()); }
		 * });
		 */
		BaseOutput<String> output = this.taskRpc.complete(taskId);
		// ?????????????????????
		this.sendMailByGroup(selectDeman.getSerialNumber(), DEMAND_OM_GROUP_ID, "??????????????????????????????");
		return output;
	}

	public int isBackEdit(Demand demand) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (demand.getProcessType() == null) {
			return 0;
		}
		// ???????????????1???????????????????????????????????? 2?????????????????????????????????
		if (demand.getProcessType().equals(DemandProcessStatus.BACKANDEDIT.getCode()) && demand.getUserId().equals(userTicket.getId())) {
			return 1;
		}
		if (demand.getProcessType().equals(DemandProcessStatus.BACKANDEDIT_MANAGER.getCode()) && demand.getUserId().equals(userTicket.getId())) {
			return 1;
		}
		if (demand.getProcessType().equals(DemandProcessStatus.BACKANDEDIT_ACCPET.getCode()) && demand.getUserId().equals(userTicket.getId())) {
			return 1;
		}
		return 0;
	}

	public int isBtnType(Demand demand) {
		if (demand.getProcessType() == null) {
			return 0;
		}
		// ?????????????????????????????????????????????2?????????
		if (demand.getProcessType().equals(DemandProcessStatus.ASSIGN.code) || demand.getProcessType().equals(DemandProcessStatus.ACCEPT.code)) {
			return 1;
		}

		return 0;
	}

	@Override
	public void saveOprationRecord(String demandCode, String description, int operationValue, String operationName, ApproveResult result) throws DemandExceptions {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		// ????????????????????????
		DemandOperationRecord record = new DemandOperationRecord();
		record.setDemandCode(demandCode);
		record.setDescription(description);
		record.setOperationName(operationName);
		record.setOperationType(operationValue);
		record.setOpertateResult(result.getValue());
		record.setOperatorId(userTicket.getId());
		record.setOperateTime(new Date());
		int rows = this.operationRecordMapper.insertSelective(record);
		if (rows <= 0) {
			throw new DemandExceptions("????????????????????????");
		}

	}

	@Override
	public EasyuiPageOutput getOprationRecordList(String demandCode) {

		DemandOperationRecord operationRecord = new DemandOperationRecord();
		operationRecord.setDemandCode(demandCode);

		List<DemandOperationRecord> opRecords = this.operationRecordMapper.select(operationRecord);
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberForAllProvider");
		metadata.put("operatorId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("operateTime", datetimeProvider);

		metadata.put("opertateResult", "operationResultProvider");
		List<Map> recordList;
		try {
			recordList = ValueProviderUtils.buildDataByProvider(metadata, opRecords);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return new EasyuiPageOutput(Long.valueOf(recordList.size()), recordList);
	}

	@Override
	public Long departmentManagerId(Long applyId) throws DemandExceptions {

		BaseOutput<User> userOut = userRpc.get(applyId);
		if (!userOut.isSuccess()) {
			throw new DemandExceptions("???????????????????????????" + userOut.getMessage());
		}
		User u = userOut.getData();
		BaseOutput<List<User>> firmUsersOut = userRpc.findCurrentFirmUsersByResourceCode(u.getFirmCode(), "departmentApprove");// TODO:departmentApprove?????????????????????
		if (!firmUsersOut.isSuccess()) {
			throw new DemandExceptions("????????????????????????????????????" + firmUsersOut.getMessage());
		}
		User departmentManager = DTOUtils.newInstance(User.class);
		if (firmUsersOut.getData().size() <= 0) {
			throw new DemandExceptions("??????????????????????????????????????????????????????");
		} else {
			departmentManager = firmUsersOut.getData().get(0);
		}

		return departmentManager.getId();
	}

	public DemandServiceImpl() {
		super();
		InputStream in = null;
		try {
			Resource res = new ClassPathResource("conf/demandMailContentTemplate.html");
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

	private void sendMailByGroup(String code, Long groupId, String title) {
		// ????????????
		Set<String> emails = new HashSet<>();

		BaseOutput<List<User>> userList = userRpc.listUserByRoleId(groupId);
		List<User> executors = userList.getData();
		if (userList.getCode().equals("200")) {
			executors.forEach(e -> emails.add(AlmCache.getInstance().getAllUserMap().get(e.getId()).getEmail()));
		}
		// ??????????????????
		DemandDto demandDto = null;
		try {
			demandDto = this.getDetailViewData(this.getByCode(code).getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.sendMail(demandDto, title, emails);
	}

	/**
	 * ????????????
	 * 
	 * @param demand  ????????????
	 * @param subject ????????????
	 * @param to      ????????????
	 */
	private void sendMail(DemandDto demand, String subject, Collection<String> to) {

		// System.out.println(demand.getSerialNumber()+">>>"+demand.getName()+">>>"+demand.getProcessType());
		// System.out.println(subject+">>>"+to.toString());
		String content;

		// to.clear();
		// to.add("lijing@diligrp.com");
		try {
			// ??????????????????-????????????
			Template template = this.groupTemplate.getTemplate(this.contentTemplate);
			template.binding("model", parseViewModel(demand));
			// ??????????????????-??????????????????
			DemandOperationRecord operationRecords = new DemandOperationRecord();
			operationRecords.setDemandCode(demand.getSerialNumber());
			List<DemandOperationRecord> list = this.operationRecordMapper.select(operationRecords);
			template.binding("recordsList", this.buildOperationRecordViewModel(list));

			template.binding("uapContextPath", this.uapContextPath);

			content = template.render();
			// System.out.println(content);
			// ???????????????
			to.forEach(t -> {
				// ??????
				try {
					this.mailManager.sendMail(this.mailFrom, t, content, true, subject, null);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			});
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	@SuppressWarnings("rawtypes")
	private List<Map> buildOperationRecordViewModel(List<DemandOperationRecord> list) {
		Map<Object, Object> metadata = new HashMap<>();
		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("operatorId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("operateTime", datetimeProvider);

		metadata.put("opertateResult", "operationResultProvider");
		try {
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	private boolean ifShowInList(String fireCode) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		String userFirmCode = userTicket.getFirmCode();

		if (userFirmCode.equals(fireCode)) {
			return false;
		}

		if (userFirmCode.equals(FIRECODE_SZPT) || userFirmCode.equals(FIRECODE_JYGL)) {
			return false;
		}

		return true;
	}
}