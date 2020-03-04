package com.dili.alm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dili.alm.cache.AlmCache;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-24 14:31:10.
 */
@Component
public class OnlineDataApplyStateProvider implements ValueProvider {

	@Override
	public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		AlmCache.getInstance().getProjectStateMap().forEach((k, v) -> {
			buffer.add(new ValuePairImpl<String>(v, k));
		});
		return buffer;
	}

	
	/**
	 *   
    	   if(row.dataStatus==1){
           	      return "<font color='gray'>驳回</font>";
    	   }else if(row.dataStatus==2){
    		    	return "<font color='gray'>项目经理确认</font>";
           }else if(row.dataStatus==3){
   		          return "<font color='gray'>测试人员确认</font>";
           }else if(row.dataStatus==4){
   		    	return "<font color='gray'>dba确认</font>";
           }else if(row.dataStatus==5){
    		   return "<font color='gray'>线上验证</font>";
           }else if(row.dataStatus==6){
              	return "<font color='gray'>归档</font>";
           }else if(row.dataStatus==0){
              	return "<font color='green'>保存</font>";
           }else{
           	   return "<font color='orange'>其他</font>";
           }
	 */
	@Override
	public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
		if (obj == null || obj.equals(""))
			return null;
		
		
		String temp="其他";
        if(obj.toString().equals("0")) {
			temp="保存";
		}else  if(obj.toString().equals("1")) {
			temp="驳回";
		}else if(obj.toString().equals("2")) {
			temp="项目经理确认";		
		}else  if(obj.toString().equals("3")) {
			temp="测试人员确认";		
		}else if(obj.toString().equals("4")) {
			temp="dba确认";	
		}else  if(obj.toString().equals("5")) {
        	temp="线上验证";	
		}else if(obj.toString().equals("6")) {
        	 temp="归档";	
		}
        return temp;
	}
}