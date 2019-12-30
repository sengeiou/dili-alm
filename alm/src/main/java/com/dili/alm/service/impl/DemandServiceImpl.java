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
import com.dili.uap.sdk.domain.Department;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Sequence;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.exceptions.DemandExceptions;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DemandService;
import com.dili.alm.utils.GetFirstCharUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;
import com.github.pagehelper.Page;

import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
	private DepartmentRpc deptRpc;
	
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
	BaseOutput<List<Department>> de = deptRpc.findByUserId(userTicket.getId());
	String depStr = "";
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
		//判断是否是直接提交
		int rows =0;
		if (newDemand.getId()==null) {
			newDemand.setStatus((byte) DemandStatus.APPROVING.getCode());
			this.addNewDemand(newDemand);
		}
		Demand selectDeman = demandMapper.selectOne(newDemand);
		if (selectDeman==null) {
			throw new DemandExceptions("找不到当前需求!");
		}
		
		selectDeman.setStatus((byte) DemandStatus.APPROVING.getCode());
		rows=this.demandMapper.updateByPrimaryKey(selectDeman);
		
		if (rows<=0) {
			throw new DemandExceptions("新增需求失败");
		}
		return 1;
	}
	@Override
	public EasyuiPageOutput listPageForUser(Demand selectDemand) {
		selectDemand.setOrder("desc");
		selectDemand.setSort("created");
		List<Demand> list = demandMapper.selectDemandList(selectDemand);// 查询出来
		Page<Demand> page = (Page<Demand>) list;
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == selectDemand.getMetadata() ? new HashMap<>() : selectDemand.getMetadata();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("owner", memberProvider);

		selectDemand.setMetadata(metadata);
		try {
			List demandList = ValueProviderUtils.buildDataByProvider(selectDemand, list);
			EasyuiPageOutput demandEasyuiPageOutput = new EasyuiPageOutput(Integer.valueOf(Integer.parseInt(String.valueOf(page.getTotal()))), demandList);
			return demandEasyuiPageOutput;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
		DemandProject demandProject=new DemandProject();
		demandProject.setDemandId(demand.getId());
		demandProject.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
		DemandProject selectOne = this.demandProjectMapper.selectOne(demandProject);
		
		
		
		DemandDto demandDto =new DemandDto();
		BeanUtils.copyProperties(demandDto, demand);
		BaseOutput<List<Department>> departmentBase = this.departmentRpc.findByUserId(demand.getUserId());
		if(departmentBase.getData()!=null&&departmentBase.getData().size()>0) {
			Department department= departmentBase.getData().get(0);
			demandDto.setDepartmentId(department.getId());
			demandDto.setDepartmentName(department.getName());
		}
		BaseOutput<User> userBase = this.userRpc.findUserById(demand.getUserId());
		if(userBase.getData()!=null) {
			User user = userBase.getData();
			demandDto.setUserPhone(user.getCellphone());
		}
		return demandDto;
	}
}