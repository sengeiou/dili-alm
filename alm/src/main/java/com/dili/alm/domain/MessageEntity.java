package com.dili.alm.domain;

import java.util.Date;

import com.dili.ss.domain.BaseDomain;

public class MessageEntity  extends BaseDomain implements Message{
	private Long id;
	private String url;
	private Long sender;
	private Long recipient;
	private Date created;
	private Integer type;
	private Boolean isRead;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getSender() {
		return sender;
	}
	public void setSender(Long sender) {
		this.sender = sender;
	}
	public Long getRecipient() {
		return recipient;
	}
	public void setRecipient(Long recipient) {
		this.recipient = recipient;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
}
