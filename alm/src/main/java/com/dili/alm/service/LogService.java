package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.Log;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:19:20.
 */
public interface LogService extends BaseService<Log, Long> {

	int updateLog( Long logId,String logText);

	EasyuiPageOutput listLogPage(Log log, String beginTime, String endTime);

	List<DataDictionaryValueDto> getLogModules();

	int insertLog(String logText, Integer logModule);
}