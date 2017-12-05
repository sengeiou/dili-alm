package com.dili.alm.service.impl;

import java.io.File;
import java.util.Date;

import com.dili.alm.dao.LogMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Log;
import com.dili.alm.domain.Task;
import com.dili.alm.service.LogService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.dili.sysadmin.sdk.util.WebContent;

import org.apache.catalina.mbeans.UserMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:19:20.
 */
@Service
public class LogServiceImpl extends BaseServiceImpl<Log, Long> implements LogService {
    public LogMapper getActualDao() {
        return (LogMapper)getDao();
    }

	@Override
	public int insertLog(String logText) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if(userTicket==null){
			throw new RuntimeException("未登录");
		}
		Log record = DTOUtils.newDTO(Log.class);
		record.setOperatorId(userTicket.getId());
		record.setOperatorName(userTicket.getUserName());
		record.setContent(logText);
		record.setCreated(new Date());
		record.setIp(userTicket.getLastLoginIp());
		return this.getActualDao().insertSelective(record);
	}

	@Override
	public int updateLog(Long logId,String logText) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Log record = DTOUtils.newDTO(Log.class);
		record.setId(logId);
		record.setOperatorId(userTicket.getId());
		record.setOperatorName(userTicket.getUserName());
		record.setContent(logText);
		record.setModified(new Date());
		record.setIp(userTicket.getLastLoginIp());
		return this.getActualDao().updateByPrimaryKeySelective(record);
	}

}