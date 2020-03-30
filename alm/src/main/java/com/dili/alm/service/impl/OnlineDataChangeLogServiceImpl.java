package com.dili.alm.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.alm.domain.OnlineDataChangeLog;
import com.dili.alm.exceptions.OnlineDataChangeException;
import com.dili.alm.service.OnlineDataChangeLogService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.session.SessionContext;

/**
 * MyBatis Generator This file was generated on 2019-12-25 18:22:44.
 */
//@Transactional(rollbackFor = Exception.class)
@Transactional(rollbackFor = OnlineDataChangeException.class)
@Service
public class OnlineDataChangeLogServiceImpl  extends BaseServiceImpl<OnlineDataChangeLog, Long> implements OnlineDataChangeLogService {
	
	@Override
	public void insertDataExeLog(String dataId, String operationName, int opertateResult) {
		OnlineDataChangeLog  log=DTOUtils.newDTO(OnlineDataChangeLog.class);
		Long  userOperatorId=SessionContext.getSessionContext().getUserTicket().getId();
		log.setOperatorId(userOperatorId);
		log.setOperateTime(new Date());
		
		log.setOperationName(operationName);
		log.setOpertateResult(opertateResult);
		log.setOnlineDateId(Long.parseLong(dataId));
		this.insertSelective(log);
	}

	

}