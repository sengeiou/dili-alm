package com.dili.alm.service;

import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.dto.UserWorkHourDetailDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseDomain;
import com.github.pagehelper.Page;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:06.
 */
public interface TaskDetailsService extends BaseService<TaskDetails, Long> {

	Page<UserWorkHourDetailDto> listUserWorkHourDetail(Long userId, BaseDomain query);

}