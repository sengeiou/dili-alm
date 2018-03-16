package com.dili.alm.service.impl;

import com.dili.alm.dao.HardwareResourceApplyMapper;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-03-13 15:29:59.
 */
@Service
public class HardwareResourceApplyServiceImpl extends BaseServiceImpl<HardwareResourceApply, Long> implements HardwareResourceApplyService {

    public HardwareResourceApplyMapper getActualDao() {
        return (HardwareResourceApplyMapper)getDao();
    }
}