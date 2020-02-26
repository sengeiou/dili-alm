package com.dili.alm.dao;

import java.util.List;

import com.dili.alm.domain.Approve;
import com.dili.alm.domain.WorkOrder;
import com.dili.ss.base.MyMapper;

public interface ApproveMapper extends MyMapper<Approve> {
	
	
    int  updateByCreateMemberId(Approve approve);
    
    int  updateByModifyMemberId(Approve approve);
    
}