package com.dili.alm.constant;

/**
 * Created by asiamaster on 2017/10/24 0024.
 */
public class AlmConstants {

	// 数据字典值表中的数据权限类型为项目
	public static final String DATA_AUTH_TYPE_PROJECT = "Project";
	public static final String ROLE_CODE = "role_code";
	public static final String ROLE_CODE_WYH = "wyh";
	public static final String ROLE_CODE_WYH_LEADER = "wyh_leader";

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
		APPLY(1), APPROVE(2), PASS(3), NOPASS(4);

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
		APPLY(1), APPROVE(2), PASS(3), NOPASS(4), VRIFY(5);

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
}
