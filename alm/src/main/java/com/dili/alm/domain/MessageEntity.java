package com.dili.alm.domain;

import java.util.Date;

import com.dili.ss.domain.BaseDomain;

public class MessageEntity extends BaseDomain implements Message {
	private Long id;
	private String url;
	private Long sender;
	private Long recipient;
	private Date created;
	private Integer type;
	private Boolean isRead;
	private String name;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public Long getSender() {
		return sender;
	}

	@Override
	public void setSender(Long sender) {
		this.sender = sender;
	}

	@Override
	public Long getRecipient() {
		return recipient;
	}

	@Override
	public void setRecipient(Long recipient) {
		this.recipient = recipient;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public Integer getType() {
		return type;
	}

	@Override
	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public Boolean getIsRead() {
		return isRead;
	}

	@Override
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
