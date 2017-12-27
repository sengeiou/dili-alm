package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.FilesEntity;

public class UploadProjectFileDto extends FilesEntity {

	private List<Long> fileIds;
	private String receiver;
	private Boolean sendMail;

	public List<Long> getFileIds() {
		return fileIds;
	}

	public void setFileIds(List<Long> fileIds) {
		this.fileIds = fileIds;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Boolean getSendMail() {
		return sendMail;
	}

	public void setSendMail(Boolean sendMail) {
		this.sendMail = sendMail;
	}

}
