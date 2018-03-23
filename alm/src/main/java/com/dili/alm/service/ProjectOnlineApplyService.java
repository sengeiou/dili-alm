package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.dto.ProjectOnlineApplyUpdateDto;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
import com.dili.ss.base.BaseService;

/**
 * 上线申请service
 * 
 * @author jiang
 *
 */
public interface ProjectOnlineApplyService extends BaseService<ProjectOnlineApply, Long> {

	/**
	 * 新增或修改上线申请
	 * 
	 * @param dto
	 *            数据模型
	 * @throws ProjectOnlineApplyException 
	 */
	void saveOrUpdate(ProjectOnlineApplyUpdateDto dto) throws ProjectOnlineApplyException;

	/**
	 * 查看申请详情
	 * 
	 * @param applyId
	 *            申请id
	 * @return 数据模型
	 */
	ProjectOnlineApplyDetailDto getEditViewDataById(Long applyId);

	/**
	 * 提交申请进入上线操作流程
	 * 
	 * @param applyId
	 *            申请id
	 */
	void submit(Long applyId) throws ProjectOnlineApplyException;

	/**
	 * 开始执行
	 * 
	 * @param applyId
	 *            申请id
	 * @param executorId TODO
	 * @param executors TODO
	 * @param description TODO
	 * @throws ProjectOnlineApplyException
	 */
	void startExecute(Long applyId, Long executorId, List<Long> executors, String description) throws ProjectOnlineApplyException;

	/**
	 * 测试确认
	 * 
	 * @param applyId
	 *            申请id
	 * @param executorId TODO
	 * @param result TODO
	 * @param description TODO
	 * @throws ProjectOnlineApplyException
	 */
	void testerConfirm(Long applyId, Long executorId, OperationResult result, String description) throws ProjectOnlineApplyException;

	/**
	 * 执行确认
	 * 
	 * @param applyId
	 *            申请id
	 * @param executorId TODO
	 * @param result
	 *            执行结果
	 * @param description TODO
	 * @throws ProjectOnlineApplyException
	 */
	void excuteConfirm(Long applyId, Long executorId, OperationResult result, String description) throws ProjectOnlineApplyException;

	/**
	 * 验证
	 * 
	 * @param applyId
	 *            申请id
	 * @throws ProjectOnlineApplyException
	 */
	void verify(Long applyId) throws ProjectOnlineApplyException;

	void insertProjectOnlineApply(ProjectOnlineApplyUpdateDto projectOnlineApply) throws ProjectOnlineApplyException;

	void updateProjectOnlineApply(ProjectOnlineApplyUpdateDto dto) throws ProjectOnlineApplyException;

	void deleteProjectOnlineApply(Long id) throws ProjectOnlineApplyException;
}