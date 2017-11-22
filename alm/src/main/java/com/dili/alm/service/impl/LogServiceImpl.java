package com.dili.alm.service.impl;

import com.dili.alm.dao.LogMapper;
import com.dili.alm.domain.Log;
import com.dili.alm.service.LogService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-22 16:28:57.
 */
@Service
public class LogServiceImpl extends BaseServiceImpl<Log, Long> implements LogService {

    public LogMapper getActualDao() {
        return (LogMapper)getDao();
    }
}