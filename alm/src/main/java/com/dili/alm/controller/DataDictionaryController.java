package com.dili.alm.controller;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.DataDictionary;
import com.dili.alm.service.DataDictionaryService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("/dataDictionary")
@Controller
@RequestMapping("/dataDictionary")
public class DataDictionaryController {
	@Autowired
	DataDictionaryService dataDictionaryService;

	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "dataDictionary/index";
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<DataDictionary> list(DataDictionary dataDictionary) {
		return dataDictionaryService.list(dataDictionary);
	}

	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(DataDictionary dataDictionary) throws Exception {
		return dataDictionaryService.listEasyuiPageByExample(dataDictionary, true).toString();
	}

	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(DataDictionary dataDictionary) {
		Map metadata = new HashMap();
		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("created", datetimeProvider);
		metadata.put("modified", datetimeProvider);

		dataDictionaryService.insertSelective(dataDictionary);
		try {
			// 测试数据
			dataDictionary.setCreated(new Date());
			dataDictionary.setModified(new Date());

			List list = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(dataDictionary));
			return BaseOutput.success("插入成功").setData(list.get(0));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(DataDictionary dataDictionary) {
		Map metadata = new HashMap();
		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("created", datetimeProvider);
		metadata.put("modified", datetimeProvider);

		dataDictionaryService.updateSelective(dataDictionary);
		try {
			List list = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(dataDictionary));
			return BaseOutput.success("修改成功").setData(list.get(0));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		dataDictionaryService.delete(id);
		return BaseOutput.success("删除成功");
	}

	
	@RequestMapping(value = "/updateUapDataDictionaryList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseOutput<Object> updateUapDataDictionaryList() {
		dataDictionaryService.updateUapDataDictionaryList();
		return  BaseOutput.success("添加成功");
	}
}