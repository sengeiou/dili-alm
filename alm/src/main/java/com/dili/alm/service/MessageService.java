package com.dili.alm.service;

import java.util.List;
import java.util.Map;

import com.dili.alm.domain.Message;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-05 10:42:19.
 */
public interface MessageService extends BaseService<Message, Long> {

	int insertMessage(String messageUrl, Long sender, Long recipient,Integer type);
	
	int updateMessageIsRead( Long messageId);

	Map<String,Object> mapMessagges(String userId);
}