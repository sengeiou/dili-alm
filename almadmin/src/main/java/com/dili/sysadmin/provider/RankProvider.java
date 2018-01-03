package com.dili.sysadmin.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.dili.sysadmin.cache.SysadminCache;
import com.dili.sysadmin.domain.dto.DataDictionaryDto;
import com.dili.sysadmin.domain.dto.DataDictionaryValueDto;
import com.dili.sysadmin.rpc.DataDictrionaryRPC;

@Component
public class RankProvider implements ValueProvider, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private DataDictrionaryRPC dataDictionaryRPC;
	private static final String RANK_CODE = "rank";

	@Override
	public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
		this.init();
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		SysadminCache.RANK_MAP.forEach((k, v) -> {
			buffer.add(new ValuePairImpl<String>(v, k));
		});
		return buffer;
	}

	@Override
	public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
		if (val == null || val.equals(""))
			return null;
		return SysadminCache.RANK_MAP.get(val.toString());
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.init();
	}

	public void init() {
		if (SysadminCache.RANK_MAP.isEmpty()) {
			BaseOutput<DataDictionaryDto> output = this.dataDictionaryRPC.findDataDictionaryByCode(RANK_CODE);
			if (!output.isSuccess()) {
				return;
			}
			if (output.getData() == null) {
				return;
			}
			List<DataDictionaryValueDto> values = output.getData().getValues();
			if (CollectionUtils.isEmpty(values)) {
				return;
			}
			values.forEach(v -> {
				SysadminCache.RANK_MAP.put(v.getValue(), v.getCode());
			});
		}
	}

}
