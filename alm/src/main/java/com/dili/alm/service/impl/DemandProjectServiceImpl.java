package com.dili.alm.service.impl;

import com.dili.alm.dao.DemandProjectMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.DemandProjectStatus;
import com.dili.alm.domain.FileType;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.DemandProjectDto;
import com.dili.alm.exceptions.ProjectVersionException;
import com.dili.alm.service.DemandProjectService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;

import java.util.List;

import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:32:14.
 */
@Service
public class DemandProjectServiceImpl extends BaseServiceImpl<DemandProject, Long> implements DemandProjectService {

    public DemandProjectMapper getActualDao() {
        return (DemandProjectMapper)getDao();
    }
    @Autowired
    private ProjectMapper projectMapper;
	@Override
	public BaseOutput insertDemandProjectDto(DemandProjectDto demandProjectDto) {
		demandProjectDto.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
		try {
			List<Long> demandIds = demandProjectDto.getDemandIds();
			for (Long demandId : demandIds) {
				demandProjectDto.setDemandId(demandId);
				int insertExact = this.getActualDao().insertExact(demandProjectDto);
				if(insertExact==0) {
					throw new RuntimeException("插入关联失败");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return BaseOutput.success("新增失败");
		}
		return BaseOutput.success("新增成功");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public BaseOutput deleteByProjectOrVersionOrWorkOrder(Long projectId, Long versionId, Long workOrderId) {
		// TODO Auto-generated method stub
		DemandProject demandProject =new DemandProject();
		if(projectId!=null&&versionId==null&&workOrderId==null) {
			Project selectByPrimaryKey = this.projectMapper.selectByPrimaryKey(projectId);
			demandProject.setProjectNumber(selectByPrimaryKey.getSerialNumber());
		}
		if(versionId!=null&&workOrderId==null) {
			demandProject.setVersionId(versionId);
		}
		if(workOrderId!=null) {
			demandProject.setWorkOrderId(workOrderId);
		}
		try {
			List<DemandProject> selectByExample = this.getActualDao().selectByExample(demandProject);
			int deleteByExample = this.getActualDao().deleteByExample(demandProject);
			if(selectByExample.size()!=deleteByExample) {
				throw new RuntimeException("删除关联失败");
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return BaseOutput.success("删除失败");
		}
		return BaseOutput.success("删除成功");
	}
	
}