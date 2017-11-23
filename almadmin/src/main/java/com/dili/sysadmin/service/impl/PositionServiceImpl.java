package com.dili.sysadmin.service.impl;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.dao.PositionMapper;
import com.dili.sysadmin.domain.Position;
import com.dili.sysadmin.domain.dto.DataDictionaryDto;
import com.dili.sysadmin.domain.dto.DataDictionaryValueDto;
import com.dili.sysadmin.rpc.DataDictrionaryRPC;
import com.dili.sysadmin.service.PositionService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-21 16:14:32.
 */
@Service
public class PositionServiceImpl extends BaseServiceImpl<Position, Long> implements PositionService {

	private static final String RANK_CODE = "rank";
	@Autowired
	private DataDictrionaryRPC dataDicionaryRPC;

	public PositionMapper getActualDao() {
		return (PositionMapper) getDao();
	}

	@Override
	public List<DataDictionaryValueDto> listRank() {
		BaseOutput<DataDictionaryDto> output = this.dataDicionaryRPC.findDataDictionaryByCode(RANK_CODE);
		if (!output.isSuccess()) {
			return null;
		}
		if (output.getData() == null) {
			return null;
		}
		return output.getData().getValues();
	}
}