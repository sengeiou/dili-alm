package com.dili.alm.domain;

import java.util.Date;

import com.dili.alm.domain.Task;
import com.dili.ss.domain.BaseDomain;

/*
 * 任务查询的DTO
 */
public class TaskEntity extends BaseDomain implements Task {

	long id;// ID

	String name;// 任务名称

	String projectName;// 所属项目名称

	String versionStr;// 版本名称

	String phaseStr;// 项目阶段

	String flowStr = "正常流程";// 流程

	Integer status;

	String planDays;// 计划周期

	int progress = 0;// 进度

	long projectId;

	long versionId;

	Long phaseId;

	Date startDate;

	Date endDate;

	Long beforeTask;

	Integer type;

	Long owner;

	Boolean flow;

	String describe;

	Short planTime;

	Long changeId;

	Date created;

	Date modified;

	Long createMemberId;

	Long modifyMemberId;

	Date factBeginDate;

	Date factEndDate;

	Boolean updateDetail;

	Boolean copyButton;

	Boolean canOperation;

	public Boolean getCopyButton() {
		return copyButton;
	}

	public void setCopyButton(Boolean copyButton) {
		this.copyButton = copyButton;
	}

	public Boolean getUpdateDetail() {
		return updateDetail;
	}

	public void setUpdateDetail(Boolean updateDetail) {
		this.updateDetail = updateDetail;
	}

	public TaskEntity(Task task) {
		this.id = task.getId();
		this.beforeTask = task.getBeforeTask();
		this.changeId = task.getChangeId();
		this.created = task.getCreated();
		this.modifyMemberId = task.getModifyMemberId();
		this.describe = task.getDescribe();
		this.flow = task.getFlow();
		this.modified = task.getModified();
		this.createMemberId = task.getCreateMemberId();
		this.versionId = task.getVersionId();
		this.phaseId = task.getPhaseId();
		this.owner = task.getOwner();
		this.planTime = task.getPlanTime();
		this.name = task.getName();
		this.type = task.getType();
		this.status = task.getStatus();
		this.startDate = task.getStartDate();
		this.endDate = task.getEndDate();
		this.projectId = task.getProjectId();
		this.factBeginDate = task.getFactBeginDate();
		this.factEndDate = task.getFactBeginDate();
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getVersionStr() {
		return versionStr;
	}

	public void setVersionStr(String versionStr) {
		this.versionStr = versionStr;
	}

	public String getPhaseStr() {
		return phaseStr;
	}

	public void setPhaseStr(String phaseStr) {
		this.phaseStr = phaseStr;
	}

	public String getFlowStr() {
		return flowStr;
	}

	public void setFlowStr(Boolean flow) {

		this.flowStr = flow ? "变更流程" : "正常流程";
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPlanDays() {
		return planDays;
	}

	public void setPlanDays(String planDays) {
		this.planDays = planDays;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public Long getOwner() {
		return owner;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public Long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Long phaseId) {
		this.phaseId = phaseId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getBeforeTask() {
		return beforeTask;
	}

	public void setBeforeTask(Long beforeTask) {
		this.beforeTask = beforeTask;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getFlow() {
		return flow;
	}

	public void setFlow(Boolean flow) {
		this.flow = flow;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public Short getPlanTime() {
		return planTime;
	}

	public void setPlanTime(Short planTime) {
		this.planTime = planTime;
	}

	public void setFlowStr(String flowStr) {
		this.flowStr = flowStr;
	}

	@Override
	public Long getChangeId() {

		return changeId;
	}

	@Override
	public void setChangeId(Long changeId) {
		this.changeId = changeId;
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
	public Date getModified() {
		return modified;
	}

	@Override
	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Override
	public Long getCreateMemberId() {
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

	@Override
	public Date getFactEndDate() {
		return factEndDate;
	}

	@Override
	public void setFactEndDate(Date factEndDate) {
		this.factEndDate = factEndDate;
	}

	@Override
	public Date getFactBeginDate() {
		return factBeginDate;
	}

	@Override
	public void setFactBeginDate(Date factBeginDate) {
		this.factBeginDate = factBeginDate;
	}

	public Boolean getCanOperation() {
		return canOperation;
	}

	public void setCanOperation(Boolean canOperation) {
		this.canOperation = canOperation;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public void setVersionId(long versionId) {
		this.versionId = versionId;
	}

}
