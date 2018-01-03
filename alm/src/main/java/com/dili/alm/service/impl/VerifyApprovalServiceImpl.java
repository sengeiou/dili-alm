package com.dili.alm.service.impl;

import com.dili.alm.dao.VerifyApprovalMapper;
import com.dili.alm.domain.VerifyApproval;
import com.dili.alm.service.VerifyApprovalService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-09 16:40:20.
 */
@Service
public class VerifyApprovalServiceImpl extends BaseServiceImpl<VerifyApproval, Long> implements VerifyApprovalService {

    public VerifyApprovalMapper getActualDao() {
        return (VerifyApprovalMapper)getDao();
    }
}