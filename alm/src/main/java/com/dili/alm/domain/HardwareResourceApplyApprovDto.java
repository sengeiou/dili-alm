package com.dili.alm.domain;

/**
 *
 *线上申请设置申请节点
 *
 */
public interface HardwareResourceApplyApprovDto {
	/**
	 * 审批中
	 * @return
	 */
	Boolean getApproving();

	void setApproving(Boolean approving);
	
	/**
	 * 项目经理
	 * @return
	 */
	Boolean getProjectManagerApprov();

	void setProjectManagerApprov(Boolean projectManagerApprov);	
	/**
	 * 审批中
	 * @return
	 */
	
	Boolean getGeneralManagerAppov();

	void setGeneralManagerAppov(Boolean generalManagerAppov);
	
	/**
	 * 运维经理
	 * @return
	 */
	Boolean getOperactionManagerAppov();

	void setOperactionManagerAppov(Boolean operactionManagerAppov);
	/**
	 * 实施
	 * @return
	 */
	Boolean getImplementing();

	void setImplementing(Boolean implementing);
}