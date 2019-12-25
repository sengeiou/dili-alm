package com.dili.alm.service.impl;

import com.dili.alm.dao.OnlineDataChangeMapper;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-25 18:22:44.
 */
@Service
public class OnlineDataChangeServiceImpl extends BaseServiceImpl<OnlineDataChange, Long> implements OnlineDataChangeService {

    public OnlineDataChangeMapper getActualDao() {
        return (OnlineDataChangeMapper)getDao();
    }
}