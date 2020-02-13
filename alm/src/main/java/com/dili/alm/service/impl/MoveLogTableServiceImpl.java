package com.dili.alm.service.impl;

import com.dili.alm.dao.MoveLogTableMapper;
import com.dili.alm.domain.MoveLogTable;
import com.dili.alm.service.MoveLogTableService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-27 16:48:24.
 */
@Service
public class MoveLogTableServiceImpl extends BaseServiceImpl<MoveLogTable, Long> implements MoveLogTableService {

    public MoveLogTableMapper getActualDao() {
        return (MoveLogTableMapper)getDao();
    }
}