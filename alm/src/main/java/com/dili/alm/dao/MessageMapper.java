package com.dili.alm.dao;

import java.util.List;

import com.dili.alm.domain.Message;
import com.dili.ss.base.MyMapper;

public interface MessageMapper extends MyMapper<Message> {
	List<Message> selectMessages(Message message);
	int selectMessagesCount(Message message);
	int deleteLikeUrl(String url);
}