package com.dili.alm.provider;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.VerifyApproval;
import com.dili.alm.service.VerifyApprovalService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValueProvider;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.rpc.RoleRpc;

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
					BaseOutput<List<Role>> listRoleByUserId = roleRpc.listByUser(approve.getApprover(),"");	
					return listRoleByUserId.getData().stream().map(Role::getRoleName).collect(Collectors.joining(","));				
				}
			}
		}catch (Exception ignored){}
		if(o == null) return null;
		BaseOutput<List<Role>> listRoleByUserId = roleRpc.listByUser(Long.parseLong(o.toString()),"");
		
		return listRoleByUserId.getData().stream().map(Role::getRoleName).collect(Collectors.joining(","));
	}
}
