package com.dili.alm.domain.dto;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

public class ProjectOnlineApplyUpdateDto extends ProjectOnlineApplyDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7624328628824598772L;

	private List<ProjectOnlineSubsystemDto> subsystem;
	private List<String> emailAddress;

	public List<ProjectOnlineSubsystemDto> getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(List<ProjectOnlineSubsystemDto> subsystem) {
		this.subsystem = subsystem;
	}

	public List<String> getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(List<String> emailAddress) {
		this.emailAddress = emailAddress;
	}

}
