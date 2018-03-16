package com.dili.alm.service.impl;

import com.dili.alm.dao.OnlineSystemMapper;
import com.dili.alm.domain.OnlineSystem;
import com.dili.alm.service.OnlineSystemService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-03-13 15:30:51.
 */
@Service
public class OnlineSystemServiceImpl extends BaseServiceImpl<OnlineSystem, Long> implements OnlineSystemService {

    public OnlineSystemMapper getActualDao() {
        return (OnlineSystemMapper)getDao();
    }
}