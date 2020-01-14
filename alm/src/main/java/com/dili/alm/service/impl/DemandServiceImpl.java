package com.dili.alm.service.impl;

import com.dili.alm.constant.AlmConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants.DemandStatus;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.dao.DemandMapper;
import com.dili.alm.dao.DemandProjectMapper;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.SequenceMapper;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.DemandProjectStatus;
import com.dili.alm.domain.DemandProjectType;
import com.dili.alm.domain.FileType;
import com.dili.alm.domain.Files;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.Firm;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Sequence;
import com.dili.alm.domain.SystemDto;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.exceptions.DemandExceptions;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.FirmRpc;
import com.dili.alm.rpc.SysProjectRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DemandService;
import com.dili.alm.utils.ColumnUtil;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.GetFirstCharUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.FormRpc;
import com.dili.bpmc.sdk.rpc.RuntimeRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.BusinessException;
import com.dili.ss.exception.ParamErrorException;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.exception.NotLoginException;
import com.dili.uap.sdk.session.SessionContext;
import com.github.pagehelper.Page;

import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:32:14.
 */
@Service
public class DemandServiceImpl extends BaseServiceImpl<Demand, Long> implements DemandService {
	
	private static final String DEMAND_NUMBER_GENERATOR_TYPE = "demandNumberGenerator";
	
	@Qualifier(DEMAND_NUMBER_GENERATOR_TYPE)
	@Autowired
	private NumberGenerator numberGenerator;
    @Autowired
    RuntimeRpc runtimeRpc;
    @Autowired
    TaskRpc taskRpc;
	@Autowired
	private SequenceMapper sequenceMapper;
	
	    public DemandMapper getActualDao() {
        return (DemandMapper)getDao();
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
	private SysProjectRpc sysProjectRpc;
    @Autowired
    FormRpc formRpc;
	
	@Autowired
	private FirmRpc firmRpc;
	@Autowired
	private DemandMapper demandMapper;
	
	public static Object parseViewModel(DemandDto detailViewData) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("belongProId", projectProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
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

		List<Map> listMap = ValueProviderUtils.buildDataByProvider(metadata,
				Arrays.asList(detailViewData));
		return listMap.get(0);
	}
    /**
     * 添加需求
     * @throws DemandExceptions 
     */
    @Override
   public int addNewDemand(Demand newDemand) throws DemandExceptions{
    this.numberGenerator.init();
	/** 个人信息 **/
	UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
	BaseOutput<List<Department>> de = departmentRpc.findByUserId(userTicket.getId());
	String depStr = "";
/*	if (de.getData()==null) {//正确的部门校验
		throw new DemandExceptions("所属部门为空！");
	}*/
	if (de.getData()==null) {
		depStr="cp";
	}else
	if (de.getData().size()>0) {
		depStr = de.getData().get(0).getName();
		 if (depStr.indexOf("市场")>-1) {
			 depStr = depStr.substring(0,depStr.length()-2);
		}else if(depStr.indexOf("部")>-1){
			depStr = depStr.substring(0,depStr.length()-1);
		}
		 depStr = GetFirstCharUtil.getFirstSpell(depStr).toUpperCase();
	}

	
	newDemand.setSerialNumber(depStr+this.numberGenerator.get());//加部门全拼获取自增
	newDemand.setCreateDate(new Date());
	if (newDemand.getStatus()!=null&&newDemand.getStatus()==(byte) DemandStatus.APPROVING.getCode()) {
		newDemand.setStatus((byte) DemandStatus.APPROVING.getCode());//直接提交为申请单
	}else{
		newDemand.setStatus((byte) DemandStatus.NOTSUBMIT.getCode());
	}
	
	int rows = this.insertSelective(newDemand);
	if (rows <= 0) {
		throw new DemandExceptions("新增需求失败");
	}
	return 1;
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
		/** 个人信息 **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Demand selectDeman = null;
		//判断是否是直接提交
		int rows =0;
		if (newDemand.getId()==null) {
			newDemand.setStatus((byte) DemandStatus.APPROVING.getCode());
			this.addNewDemand(newDemand);
			selectDeman=this.list(newDemand).get(0);
		}else {
			selectDeman= demandMapper.selectByPrimaryKey(newDemand.getId());
			if (selectDeman==null) {
				throw new DemandExceptions("找不到当前需求!");
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

        //流程启动参数设置
        Map<String, Object> variables = new HashMap<>(1);
        variables.put(BpmConsts.DEMAND_CODE, selectDeman.getSerialNumber());
        //启动流程
        BaseOutput<ProcessInstanceMapping> processInstanceOutput = runtimeRpc.
        		startProcessInstanceByKey(BpmConsts.PROCESS_DEFINITION_KEY, 
        				selectDeman.getSerialNumber(), userTicket.getId().toString(), variables);
        if(!processInstanceOutput.isSuccess()){
           throw new DemandExceptions(processInstanceOutput.getMessage());
        }
        //回调，写入相关流程任务数据
        ProcessInstanceMapping processInstance = processInstanceOutput.getData();
        selectDeman.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        selectDeman.setProcessInstanceId(processInstance.getProcessInstanceId());
        selectDeman.setStatus((byte)DemandStatus.APPROVING.getCode());
        //修改需求状态，记录流程实例id和流程定义id
		rows=this.demandMapper.updateByPrimaryKey(selectDeman);
		
		if (rows<=0) {
			throw new DemandExceptions("提交需求失败");
		}
		return 1;
	}
	@Override
	public EasyuiPageOutput listPageForUser (Demand selectDemand) {
		
		try {
			DemandDto selectDemandDto =new DemandDto();
			Map<Object, Object> metadata = new HashMap<>();

			if(selectDemand.getBelongProId()!=null) {
				selectDemandDto.setBelongProId(selectDemand.getBelongProId());
			}
			if(!WebUtil.strIsEmpty(selectDemand.getName())) {
				selectDemandDto.setName(selectDemand.getName());
				
			}
			if(selectDemand.getType()!=null) {
				selectDemandDto.setType(selectDemand.getType());
			}
			if(selectDemand.getStatus()!=null) {
				selectDemandDto.setStatus(selectDemand.getStatus());
			}
			if(selectDemand.getSubmitDate()!=null) {
				String selectDate = DateUtil.getDate(selectDemand.getSubmitDate());
				selectDemandDto.setStartTime(selectDate+" 00:00:00");
				selectDemandDto.setEndTime(selectDate+" 23:59:59");
			}
			if(!WebUtil.strIsEmpty(selectDemand.getSort())) {
				String columnValue = ColumnUtil.getColumnValue(Demand.class,selectDemand.getSort());
				selectDemandDto.setSort(columnValue);
			}
			if(!WebUtil.strIsEmpty(selectDemand.getOrder())) {
				selectDemandDto.setOrder(selectDemand.getOrder());
			}
			if(selectDemand.getPage()!=null) {
				selectDemandDto.setPage(selectDemand.getPage());
			}
			if(selectDemand.getRows()!=null) {
				selectDemandDto.setRows(selectDemand.getRows());
			}
			List<Demand> dates =this.getActualDao().selectDemandList(selectDemandDto);
			int total=this.getActualDao().selectDemandListCount(selectDemandDto);
			DemandDto demandDto =new DemandDto();
			List<DemandDto> dtoDates = new ArrayList<DemandDto>();
			//循环塞部门
			dates.forEach(d -> {
				try {
					DemandDto newDemandDto =new DemandDto();
			    	BeanUtils.copyProperties(newDemandDto, d);
					BaseOutput<User> userBase = this.userRpc.findUserById(d.getUserId());
					User user = userBase.getData();
					if (user!=null) {
						BaseOutput<Department> departmentBase = this.departmentRpc.get(user.getDepartmentId());
						if(departmentBase.getData()!=null) {
							newDemandDto.setDepartmentId(departmentBase.getData().getId());
							newDemandDto.setDepartmentName(departmentBase.getData().getName());
							BaseOutput<Department> firstDepartment = this.departmentRpc.getFirstDepartment(departmentBase.getData().getId());
							if(firstDepartment.getData()!=null) {
								newDemandDto.setDepartmentFirstName(firstDepartment.getData().getName());
							}
						}
					}
					dtoDates.add(newDemandDto);
				} catch (Exception e) {
					e.getMessage();
				}

			});
			JSONObject projectProvider = new JSONObject();
			projectProvider.put("provider", "projectProvider");
			metadata.put("belongProId", projectProvider);

			JSONObject memberProvider = new JSONObject();
			memberProvider.put("provider", "memberProvider");
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
			demandDto.setMetadata(metadata);
			List dtoDatesValue = ValueProviderUtils.buildDataByProvider(demandDto, dtoDates);
            return new EasyuiPageOutput(total, dtoDatesValue);
		} catch (Exception e) {
			e.getMessage();
		}
		return null;
		
	}
	
	@Override
	public List<Demand> queryDemandListToProject(Long projectId) {
		return this.getActualDao().selectDemandListToProject(projectId, DemandProjectStatus.NOASSOCIATED.getValue());
	}

	@Override
	public List<Demand> queryDemandListByIds(String ids) {
		DemandDto demandDto=new DemandDto();
		String[] split = ids.split(",");
		List<Long> demandIds= new ArrayList<Long>();
		for (String id : split) {
			if(!WebUtil.strIsEmpty(id)) {
				demandIds.add(Long.valueOf(id));
			}
		}
		demandDto.setIds(demandIds);
		List<Demand> selectByExampleExpand = this.getActualDao().selectByExampleExpand(demandDto);
		return selectByExampleExpand;
	}

	@Override
	public List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id, Integer type) {
							
		DemandProject demandProject=new DemandProject();
		if(type==DemandProjectType.APPLY.getValue()) {
			ProjectApply apply = projectApplyMapper.selectByPrimaryKey(id);
			if(apply.getStatus()==AlmConstants.ApplyState.PASS.getCode()) {
				Project project = DTOUtils.newDTO(Project.class);
				project.setApplyId(apply.getId());
				Project selectOne = this.projectMapper.selectOne(project);
				ProjectVersion projectVesion = DTOUtils.newDTO(ProjectVersion.class);
				projectVesion.setProjectId(selectOne.getId());
				projectVesion.setOrder("asc");
				projectVesion.setSort("id");
				List<ProjectVersion> selectProjectVesions = this.projectVesionMapper.select(projectVesion);
				if(selectProjectVesions!=null&&selectProjectVesions.size()>0) {
					demandProject.setVersionId(selectProjectVesions.get(0).getId());	
				}else {
					return null;
				}
			}else if(apply.getStatus()==AlmConstants.ApplyState.APPROVE.getCode()){
				Project project = DTOUtils.newDTO(Project.class);
				project.setApplyId(apply.getId());
				Project selectOne = this.projectMapper.selectOne(project);
				ProjectVersion projectVesion = DTOUtils.newDTO(ProjectVersion.class);
				projectVesion.setProjectId(selectOne.getId());
				projectVesion.setOrder("asc");
				projectVesion.setSort("id");
				List<ProjectVersion> selectProjectVesions = this.projectVesionMapper.select(projectVesion);
				if(selectProjectVesions!=null&&selectProjectVesions.size()>0) {
					demandProject.setVersionId(selectProjectVesions.get(0).getId());	
				}else {
					demandProject.setProjectNumber(apply.getNumber());
				}
			}else{
				demandProject.setProjectNumber(apply.getNumber());
			}
			
		}
		if(type==DemandProjectType.VERSION.getValue()) {
			demandProject.setVersionId(id);
		}
		if(type==DemandProjectType.WORKORDER.getValue()) {
			demandProject.setWorkOrderId(id);
		}
		demandProject.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
		List<DemandProject> selectByExample = demandProjectMapper.select(demandProject);
		List<Long> ids =new ArrayList<Long>();
		for (DemandProject selectDemandProject : selectByExample) {
			ids.add(selectDemandProject.getDemandId());
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
/*		DemandProject demandProject=new DemandProject();
		demandProject.setDemandId(demand.getId());
		demandProject.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
		DemandProject selectOne = this.demandProjectMapper.selectOne(demandProject);
		if(selectOne!=null) {
			
		}*/
		//复制dto
		DemandDto demandDto =new DemandDto();
		BeanUtils.copyProperties(demandDto, demand);
		BaseOutput<User> userBase = this.userRpc.findUserById(demand.getUserId());
		if(userBase.getData()!=null) {
			User user = userBase.getData();
			demandDto.setUserPhone(user.getCellphone());
			demandDto.setUserName(user.getRealName());
			BaseOutput<Department> departmentBase = this.departmentRpc.get(user.getDepartmentId());
			if(departmentBase.getData()!=null) {
				demandDto.setDepartmentId(departmentBase.getData().getId());
				demandDto.setDepartmentName(departmentBase.getData().getName());
				BaseOutput<Department> firstDepartment = this.departmentRpc.getFirstDepartment(departmentBase.getData().getId());
				if(firstDepartment.getData()!=null) {
					demandDto.setDepartmentFirstName(firstDepartment.getData().getName());
				}
			}
			BaseOutput<Firm> firm = this.firmRpc.getByCode(user.getFirmCode());
			if(firm.getData()!=null){
				demandDto.setFirmName(firm.getData().getName());
			}
		}
		return demandDto;
	}
	
	@Override
	public int finishStutas(Long id,boolean approved) throws DemandExceptions {
    	Demand selectDemand = new Demand();
    	selectDemand=this.get(id);
    	String valProcess = selectDemand.getProcessInstanceId();
    	
        //根据业务号查询任务
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(valProcess);
        BaseOutput<List<TaskMapping>> output = taskRpc.list(taskDto);
        if(!output.isSuccess()){
            throw new DemandExceptions(output.getMessage());
        }
        List<TaskMapping> taskMappings = output.getData();
        //没有进行中的任务或任务已结束
        if(CollectionUtils.isEmpty(taskMappings)){
        	throw new DemandExceptions("未找到进行中的任务");
        }
        //默认没有并发流程，所以取第一个任务
        //如果有并发流程，需使用TaskDefKey来确认流程节点
        TaskMapping taskMapping = taskMappings.get(0);
        //流程启动参数设置
        Map<String, Object> variables = new HashMap<>(1);
        variables.put("approved", approved);
    	if (valProcess!=null) {//完成流程
			//发送消息通知流程
    		taskRpc.complete(taskMapping.getId(), variables);
		}
    	
    	selectDemand.setStatus((byte)DemandStatus.COMPLETE.getCode());
    	int i = this.update(selectDemand);
		return i;
	}
	
    @Override
    @Transactional
    public int logicDelete(Long id) throws DemandExceptions {
    	Demand selectDemand = new Demand();
    	selectDemand=this.get(id);
    	if (selectDemand.getStatus().equals((byte)DemandStatus.COMPLETE.getCode())) {
    		throw new DemandExceptions("需求已经通过申请，不能删除");
		}
    	String valProcess = selectDemand.getProcessInstanceId();
    	if (valProcess!=null) {//已经开启流程的，停止流程
    		 //发送消息通知流程
    		taskRpc.messageEventReceived("demandDeleteMsg", selectDemand.getProcessInstanceId(), null);
		}
    	selectDemand.setStatus((byte)DemandStatus.DELETE.getCode());
    	int i = this.update(selectDemand);
        return i;
    }
	@Override
	public Demand getByCode(String code) {
	    Demand condition = new Demand();
	    condition.setSerialNumber(code);;
	    List<Demand> list = listByExample(condition);
	    return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}
	
	@Override
	public BaseOutput submitApproveAndAccept(String code, String taskId,Long userId) {
	    Map<String,Object> variables = new HashMap<>();
	    variables.put("approved", "true");
	    Demand selectDemand = new Demand();
	    selectDemand=this.getByCode(code);
	    selectDemand.setReciprocateId(userId);
	    this.update(selectDemand);
	    return taskRpc.complete(taskId,variables);
	}
	
	@Override
	public BaseOutput submitApprove(String code, String taskId,Byte status) {
	    Demand condition = new Demand();
	    condition.setSerialNumber(code);;
	    List<Demand> list = listByExample(condition);
	    if (status!=null) {
		    Demand selectDemand = list.get(0);
		    selectDemand.setStatus(status);
		    this.update(selectDemand);
		}
	    Map<String,Object> variables = new HashMap<>();
	    variables.put("approved", "true");
		return taskRpc.complete(taskId,variables);
	}
	@Override
	public BaseOutput rejectApprove(String code, String taskId) {
		Map<String,Object> variables = new HashMap<>();
	    variables.put("approved", "false");
	    return taskRpc.complete(taskId,variables);
	}
	@Override
	public BaseOutput reSubmint(Demand newDemand, String taskId) throws DemandExceptions {
		// TODO Auto-generated method stub
		Demand selectDeman= demandMapper.selectByPrimaryKey(newDemand.getId());
		if (selectDeman==null) {
			throw new DemandExceptions("找不到当前需求!");
		}
		selectDeman.setBelongProId(newDemand.getBelongProId());
		selectDeman.setBelongSysId(newDemand.getBelongSysId());
		selectDeman.setContent(newDemand.getContent());
		selectDeman.setDocumentUrl(newDemand.getDocumentUrl());
		selectDeman.setReason(newDemand.getReason());
		selectDeman.setName(newDemand.getName());
		selectDeman.setType(newDemand.getType());
		selectDeman.setFinishDate(newDemand.getFinishDate());
		this.update(selectDeman);
		Map<String,Object> variables = new HashMap<>();
	    variables.put("approved", "false");
	    return taskRpc.complete(taskId,variables);
	}

}