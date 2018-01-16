package com.dili.sysadmin.service;

import java.util.List;

import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Resource;
import com.dili.sysadmin.domain.dto.ResourceDto;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-07-04 15:24:51.
 */
public interface ResourceService extends BaseService<Resource, Long> {

	BaseOutput<Object> add(ResourceDto command);

	BaseOutput<Object> update(ResourceDto dto);

	List<String> findCodeByUserId(Long id);
}