package com.dili.alm.domain;

import java.util.Date;

import com.dili.ss.domain.BaseDomain;

public class WeeklyEntity extends BaseDomain implements Weekly {

	private static final long serialVersionUID = 1L;
	private Long projectId;
	private Date startDate;
	private Date endDate;

	private String risk;
	private String question;
	private String progress;

	private String currentWeek;
	private String nextWeek;

	private Date created;
	private Date modified;
	private Long createMemberId;
	private Long modifyMemberId;

	@Override
	public Long getProjectId() {
		// TODO Auto-generated method stub
		return projectId;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;

	}

	@Override
	public String getCurrentWeek() {
		return currentWeek;
	}

	@Override
	public void setCurrentWeek(String currentWeek) {
		this.currentWeek = currentWeek;
	}

	@Override
	public String getNextWeek() {
		return nextWeek;
	}

	@Override
	public void setNextWeek(String nextWeek) {
		this.nextWeek = nextWeek;
	}

	@Override
	public Date getStartDate() {
		// TODO Auto-generated method stub
		return startDate;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;

	}

	@Override
	public Date getEndDate() {
		// TODO Auto-generated method stub
		return endDate;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;

	}

	@Override
	public String getRisk() {
		// TODO Auto-generated method stub
		return risk;
	}

	@Override
	public void setRisk(String risk) {
		this.risk = risk;

	}

	@Override
	public String getQuestion() {
		// TODO Auto-generated method stub
		return question;
	}

	@Override
	public void setQuestion(String question) {
		this.question = question;

	}

	@Override
	public String getProgress() {
		// TODO Auto-generated method stub
		return progress;
	}

	@Override
	public void setProgress(String progress) {
		this.progress = progress;

	}

	@Override
	public Date getCreated() {
		// TODO Auto-generated method stub
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;

	}

	@Override
	public Date getModified() {
		// TODO Auto-generated method stub
		return modified;
	}

	@Override
	public void setModified(Date modified) {

		this.modified = modified;

	}

	@Override
	public Long getCreateMemberId() {
		// TODO Auto-generated method stub
		return createMemberId;
	}

	@Override
	public void setCreateMemberId(Long createMemberId) {
		this.createMemberId = createMemberId;

	}

	@Override
	public Long getModifyMemberId() {

		return modifyMemberId;
	}

	@Override
	public void setModifyMemberId(Long modifyMemberId) {
		this.modifyMemberId = modifyMemberId;

	}

}
