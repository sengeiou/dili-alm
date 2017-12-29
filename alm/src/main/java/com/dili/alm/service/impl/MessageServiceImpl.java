package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dili.alm.dao.DataDictionaryValueMapper;
import com.dili.alm.dao.MessageMapper;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Message;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.MessageDto;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.MessageService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private DataDictionaryValueMapper dataDictionaryValueMapper;
    @Autowired
    private DataDictionaryService dataDictionaryService;
    private static final String MESSAGE_TYPE_CODE = "message_type";
	@Override
	public int insertMessage(String messageUrl, Long sender, Long recipient,Integer type) {
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
		message.setIsRead(true);
		return this.getActualDao().updateByPrimaryKeySelective(message);
	}

	@Override
	public Map<String,Object> mapMessagges(String userId) {
		Map<String,Object> map=new HashMap<String, Object>();
		if(WebUtil.strIsEmpty(userId)){
			throw new RuntimeException("未登录");
		}
		
		synchronized (this) {// 这个很重要，必须使用一个锁， 
			Message message=DTOUtils.newDTO(Message.class);
			message.setRecipient(Long.parseLong(userId));
			message.setIsRead(false);
			
			List<Message> list = this.getActualDao().selectMessages(message);
			DataDictionaryDto dto = this.dataDictionaryService.findByCode(MESSAGE_TYPE_CODE);
			
			List<MessageDto> listDto=new ArrayList<MessageDto>();
			for (Message newMessage : list) {
				 MessageDto messageDto=new MessageDto();
				 messageDto.setId(newMessage.getId());
				 messageDto.setCreated(newMessage.getCreated());
				 messageDto.setIsRead(newMessage.getIsRead());
				 DataDictionaryValue dtoValue=DTOUtils.newDTO(DataDictionaryValue.class);
				 dtoValue.setDdId(dto.getId());
				 dtoValue.setValue(newMessage.getType().toString());
				 DataDictionaryValue selectOne = dataDictionaryValueMapper.selectOne(dtoValue);
				 messageDto.setMessageName(selectOne.getCode());
				 messageDto.setSender(newMessage.getSender());
				 messageDto.setRecipient(newMessage.getRecipient());
				 listDto.add(messageDto);
			}
			map.put("messages", listDto);
			int count = this.getActualDao().selectMessagesCount(message);
			map.put("count", count);
		}
		return map;
	}


}