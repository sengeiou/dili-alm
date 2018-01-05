package com.dili.alm.provider;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.VerifyApproval;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.service.VerifyApprovalService;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by asiamaster on 2017/10/18 0018.
 */
@Component
public class RoleNameProvider implements ValueProvider {

	@Autowired
	private RoleRpc roleRpc;

	@Autowired
	private VerifyApprovalService verifyApprovalService;


	@Override
	public List<ValuePair<?>> getLookupList(Object o, Map map, FieldMeta fieldMeta) {
		return null;
	}

	@Override
	public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {

		JSONObject json = (JSONObject) map;
		try {
			if (Objects.equals(json.getString("field"), "roleName")) {
				if(json.get("_rowData") instanceof VerifyApproval){
					VerifyApproval approve = json.getObject("_rowData", VerifyApproval.class);
					approve = verifyApprovalService.get(approve.getId());
					return roleRpc.listRoleNameByUserId(approve.getApprover()).getData();
				}
			}
		}catch (Exception ignored){}
		if(o == null) return null;
		return roleRpc.listRoleNameByUserId(Long.parseLong(o.toString())).getData();
	}
}
