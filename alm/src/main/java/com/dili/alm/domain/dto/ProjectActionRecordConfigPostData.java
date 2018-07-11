package com.dili.alm.domain.dto;

import java.util.List;

public class ProjectActionRecordConfigPostData {

	private List<Long> id;
	private List<String> actionCode;
	private List<Integer> actionDateType;
	private List<Boolean> showDate;
	private List<String> color;
	private List<Boolean> hint;
	private List<String> hintMessage;

	public List<Long> getId() {
		return id;
	}

	public List<String> getActionCode() {
		return actionCode;
	}

	public List<Integer> getActionDateType() {
		return actionDateType;
	}

	public List<Boolean> getShowDate() {
		return showDate;
	}

	public List<String> getColor() {
		return color;
	}

	public List<Boolean> getHint() {
		return hint;
	}

	public List<String> getHintMessage() {
		return hintMessage;
	}

	public void setId(List<Long> id) {
		this.id = id;
	}

	public void setActionCode(List<String> actionCode) {
		this.actionCode = actionCode;
	}

	public void setActionDateType(List<Integer> actionDateType) {
		this.actionDateType = actionDateType;
	}

	public void setShowDate(List<Boolean> showDate) {
		this.showDate = showDate;
	}

	public void setColor(List<String> color) {
		this.color = color;
	}

	public void setHint(List<Boolean> hint) {
		this.hint = hint;
	}

	public void setHintMessage(List<String> hintMessage) {
		this.hintMessage = hintMessage;
	}
}
