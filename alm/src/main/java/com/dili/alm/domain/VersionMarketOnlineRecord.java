package com.dili.alm.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-04-03 09:10:41.
 */
@Table(name = "`version_market_online_record`")
public interface VersionMarketOnlineRecord extends IBaseDomain {

	@Id
	@Column(name = "`version_id`")
	@FieldDef(label = "版本id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getVersionId();

	void setVersionId(Long versionId);

	@Id
	@Column(name = "`market_code`")
	@FieldDef(label = "市场编号，数据字典配置", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	String getMarketCode();

	void setMarketCode(String marketCode);
}