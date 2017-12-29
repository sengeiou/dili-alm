package com.dili.alm.domain.dto;

import com.dili.alm.domain.MessageEntity;

public class MessageDto extends MessageEntity{
	private String messageName;

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}
	
}
