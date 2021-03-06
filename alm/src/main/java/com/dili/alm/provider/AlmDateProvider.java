package com.dili.alm.provider;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * Created by asiamaster on 2017/5/31 0031.
 */
@Component
public class AlmDateProvider implements ValueProvider {

	private static final List<ValuePair<?>> buffer;

	static {
		buffer = new ArrayList<ValuePair<?>>();
		buffer.add(new ValuePairImpl(EMPTY_ITEM_TEXT, null));
	}

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		return buffer;
	}

	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals("")) {
			return "";
		}
		if (obj instanceof Instant) {
			// 输出yyyy-MM-dd HH:mm:ss格式字符串
			return DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(((Instant) obj));
		}
		if (obj instanceof LocalDateTime) {
			// 输出yyyy-MM-dd HH:mm:ss格式字符串
			return ((LocalDateTime) obj)
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (obj instanceof Date) {
			return sdf.format((Date) obj);
		}
		Long time = obj instanceof Long ? (Long) obj : obj instanceof String ? Long.parseLong(obj.toString()) : 0;
		return sdf.format(new Date(time));
	}

}
