package com.dili.alm.domain.dto;



/****
 * 查询项目年度总汇报表显示实体
 * @author lijing
 *
 */
public class ProjectYearCoverForAllDto  {
	

    int taskCount;
    int finishCount;
    int ongoing;
    int noFinish;
    int pauseTotal;
    int noBeginTotal;
    
    

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

	public int getNoFinish() {
		return noFinish;
	}
	public void setNoFinish(int noFinish) {
		this.noFinish = noFinish;
	}
	public int getPauseTotal() {
		return pauseTotal;
	}
	public void setPauseTotal(int pauseTotal) {
		this.pauseTotal = pauseTotal;
	}
	public int getNoBeginTotal() {
		return noBeginTotal;
	}
	public void setNoBeginTotal(int noBeginTotal) {
		this.noBeginTotal = noBeginTotal;
	}
  
}
