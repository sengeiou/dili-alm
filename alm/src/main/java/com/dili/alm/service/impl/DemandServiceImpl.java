package com.dili.alm.service.impl;

import com.dili.alm.dao.DemandMapper;
import com.dili.alm.domain.Demand;
import com.dili.alm.service.DemandService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:32:14.
 */
@Service
public class DemandServiceImpl extends BaseServiceImpl<Demand, Long> implements DemandService {

    public DemandMapper getActualDao() {
        return (DemandMapper)getDao();
    }
}