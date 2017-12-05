package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dili.alm.dao.MessageMapper;
import com.dili.alm.domain.Message;
import com.dili.alm.service.MessageService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-05 10:42:19.
 */
@Service
public class MessageServiceImpl extends BaseServiceImpl<Message, Long> implements MessageService {

    public MessageMapper getActualDao() {
        return (MessageMapper)getDao();
    }

	@Override
	public int insertMessage(String messageUrl, Long sender, Long recipient,
			Integer type) {
		Message message = DTOUtils.newDTO(Message.class);
		message.setUrl(messageUrl);
		message.setSender(sender);
		message.setRecipient(recipient);
		message.setType(type);
		message.setCreated(new Date());
		return this.getActualDao().insertSelective(message);	
	}

	@Override
	public int updateMessageIsRead(Long messageId) {
		Message message = this.getActualDao().selectByPrimaryKey(messageId);
		message.setIsRead(false);
		return this.getActualDao().updateByPrimaryKeySelective(message);
	}

	@Override
	public List<Message> listMessagges() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if(userTicket==null){
			throw new RuntimeException("未登录");
		}
		List<Message> list = new ArrayList<Message>();
		Message message=DTOUtils.newDTO(Message.class);
		synchronized (this) {// 这个很重要，必须使用一个锁， 
			message.setRecipient(userTicket.getId());
			message.setIsRead(false);
			list = this.getActualDao().select(message);
		}
		return list;
	}


}