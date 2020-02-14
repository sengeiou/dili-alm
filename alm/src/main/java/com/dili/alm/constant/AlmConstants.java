package com.dili.alm.constant;

/**
 * Created by asiamaster on 2017/10/24 0024.
 */
public class AlmConstants {

	// 序列号生成器步长
	public static final int SEQUENCE_NUMBER_STEP_LENGTH = 50;
	// 序列号生成器步长
	public static final int DEMAND_SEQUENCE_NUMBER_STEP_LENGTH = 1;

	// 数据字典值表中的数据权限类型为项目
	public static final String DATA_AUTH_TYPE_PROJECT = "project";
	public static final String ROLE_CODE = "role_code";
	public static final String ROLE_CODE_WYH = "wyh";
	public static final String ROLE_CODE_WYH_LEADER = "wyh_leader";
	public static final String ROLE_CODE_WYH_SUPER_LEADER = "wyh_super_leader";

	// 运维部门编码
	public static final String OPERATION_DEPARTMENT_CODE = "maintenance";
	public static final String MARKET_CODE = "market";
	public static final String ENVIRONMENT_CODE = "environment";
	public static final String REGIONAL_CODE = "regional";

	// 部门经理数据字典配置编码
	public static final String DEPARTMENT_MANAGER_ROLE_CONFIG_CODE = "department_manager_role_config";
	// 研发中心总经理数据字典编码
	public static final String GENERAL_LEADER_CODE = "general_leader";
	// 运维部部门经理数据字典编码
	public static final String OPERATION_MANAGER_CODE = "operation_manager";
	// 测试部部门经理数据字典编码
	public static final String TEST_MANAGER_CODE = "test_manager";
	// 总经理助理数据字典编码
	public static final String GENERAL_MANAGER_ASSISTANT_CODE = "general_manager_assistant";

	// 项目类型数据字典编码
	public static final String PROJECT_TYPE_CODE = "project_type";
	// 项目状态数据字典编码
	public static final String PROJECT_STATE_CODE = "project_state";
	// 项目文件类型数据字典编码
	public static final String PROJECT_FILE_TYPE_CODE = "project_file_type";
	// 项目阶段名称数据字典编码
	public static final String APPLY_PLAN_PHASE_CODE = "apply_plan_phase";
	// 团队角色数据字典编码
	public static final String TEAM_ROLE_CODE = "team_role";
	// 资源使用环境数据字典编码
	public static final String RESOURCE_ENVIRONMENT_CODE = "resource_environment";
	// 差旅费明细费用配置
	public static final String TRAVEL_COST_DETAIL_CONFIG_CODE = "travel_cost_item";
	// 工单类型数据字典配置
	public static final String WORK_ORDER_TYPE_CODE = "work_order_type";
	// 工单优先级数据字典配置
	public static final String WORK_ORDER_PRIORITY_CODE = "work_order_priority";
	// 工单受理人数据字典配置
	public static final String OUTSIDE_WORK_ORDER_RECEIVERS_CODE = "work_order_receivers";
	// 研发中心工单受理人数据字典配置
	public static final String DEV_WORK_ORDER_RECEIVERS_CODE = "dev_work_order_receivers";
	// 项目进程展示背景色数据字典配置
	public static final String GANTT_COLOR_CODE = "gantt_color";
	// 工单受理人过滤开不安数据字典配置
	public static final String WORK_ORDER_MEMBER_FILTER_SWITCH = "work_order_member_filter_switch";
	// 工单超时自动关闭时间
	public static final long CLOSE_OVER_TIME = 2 * 24 * 60 * 60 * 1000;
		//需求状态数据字典配置
	public static final String DEMAND_STATUS = "demand_status";
	//需求类型数据字典配置
	public static final String DEMAND_TYPE = "demand_type";
	//需求流程类型数据字典配置
	public static final String DEMAND_PROCESS_TYPE = "demand_process_type";
	// 所属系统的字典值
	public static final String ALM_SYSTEM_CODE = "ALM";
	//字典表开发环境code码
	public static final String ALM_ENVIRONMENT = "environment";
	//部门所属市场Code码
	public static final String ALM_FIRM_CODE = "szpt";
	
    /**
     * 树状结构，菜单ID前缀
     */
    public static final String ALM_PROJECT_PREFIX = "alm_";
	// 团队成员状态: 加入/离开
	public enum MemberState {
		LEAVE(0), JOIN(1);

		private int code;

		MemberState(int code) {
			this.code = code;
		}

		/**
		 * 根据类型编码获取MemberState
		 *
		 * @param code
		 * @return
		 */
		public static MemberState getMemberStateByCode(int code) {
			for (MemberState memberState : MemberState.values()) {
				if (memberState.getCode() == code) {
					return memberState;
				}
			}
			return null;
		}

		public int getCode() {
			return code;
		}
	}

	public enum ApplyState {
		APPLY(1), APPROVE(2), PASS(3), NOPASS(4),REFUSED(6);

		private int code;

		ApplyState(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public boolean check(Object value) {
			try {
				return Integer.parseInt(value.toString()) == this.code;
			} catch (Exception ignored) {
			}
			return false;
		}
	}

	public enum ChangeState {
		APPLY(1), APPROVE(2), PASS(3), NOPASS(4), VRIFY(5),REFUSED(6);

		private int code;

		ChangeState(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public boolean check(Object value) {
			try {
				return Integer.parseInt(value.toString()) == this.code;
			} catch (Exception ignored) {
			}
			return false;
		}
	}

	public enum ApproveType {
		APPLY("apply"), CHANGE("change"), COMPLETE("complete");

		private String code;

		ApproveType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

	}

	public enum LogSort {
		LOGNUMBER("log_number"), OPERATORID("operator_id");

		private String code;

		LogSort(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

	}

	// 状态
	public enum TaskStatus {

		NOTSTART(0), START(1), PAUSE(2), COMPLETE(3), NOTCOMPLETE(4);

		public int code;

		TaskStatus(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}

	// 状态
	public enum MessageType {

		APPLY(1), APPLYRESULT(2), TASK(3), COMPLETE(4), COMPLETERESULT(5), CHANGE(6), CHANGERESULT(7), OTHER(8);

		public int code;

		MessageType(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	
	// 请求状态 未提交，提交，审批中，完成
	public enum DemandStatus {

		NOTSUBMIT(1), SUBMIT(2), APPROVING(3), COMPLETE(4),DELETE(5);

		public int code;

		DemandStatus(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	
	// 流程状态 部门经理同意 接受需求 指派人 反馈方案 需求管理员同意 驳回编辑
	public enum DemandProcessStatus {

		DEPARTMENTMANAGER("departmentManager"), ACCEPT("accept"), RECIPROCATE("reciprocate"), FEEDBACK("feedback"),DEMANDMANAGER("demandManager"),BACKANDEDIT("backAndEdit"),FINISH("finish");

		public String code;

		DemandProcessStatus(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}
}
