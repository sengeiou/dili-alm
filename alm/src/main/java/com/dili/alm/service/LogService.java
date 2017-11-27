package com.dili.alm.service;

import com.dili.alm.domain.Log;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:19:20.
 */
public interface LogService extends BaseService<Log, Long> {

	int insertLog( String logText);

	int updateLog( Long logId,String logText);
}