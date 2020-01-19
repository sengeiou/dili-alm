package com.dili.alm.component;

public interface AlmProcessInstanceDto {

	/**
	 * 流程实例id
	 * 
	 * @return
	 */
	String getProcessInstanceId();

	void setProcessInstanceId(String processInstanceId);

	/**
	 * 流程定义id
	 * 
	 * @return
	 */
	String getProcessDefinitionId();

	void setProcessDefinitionId(String processDefinitionId);
}
