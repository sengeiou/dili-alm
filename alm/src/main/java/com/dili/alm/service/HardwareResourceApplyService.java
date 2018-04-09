package com.dili.alm.service;

import java.util.Set;

import com.dili.alm.domain.ApproveResult;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.dto.HardwareResourceApplyUpdateDto;
import com.dili.alm.exceptions.HardwareResourceApplyException;
import com.dili.ss.base.BaseService;

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
	 * 研发中心总经理审批
	 * 
	 * @param applyId
	 *            申请id
	 * @param generalManagerId
	 *            研发中心总经理id
	 * @param result
	 *            审批意见
	 * @param description
	 *            描述
	 */
	void generalManagerApprove(Long applyId, Long generalManagerId, ApproveResult result, String description)
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

}