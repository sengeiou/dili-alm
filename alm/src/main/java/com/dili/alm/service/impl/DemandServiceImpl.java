package com.dili.alm.service.impl;

import com.dili.alm.dao.DemandMapper;
import com.dili.alm.dao.DemandProjectMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.DemandProjectStatus;
import com.dili.alm.domain.DemandProjectType;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.service.DemandService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:32:14.
 */
@Service
public class DemandServiceImpl extends BaseServiceImpl<Demand, Long> implements DemandService {

    public DemandMapper getActualDao() {
        return (DemandMapper)getDao();
    }
	@Autowired
	private DepartmentRpc departmentRpc;
    @Autowired
    private DemandProjectMapper demandProjectMapper;
    @Autowired
    private ProjectMapper projectMapper;
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