package com.dili.alm.service.impl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.LogMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Log;
import com.dili.alm.domain.Task;
import com.dili.alm.service.LogService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
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
		record.setContent(logText);
		record.setModified(new Date());
		record.setIp(userTicket.getLastLoginIp());
		return this.getActualDao().updateByPrimaryKeySelective(record);
	}
	
	@Override
	public EasyuiPageOutput listLogPage(Log log,String beginTime,String endTime) {
		String content = log.getContent();
		if(!WebUtil.strIsEmpty(content)){
			String replaceAll = content.replaceAll(" ", "");
			log.setContent(replaceAll);
		}
		List<Log> logLikeList = this.getActualDao().logLikeList(log,beginTime,endTime);
		int logLikeListCount = this.getActualDao().logLikeListCount(log,beginTime,endTime);
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == log.getMetadata() ? new HashMap<>() : log.getMetadata();

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("operatorId", memberProvider);
		
		JSONObject provider = new JSONObject();
		provider.put("provider", "datetimeProvider");;
		metadata.put("created", provider);
		metadata.put("modified", provider);
		log.setMetadata(metadata);
		try {
			List list = ValueProviderUtils.buildDataByProvider(log, logLikeList);
			return new EasyuiPageOutput(logLikeListCount, list);
		} catch (Exception e) {
			return null;
		}
	}

}