package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.cache.AlmCache;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * Created by asiamaster on 2017/10/18 0018.
 */
@Component
public class MemberProvider implements ValueProvider {

	@Autowired
	private UserRpc userRpc;
	
	@Override
	public List<ValuePair<?>> getLookupList(Object o, Map map, FieldMeta fieldMeta) {
		ArrayList buffer = new ArrayList<ValuePair<?>>();
		AlmCache.getInstance().getUserMap().forEach((k, v) -> {
			buffer.add(new ValuePairImpl(v.getRealName(), k));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {
		if (o == null)
			return null;
		User user = AlmCache.getInstance().getUserMap().get(Long.parseLong(o.toString()));
		if(user == null) {
			BaseOutput<User> output = userRpc.findUserById(Long.parseLong(o.toString()));
			if (output.isSuccess()) {
				 user = output.getData();
				 AlmCache.getInstance().getUserMap().put(user.getId(), user);
			}
		}
		return user == null ? null : user.getRealName();
	}
}
