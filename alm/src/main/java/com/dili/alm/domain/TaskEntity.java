package com.dili.alm.domain;

import java.util.Date;

import com.dili.alm.domain.Task;
import com.dili.ss.domain.BaseDomain;

/*
 * 任务查询的DTO
 */
public class TaskEntity extends BaseDomain implements Task {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6406830521013428608L;

	Long beforeTask;

	Boolean canOperation;

	Long changeId;

	Boolean copyButton;

	Date created;

	Long createMemberId;

	String describe;

	Date endDate;

	Date factBeginDate;

	Date factEndDate;

	Boolean flow;

	String flowStr = "正常流程";// 流程

	long id;// ID

	Date modified;

	Long modifyMemberId;

	String name;// 任务名称

	Long owner;

	String planDays;// 计划周期

	Short planTime;

	int progress = 0;// 进度

	long projectId;

	String projectName;// 所属项目名称

	Date startDate;

	Integer status;

	Integer type;

	Boolean updateDetail;

	long versionId;

	String versionStr;// 版本名称

	private Long projectManagerId;

//	public TaskEntity(Task task) {
//		this.id = task.getId();
//		this.beforeTask = task.getBeforeTask();
//		this.changeId = task.getChangeId();
//		this.created = task.getCreated();
//		this.modifyMemberId = task.getModifyMemberId();
//		this.describe = task.getDescribe();
//		this.flow = task.getFlow();
//		this.modified = task.getModified();
//		this.createMemberId = task.getCreateMemberId();
//		this.versionId = task.getVersionId();
//		this.owner = task.getOwner();
//		this.planTime = task.getPlanTime();
//		this.name = task.getName();
//		this.type = task.getType();
//		this.status = task.getStatus();
//		this.startDate = task.getStartDate();
//		this.endDate = task.getEndDate();
//		this.projectId = task.getProjectId();
//		this.factBeginDate = task.getFactBeginDate();
//		this.factEndDate = task.getFactBeginDate();
//	}

	@Override
	public Long getBeforeTask() {
		return beforeTask;
	}

	public Boolean getCanOperation() {
		return canOperation;
	}

	@Override
	public Long getChangeId() {

		return changeId;
	}

	public Boolean getCopyButton() {
		return copyButton;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public Long getCreateMemberId() {
		return createMemberId;
	}

	@Override
	public String getDescribe() {
		return describe;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public Date getFactBeginDate() {
		return factBeginDate;
	}

	@Override
	public Date getFactEndDate() {
		return factEndDate;
	}

	@Override
	public Boolean getFlow() {
		return flow;
	}

	public String getFlowStr() {
		return flowStr;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Date getModified() {
		return modified;
	}

	@Override
	public Long getModifyMemberId() {
		return modifyMemberId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getOwner() {
		return owner;
	}

	public String getPlanDays() {
		return planDays;
	}

	@Override
	public Short getPlanTime() {
		return planTime;
	}

	public int getProgress() {
		return progress;
	}

	@Override
	public Long getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Integer getStatus() {
		return status;
	}

	@Override
	public Integer getType() {
		return type;
	}

	public Boolean getUpdateDetail() {
		return updateDetail;
	}

	@Override
	public Long getVersionId() {
		return versionId;
	}

	public String getVersionStr() {
		return versionStr;
	}

	@Override
	public void setBeforeTask(Long beforeTask) {
		this.beforeTask = beforeTask;
	}

	public void setCanOperation(Boolean canOperation) {
		this.canOperation = canOperation;
	}

	@Override
	public void setChangeId(Long changeId) {
		this.changeId = changeId;
	}

	public void setCopyButton(Boolean copyButton) {
		this.copyButton = copyButton;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public void setCreateMemberId(Long createMemberId) {
		this.createMemberId = createMemberId;
	}

	@Override
	public void setDescribe(String describe) {
		this.describe = describe;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public void setFactBeginDate(Date factBeginDate) {
		this.factBeginDate = factBeginDate;
	}

	@Override
	public void setFactEndDate(Date factEndDate) {
		this.factEndDate = factEndDate;
	}

	@Override
	public void setFlow(Boolean flow) {
		this.flow = flow;
	}

	public void setFlowStr(Boolean flow) {

		this.flowStr = flow ? "变更流程" : "正常流程";
	}

	public void setFlowStr(String flowStr) {
		this.flowStr = flowStr;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Override
	public void setModifyMemberId(Long modifyMemberId) {
		this.modifyMemberId = modifyMemberId;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public void setPlanDays(String planDays) {
		this.planDays = planDays;
	}

	@Override
	public void setPlanTime(Short planTime) {
		this.planTime = planTime;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public void setType(Integer type) {
		this.type = type;
	}

	public void setUpdateDetail(Boolean updateDetail) {
		this.updateDetail = updateDetail;
	}

	public void setVersionId(long versionId) {
		this.versionId = versionId;
	}

	@Override
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public void setVersionStr(String versionStr) {
		this.versionStr = versionStr;
	}

	public Long getProjectManagerId() {
		return projectManagerId;
	}

	public void setProjectManagerId(Long projectManagerId) {
		this.projectManagerId = projectManagerId;
	}

}
