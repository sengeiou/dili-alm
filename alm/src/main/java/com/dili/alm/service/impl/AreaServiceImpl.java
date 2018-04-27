package com.dili.alm.service.impl;

import com.dili.alm.dao.AreaMapper;
import com.dili.alm.domain.Area;
import com.dili.alm.service.AreaService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-04-27 09:27:25.
 */
@Service
public class AreaServiceImpl extends BaseServiceImpl<Area, Long> implements AreaService {

    public AreaMapper getActualDao() {
        return (AreaMapper)getDao();
    }
}