//package com.dili.alm.provider;
//
//import java.util.List;
//import java.util.Map;
//
//import com.dili.ss.dto.DTOUtils;
//import com.dili.ss.metadata.BatchProviderMeta;
//import com.dili.ss.metadata.provider.BatchDisplayTextProviderSupport;
//
//public class ProjectChangeBatchProvider extends BatchDisplayTextProviderSupport {
//
//	@Override
//	protected BatchProviderMeta getBatchProviderMeta(Map metaMap) {
//		BatchProviderMeta batchProviderMeta = DTOUtils.newDTO(BatchProviderMeta.class);
//		// 设置主DTO和关联DTO需要转义的字段名
//		batchProviderMeta.setEscapeFiled("name");
//		// 忽略大小写关联
//		batchProviderMeta.setIgnoreCaseToRef(true);
//		// 主DTO与关联DTO的关联(java bean)属性(外键)
//		batchProviderMeta.setFkField("projectApplyId");
//		// 关联(数据库)表的主键的字段名，默认取id
//		batchProviderMeta.setRelationTablePkField("id");
//		// 当未匹配到数据时，返回的值
//		batchProviderMeta.setMismatchHandler(t -> "");
//		return batchProviderMeta;
//	}
//
//	@Override
//	protected List getFkList(List<String> relationIds, Map metaMap) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}
