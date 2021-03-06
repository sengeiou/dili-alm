package com.dili.alm.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.ApproveResult;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.dto.DemandDto;
import com.dili.alm.exceptions.DemandExceptions;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.BasePage;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.exception.BusinessException;
import com.dili.ss.metadata.ValueProviderUtils;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2019-12-23 17:32:14.
 */
public interface DemandService extends BaseService<Demand, Long> {

	/**
	 * 自定义添加新需求方法
	 * 
	 * @param newDemand
	 *            需要添加字段
	 * @return
	 */
	public int addNewDemand(Demand newDemand) throws DemandExceptions;
	/***
	 * 完成流程
	 * @param newDemandCode 需求流程号
	 * @return
	 */
	public int finishStutas(Long id,boolean approved) throws DemandExceptions ;

	/**
	 * 直接提交
	 * 
	 * @param newDemand
	 *            需要添加字段
	 * @return
	 */
	public int submint(Demand newDemand) throws DemandExceptions;
	/**
	 * 流程中修改提交
	 * 
	 * @param newDemand
	 *            需要添加字段
	 * @return
	 */
	public BaseOutput reSubmint(Demand newDemand, String taskId) throws DemandExceptions;

	/**
	 * 分页查询
	 * 
	 * @return 查询列表
	 */
	public EasyuiPageOutput listPageForUser (DemandDto selectDemand);

	/**
	 * 定时重新设置自增值
	 */
	public void setDailyBegin();
	/**
	 * 查询未立项以及对应project的需求集合
	 * @param projectId
	 * @return
	 */
	List<Demand> queryDemandListToProject(Long projectId);
	/**
	 * 查询未立项以及对应project的需求集合
	 * @param ids
	 * @return
	 */
	List<Demand> queryDemandListByIds(String ids);
	/**
	 * 根据id和type组合查询对应id的需求集合
	 * @param id
	 * @param type
	 * @return
	 */
	List<Demand> queryDemandListByProjectIdOrVersionIdOrWorkOrderId(Long id,
			Integer type);
	/**
	 * 根据需求id返回显示集合
	 * @param id
	 * @return
	 * @throws Exception
	 */
	DemandDto getDetailViewData(Long id) throws Exception;

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    int logicDelete(Long id) throws DemandExceptions;
    /**
     * 根据编号获取需求
     * @param code
     * @return
     */
    Demand getByCode(String code);
    
    /**
     * 完成提交
     * @param code
     * @return
     */
    BaseOutput submitApprove(String code, String taskId,Byte status,String processTypeString,Integer imperative);
    
    /**
     * 添加指定的接收人
     * @param codesubmitApproveAndAccept
     * @return
     */
    BaseOutput submitApproveAndAccept(Long executorId, String taskId);
    
    /**
     * 驳回需求申请
     * @param code
     * @return
     */
    BaseOutput rejectApprove(String code, String taskId,String rejectType);
    /**
     * 驳回需求申请,带有设置参数的驳回
     * @param code
     * @return
     */
    BaseOutput rejectApproveForFeedback(String code, String taskId,String rejectType,Long executorId);
    /**
     * 是否是流程驳回可编辑的
     * @param code
     * @return
     */
    int isBackEdit(Demand demand);
    /**
     * 记录流程操作日志
     * @param code
     * @return
     */
	void saveOprationRecord(String demandCode,String description,int operationValue,String operationName,ApproveResult result) throws DemandExceptions;
    /**
     * 流程操作日志列表
     * @param code
     * @return
     */
	EasyuiPageOutput getOprationRecordList(String demandCode);
	
    /**
     * 添加分配人
     * @param  executorId  taskId
     * @return
     */
    BaseOutput submitApproveForAssign(Long executorId, String taskId);
    /**
     * 数字平台,接收并判定是否需要转发
     * @param taskId
     * @return
     */
    BaseOutput submitApproveForAccept(String code,String taskId,Long executorId);
    /**
     * 获取部门经理ID
     * @param 申请人ID
     * @return
     */
    Long departmentManagerId(Long applyId)throws DemandExceptions;
	
}