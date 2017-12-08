package com.dili.alm.service;

import com.dili.alm.domain.Approve;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-04 17:39:37.
 */
public interface ApproveService extends BaseService<Approve, Long> {


    void buildApplyApprove(Map map,Long id);
    void buildChangeApprove(Map map,Long id);

    BaseOutput applyApprove(Long id, String opt, String notes);
}