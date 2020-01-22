package com.dili.alm.service;

import java.util.Set;

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

	void deleteProjectOnlineApply(Long id) throws ProjectOnlineApplyException;

	/**
	 * 执行确认
	 * 
	 * @param applyId     申请id
	 * @param executorId  执行人id
	 * @param result      执行结果
	 * @param description 描述
	 * @throws ProjectOnlineApplyException
	 */
	void excuteConfirm(Long applyId, Long executorId, OperationResult result, String description) throws ProjectOnlineApplyException;

	ProjectOnlineApply getConfirmExecuteViewModel(Long id) throws ProjectOnlineApplyException;

	ProjectOnlineApply getEasyUiRowData(Long id) throws ProjectOnlineApplyException;

	ProjectOnlineApply getEditViewDataById(Long id) throws ProjectOnlineApplyException;

	ProjectOnlineApply getStartExecuteViewData(Long id) throws ProjectOnlineApplyException;

	ProjectOnlineApply getTestConfirmViewModel(Long id) throws ProjectOnlineApplyException;

	ProjectOnlineApply getVerifyViewData(Long id) throws ProjectOnlineApplyException;

	/**
	 * 查看申请详情
	 * 
	 * @param applyId 申请id
	 * @return 数据模型
	 */
	ProjectOnlineApplyDetailDto getViewDataById(Long applyId);

	void insertProjectOnlineApply(ProjectOnlineApplyUpdateDto projectOnlineApply) throws ProjectOnlineApplyException;

	/**
	 * 项目经理确认
	 * 
	 * @param applyId     申请id
	 * @param executorId  执行人id
	 * @param result      审批意见
	 * @param description 描述
	 * @throws ProjectOnlineApplyException
	 */
	void projectManagerConfirm(Long applyId, Long executorId, OperationResult result, String description) throws ProjectOnlineApplyException;

	/**
	 * 保存并提交
	 * 
	 * @param dto 数据模型
	 * @throws ProjectOnlineApplyException
	 */
	void saveAndSubmit(ProjectOnlineApplyUpdateDto dto) throws ProjectOnlineApplyException;

	/**
	 * 新增或修改上线申请
	 * 
	 * @param dto 数据模型
	 * @throws ProjectOnlineApplyException
	 */
	void saveOrUpdate(ProjectOnlineApplyUpdateDto dto) throws ProjectOnlineApplyException;

	/**
	 * 开始执行
	 * 
	 * @param applyId     申请id
	 * @param executorId  执行人id
	 * @param executors   运维分配的执行人
	 * @param description 描述
	 * @throws ProjectOnlineApplyException
	 */
	void startExecute(Long applyId, Long executorId, Set<Long> executors, String description) throws ProjectOnlineApplyException;

	/**
	 * 提交申请进入上线操作流程
	 * 
	 * @param applyId 申请id
	 */
	void submit(Long applyId) throws ProjectOnlineApplyException;

	/**
	 * 测试确认
	 * 
	 * @param applyId     申请id
	 * @param executorId  执行人id
	 * @param result      审批意见
	 * @param description 描述
	 * @throws ProjectOnlineApplyException
	 */
	void testerConfirm(Long applyId, Long executorId, OperationResult result, String description) throws ProjectOnlineApplyException;

	void updateProjectOnlineApply(ProjectOnlineApplyUpdateDto dto) throws ProjectOnlineApplyException;

	/**
	 * 验证
	 * 
	 * @param applyId     申请id
	 * @param verifierId  验证人id
	 * @param result      验证结果
	 * @param description 描述
	 * @throws ProjectOnlineApplyException
	 */
	void verify(Long applyId, Long verifierId, OperationResult result, String description) throws ProjectOnlineApplyException;

	ProjectOnlineApply getProjectManagerConfirmViewModel(Long id) throws ProjectOnlineApplyException;

	ProjectOnlineApply getDetailViewData(Long id) throws ProjectOnlineApplyException;

	/**
	 * 根据申请编号查询上线申请
	 * 
	 * @param serialNumber
	 * @return
	 * @throws ProjectOnlineApplyException 
	 */
	ProjectOnlineApply getProjectManagerConfirmViewModel(String serialNumber) throws ProjectOnlineApplyException;

}