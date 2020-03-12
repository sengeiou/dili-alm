package com.dili.alm.service;

import java.util.List;
import java.util.Set;

import com.dili.alm.domain.ApproveResult;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.HardwareResourceRequirement;
import com.dili.alm.domain.dto.HardwareResourceApplyUpdateDto;
import com.dili.alm.exceptions.HardwareResourceApplyException;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-20 17:22:08.
 */
public interface HardwareResourceApplyService extends BaseService<HardwareResourceApply, Long> {

	/**
	 * 新增或修改申请
	 * 
	 * @param dto
	 */
	void saveOrUpdate(HardwareResourceApplyUpdateDto dto) throws HardwareResourceApplyException;

	/**
	 * 删除申请
	 * 
	 * @param applyId
	 *            申请id
	 * @throws HardwareResourceApplyException
	 */
	void deleteHardwareResourceApply(Long applyId) throws HardwareResourceApplyException;

	/**
	 * 提交申请，进入审批流程
	 * 
	 * @param applyId
	 *            申请id
	 */
	void submit(Long applyId) throws HardwareResourceApplyException;

	/**
	 * 项目经理审批
	 * 
	 * @param applyId
	 *            申请id
	 * @param projectManagerId
	 *            项目经理id
	 * @param result
	 *            审核结果
	 * @param description
	 *            描述
	 */
	void projectManagerApprove(Long applyId, Long projectManagerId, ApproveResult result, String description)
			throws HardwareResourceApplyException;

	/**
	 * 运维经理审批
	 * 
	 * @param applyId
	 *            申请id
	 * @param operationManagerId
	 *            运维经理id
	 * @param executors
	 *            实施人id
	 * @param description
	 *            描述
	 */
	void operationManagerApprove(Long applyId, Long operationManagerId, Set<Long> executors, String description)
			throws HardwareResourceApplyException;

	/**
	 * 执行人实施
	 * 
	 * @param applyId
	 *            申请id
	 * @param executorId
	 *            执行人id
	 * @param description
	 *            描述
	 */
	void operatorExecute(Long applyId, Long executorId, String description) throws HardwareResourceApplyException;

	List<HardwareResourceRequirement> listRequirement(Long applyId) throws HardwareResourceApplyException;

	void saveAndSubmit(HardwareResourceApplyUpdateDto hardwareResourceApply) throws HardwareResourceApplyException;

	HardwareResourceApply getDetailViewModel(Long id);
	
    /**
     * 完成提交
     * @param id 业务编号，任务编号 ，状态
     * @return
     */
    BaseOutput submitApprove(Long id, String taskId);
    
    /**
     * 驳回需求申请
     * @param code
     * @return
     */
    BaseOutput rejectApprove(Long id, String taskId);
    /**
	 * 项目经理审批 for流程中心
	 * 
	 * @param applyId
	 *            申请id
	 * @param projectManagerId
	 *            项目经理id
	 * @param result
	 *            审核结果
	 * @param description
	 *            描述
	 */
	void projectManagerApproveForTask(Long applyId, Long projectManagerId, ApproveResult result, String description)
			throws HardwareResourceApplyException;

	/**
	 * 运维经理审批for流程中心
	 * 
	 * @param applyId
	 *            申请id
	 * @param operationManagerId
	 *            运维经理id
	 * @param executors
	 *            实施人id
	 * @param description
	 *            描述
	 */
	void operationManagerApproveForTask(Long applyId, Long operationManagerId, Set<Long> executors, String description)
			throws HardwareResourceApplyException;

	/**
	 * 执行人实施for流程中心
	 * 
	 * @param applyId
	 *            申请id
	 * @param executorId
	 *            执行人id
	 * @param description
	 *            描述
	 */
	void operatorExecuteForTask(Long applyId, Long executorId, String description) throws HardwareResourceApplyException;
	/**
	 * 运维经理操作，同意
	 * 
	 * @param apply
	 *            
	 *   */
	void operatorManagerTask(Long applyId)throws HardwareResourceApplyException;
	/**
	 * 运维日志操作	 * 
	 * @param apply
	 *            
	 *   */
	void operatorManagerLog(Long applyId, Long operationManagerId, Set<Long> executors, String description)throws HardwareResourceApplyException;
}