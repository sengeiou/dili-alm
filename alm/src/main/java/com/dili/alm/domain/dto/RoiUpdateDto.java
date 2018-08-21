package com.dili.alm.domain.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RoiUpdateDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5915058315538372040L;

	private Long applyId;
	private String roiTotal;
	private List<String> indicatorName;
	private List<String> indicatorCurrentStatus;
	private List<String> projectObjective;
	private List<String> implemetionDate;
	private List<Integer> indicatorType;
	private List<Integer> amount;
	private List<String> rate;
	private List<Long> total;
	private List<String> note;
	private List<Integer> costType;

	public Long getApplyId() {
		return applyId;
	}

	public String getRoiTotal() {
		return roiTotal;
	}

	public List<String> getIndicatorName() {
		return indicatorName;
	}

	public List<String> getIndicatorCurrentStatus() {
		return indicatorCurrentStatus;
	}

	public List<String> getProjectObjective() {
		return projectObjective;
	}

	public List<String> getImplemetionDate() {
		return implemetionDate;
	}

	public List<Integer> getIndicatorType() {
		return indicatorType;
	}

	public List<Integer> getAmount() {
		return amount;
	}

	public List<String> getRate() {
		return rate;
	}

	public List<Long> getTotal() {
		return total;
	}

	public List<String> getNote() {
		return note;
	}

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	public void setRoiTotal(String roiTotal) {
		this.roiTotal = roiTotal;
	}

	public void setIndicatorName(List<String> indicatorName) {
		this.indicatorName = indicatorName;
	}

	public void setIndicatorCurrentStatus(List<String> indicatorCurrentStatus) {
		this.indicatorCurrentStatus = indicatorCurrentStatus;
	}

	public void setProjectObjective(List<String> projectObjective) {
		this.projectObjective = projectObjective;
	}

	public void setImplemetionDate(List<String> implemetionDate) {
		this.implemetionDate = implemetionDate;
	}

	public void setIndicatorType(List<Integer> indicatorType) {
		this.indicatorType = indicatorType;
	}

	public void setAmount(List<Integer> amount) {
		this.amount = amount;
	}

	public void setRate(List<String> rate) {
		this.rate = rate;
	}

	public void setTotal(List<Long> total) {
		this.total = total;
	}

	public void setNote(List<String> note) {
		this.note = note;
	}

	public List<Integer> getCostType() {
		return costType;
	}

	public void setCostType(List<Integer> costType) {
		this.costType = costType;
	}

}
