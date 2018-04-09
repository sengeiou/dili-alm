package com.dili.alm.service.impl;

import com.dili.alm.dao.HardwareResourceApplyMapper;
import com.dili.alm.dao.ResourceEnvironmentMapper;
import com.dili.alm.domain.ApproveResult;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.ResourceEnvironment;
import com.dili.alm.domain.dto.HardwareResourceApplyUpdateDto;
import com.dili.alm.exceptions.HardwareResourceApplyException;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-20 17:22:08.
 */
@Service
public class HardwareResourceApplyServiceImpl extends BaseServiceImpl<HardwareResourceApply, Long>
		implements HardwareResourceApplyService {

	@Autowired
	private ResourceEnvironmentMapper rem;

	public HardwareResourceApplyMapper getActualDao() {
		return (HardwareResourceApplyMapper) getDao();
	}

	@Override
	public EasyuiPageOutput listEasyuiPageByExample(HardwareResourceApply domain, boolean useProvider)
			throws Exception {
		List<HardwareResourceApply> list = listByExample(domain);
		list.forEach(h -> {
			ResourceEnvironment record = DTOUtils.newDTO(ResourceEnvironment.class);
			record.setResourceId(h.getId());
			List<ResourceEnvironment> envList = this.rem.select(record);
			h.aset("envList", envList);
		});
		long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
		List results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, list) : list;
		return new EasyuiPageOutput(Integer.parseInt(String.valueOf(total)), results);
	}

	@Override
	public void saveOrUpdate(HardwareResourceApplyUpdateDto dto) throws HardwareResourceApplyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteHardwareResourceApply(Long applyId) throws HardwareResourceApplyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void submit(Long applyId) throws HardwareResourceApplyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void projectManagerApprove(Long applyId, Long projectManagerId, ApproveResult result, String description)
			throws HardwareResourceApplyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generalManagerApprove(Long applyId, Long generalManagerId, ApproveResult result, String description)
			throws HardwareResourceApplyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operationManagerApprove(Long applyId, Long operationManagerId, Set<Long> executors, String description)
			throws HardwareResourceApplyException {
		// TODO Auto-generated method stub
		
	}

}