package com.dili.alm.domain;

import java.util.Collection;

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
	/**
	 * 环境文字转化envList
	 */
	Collection<String> getEnvList();

	void setEnvList(Collection<String> envList);
}