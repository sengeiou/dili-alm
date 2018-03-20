package com.dili.alm.domain;



/****
 * 查询项目年度总汇报表
 * @author lijing
 *
 */
public class ProjectYearCoverDto  {
	
	Long projectId;
    String projectName;
    String projectType;
    String projectstate;
    int taskCount;
    int finishCount;
    int ongoing;
    double completionRate;
    int noFinish;
    double overdueRate;
    
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getProjectstate() {
		return projectstate;
	}
	public void setProjectstate(String projectstate) {
		this.projectstate = projectstate;
	}
	public int getTaskCount() {
		return taskCount;
	}
	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}
	public int getFinishCount() {
		return finishCount;
	}
	public void setFinishCount(int finishCount) {
		this.finishCount = finishCount;
	}
	public int getOngoing() {
		return ongoing;
	}
	public void setOngoing(int ongoing) {
		this.ongoing = ongoing;
	}
	public double getCompletionRate() {
		return completionRate;
	}
	public void setCompletionRate(double completionRate) {
		this.completionRate = completionRate;
	}
	public int getNoFinish() {
		return noFinish;
	}
	public void setNoFinish(int noFinish) {
		this.noFinish = noFinish;
	}
	public double getOverdueRate() {
		return overdueRate;
	}
	public void setOverdueRate(double overdueRate) {
		this.overdueRate = overdueRate;
	}
  
}
