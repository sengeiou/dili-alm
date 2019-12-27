package com.dili.alm.service;

import com.dili.alm.domain.AlarmConfig;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-09 15:41:26.
 */
public interface AlarmConfigService extends BaseService<AlarmConfig, Long> {

	BaseOutput<Object> saveOrUpdateWithOutput(AlarmConfig alarmConfig);

	void alarm();
}