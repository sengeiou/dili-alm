package com.dili.alm.service.impl;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.DemandMapper;
import com.dili.alm.dao.DemandProjectMapper;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.DemandProjectStatus;
import com.dili.alm.domain.DemandProjectType;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.service.DemandService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
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
    private ProjectApplyMapper projectApplyMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectVersionMapper projectVesionMapper;
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
		BaseOutput<List<Department>> findByUserId = this.departmentRpc.findByUserId(demand.getUserId());
		if(findByUserId.getData()!=null&&findByUserId.getData().size()>0) {
			Department department = findByUserId.getData().get(0);
			demandDto.setDepartmentId(department.getId());
			demandDto.setDepartmentName(department.getName());
		}
		return demandDto;
	}
}