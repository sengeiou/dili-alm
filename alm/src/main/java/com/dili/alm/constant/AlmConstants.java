package com.dili.alm.constant;

/**
 * Created by asiamaster on 2017/10/24 0024.
 */
public class AlmConstants {

	//数据字典值表中的数据权限类型为项目
	public static final String DATA_AUTH_TYPE_PROJECT = "Project";
	public static final String ROLE_CODE = "role_code";
	public static final String ROLE_CODE_WYH = "wyh";
	public static final String ROLE_CODE_WYH_LEADER = "wyh_leader";


	//团队成员状态: 加入/离开
	public enum MemberState {
		LEAVE(0),
		JOIN(1);

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

	public enum ApplyState{
		APPLY(1),APPROVE(2),PASS(3),NOPASS(4);

		private int code;

		ApplyState(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public boolean check(Object value){
			try{
				return Integer.parseInt(value.toString()) == this.code;
			}catch (Exception ignored){}
			return false;
		}
	}
	public enum ChangeState{
		APPLY(1),APPROVE(2),PASS(3),NOPASS(4),VRIFY(5);

		private int code;

		ChangeState(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public boolean check(Object value){
			try{
				return Integer.parseInt(value.toString()) == this.code;
			}catch (Exception ignored){}
			return false;
		}
	}

	public enum ApproveType{
		APPLY("apply"),CHANGE("change"),COMPLETE("complete");

		private String code;

		ApproveType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

	}
	
	//状态
	public enum TaskStatus{
		
		NOTSTART(0),START(1),PAUSE(2),COMPLETE(3),NOTCOMPLETE(4);
		
		public int code;
		
		TaskStatus(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}
}
