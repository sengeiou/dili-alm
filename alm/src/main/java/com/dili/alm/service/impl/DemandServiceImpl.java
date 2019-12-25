package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants.DemandStatus;
import com.dili.alm.dao.DemandMapper;
import com.dili.alm.dao.DemandProjectMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.SequenceMapper;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.DemandProjectStatus;
import com.dili.alm.domain.DemandProjectType;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.Sequence;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.exceptions.DemandExceptions;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.service.DemandService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.github.pagehelper.Page;

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
    private ProjectMapper projectMapper;
	
	
	@Autowired
	private DemandMapper demandMapper;
	

    /**
     * 添加需求
     * @throws DemandExceptions 
     */
    @Override
   public int addNewDemand(Demand newDemand) throws DemandExceptions{
    	this.numberGenerator.init();
	newDemand.setSerialNumber(""+this.numberGenerator.get());//TODO:加部门全拼获取自增
	newDemand.setCreateDate(new Date());
	newDemand.setStatus((byte) DemandStatus.NOTSUBMIT.getCode());//初始化状态为未提交
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
		int rows =0;
		if (newDemand.getId()==null) {
			this.addNewDemand(newDemand);
		}
		Demand selectDeman = demandMapper.selectOne(newDemand);
		if (selectDeman==null) {
			throw new DemandExceptions("找不到当前需求!");
		}
		
		selectDeman.setStatus((byte) DemandStatus.SUBMIT.getCode());
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
		List<Demand> list = this.listByExample(selectDemand);// 查询出来
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
			//List<Demand> results = this.TaskParseTaskSelectDto(list, true);// 转化为查询的DTO
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
		DemandDto demandDto=new DemandDto();
		DemandProject demandProject=new DemandProject();
		List<Long> ids =new ArrayList<Long>();
		if(type==DemandProjectType.PROJECRT.getValue()) {
			Project selectByPrimaryKey = this.projectMapper.selectByPrimaryKey(id);
			demandProject.setProjectNumber(selectByPrimaryKey.getSerialNumber());
		}
		if(type==DemandProjectType.VERSION.getValue()) {
			demandProject.setVersionId(id);
		}
		if(type==DemandProjectType.WORKORDER.getValue()) {
			demandProject.setWorkOrderId(id);
		}
		List<DemandProject> selectByExample = demandProjectMapper.selectByExample(demandProject);
		for (DemandProject selectDemandProject : selectByExample) {
			ids.add(selectDemandProject.getDemandId());
		}
		demandDto.setIds(ids);
		return this.getActualDao().selectByExampleExpand(demandDto);
		 
	}

	@Override
	public DemandDto getDetailViewData(Long id) {
		Demand demand = this.getActualDao().selectByPrimaryKey(id);
		DemandProject demandProject=new DemandProject();
		demandProject.getDemandId();
		DemandProject selectOne = this.demandProjectMapper.selectOne(demandProject);
		DemandDto demandDto = DTOUtils.as(demand, DemandDto.class);
		BaseOutput<List<Department>> findByUserId = this.departmentRpc.findByUserId(demand.getUserId());
		Department department = findByUserId.getData().get(0);
		demandDto.setDepartmentId(department.getId());
		demandDto.setDepartmentName(department.getName());
		return demandDto;
	}
   
}