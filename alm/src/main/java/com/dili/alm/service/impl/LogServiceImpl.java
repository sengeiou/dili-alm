package com.dili.alm.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.LogMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Log;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.service.DataDictionaryService;
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
    @Autowired
    private DataDictionaryService dataDictionaryService;
    
    private final String LOG_MODULE_CODE="log_module";
	@Override
	public int insertLog(String logText,Integer logModule) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if(userTicket==null){
			throw new RuntimeException("未登录");
		}
		Log record = DTOUtils.newDTO(Log.class);
		
		SimpleDateFormat formater = new SimpleDateFormat("yyMMdd");
		Date date = new Date();
		String id = formater.format(date);
		SimpleDateFormat formater1 = new SimpleDateFormat("yyyy-MM-dd");
		String formatDate = formater1.format(date);
		String beginTime=formatDate+" 00:00:00";
		String endTime=formatDate+" 23:59:59";
		synchronized (this) {
			List<Log> selectLogByCreated = this.getActualDao()
					.selectLogByCreated(beginTime, endTime);
			if (selectLogByCreated != null && selectLogByCreated.size() > 0) {
				Log log = selectLogByCreated.get(0);
				record.setLogOrder(log.getLogOrder() + 1L);
				record.setLogNumber(Long.parseLong(id
						+ WebUtil.getSixNumber(log.getLogOrder() + 1L)));
			} else {
				record.setLogOrder(1L);
				record.setLogNumber(Long.parseLong(id
						+ WebUtil.getSixNumber(1L)));
			}
			record.setOperatorId(userTicket.getId());
			record.setContent(logText);
			record.setCreated(date);
			record.setLogModule(logModule);
			record.setIp(userTicket.getLastLoginIp());
			return this.getActualDao().insertSelective(record);
		}
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
		if(log.getSort().equals("logNumber")){
			log.setSort(AlmConstants.LogSort.LOGNUMBER.getCode());
		}else if(log.getSort().equals("operatorId")){
			log.setSort(AlmConstants.LogSort.OPERATORID.getCode());
		}
		List<Log> logLikeList = this.getActualDao().logLikeList(log,beginTime,endTime);
		int logLikeListCount = this.getActualDao().logLikeListCount(log,beginTime,endTime);
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == log.getMetadata() ? new HashMap<>() : log.getMetadata();

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("operatorId", memberProvider);
		
		JSONObject moduleProvider = new JSONObject();
		moduleProvider.put("provider", "moduleProvider");
		metadata.put("logModule", moduleProvider);
		
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

	@Override
	public List<DataDictionaryValueDto> getLogModules() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(LOG_MODULE_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

}