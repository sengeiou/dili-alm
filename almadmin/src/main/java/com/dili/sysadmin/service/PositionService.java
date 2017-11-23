package com.dili.sysadmin.service;

import java.util.List;

import com.dili.ss.base.BaseService;
import com.dili.sysadmin.domain.Position;
import com.dili.sysadmin.domain.dto.DataDictionaryValueDto;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-21 16:14:32.
 */
public interface PositionService extends BaseService<Position, Long> {

	List<DataDictionaryValueDto> listRank();
}