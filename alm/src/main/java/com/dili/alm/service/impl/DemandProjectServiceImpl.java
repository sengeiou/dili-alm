package com.dili.alm.service.impl;

import com.dili.alm.dao.DemandProjectMapper;
import com.dili.alm.domain.DemandProject;
import com.dili.alm.service.DemandProjectService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2019-12-23 17:47:02.
 */
@Service
public class DemandProjectServiceImpl extends BaseServiceImpl<DemandProject, Long> implements DemandProjectService {

    public DemandProjectMapper getActualDao() {
        return (DemandProjectMapper)getDao();
    }
}