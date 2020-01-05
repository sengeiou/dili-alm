package com.dili.alm.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.ProjectActionRecordConfig;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.ProjectActionRecordConfigPostData;
import com.dili.alm.domain.dto.ProjectActionRecordExportDto;
import com.dili.alm.provider.ProjectActionProvider;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectActionRecordConfigService;
import com.dili.alm.service.ProjectActionRecordService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.session.SessionContext;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-06-21 12:00:46.
 */
@Api("/projectActionRecord")
@Controller
@RequestMapping("/projectActionRecord")
public class ProjectActionRecordController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectActionRecordController.class);

	private static final String DATA_AUTH_TYPE = "project";

	@Autowired
	ProjectActionRecordService projectActionRecordService;
	@Autowired
	private ProjectActionRecordConfigService configService;
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private ProjectActionProvider actionProvider;

	@ApiOperation("跳转到ProjectActionRecord页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		@SuppressWarnings({ "rawtypes" })
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (!CollectionUtils.isEmpty(dataAuths)) {
			List<Long> projectIds = new ArrayList<>();
			dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
			modelMap.addAttribute("projects", AlmCache.getInstance().getProjectMap().values().stream()
					.filter(p -> projectIds.contains(p.getId())).collect(Collectors.toList()));
		}
		return "projectActionRecord/index";
	}

	@GetMapping("/setting.html")
	public String setting(ModelMap modelMap) throws Exception {
		List<ProjectActionRecordConfig> list = this.configService
				.list(DTOUtils.newDTO(ProjectActionRecordConfig.class));
		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("actionCode", "projectActionProvider");
		metadata.put("actionDateType", "actionDateTypeProvider");
		modelMap.addAttribute("configs", ValueProviderUtils.buildDataByProvider(metadata, list));

		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.GANTT_COLOR_CODE);
		if (dd != null && CollectionUtils.isNotEmpty(dd.getValues())) {
			modelMap.addAttribute("colors", dd.getValues());
		}
		return "projectActionRecord/setting";
	}

	@ResponseBody
	@PostMapping("/setting.html")
	public BaseOutput<Object> updateSetting(ProjectActionRecordConfigPostData data) {
		List<ProjectActionRecordConfig> configs = new ArrayList<>(data.getId().size());
		for (int i = 0; i < data.getId().size(); i++) {
			ProjectActionRecordConfig config = DTOUtils.newDTO(ProjectActionRecordConfig.class);
			config.setActionCode(data.getActionCode().get(i));
			config.setActionDateType(data.getActionDateType().get(i));
			config.setColor(data.getColor().get(i));
			config.setHint(data.getHint().get(i));
			if (data.getHint().get(i)) {
				config.setHintMessage(data.getHintMessage().get(i));
			}
			config.setId(data.getId().get(i));
			config.setShowDate(data.getShowDate().get(i) != null && data.getShowDate().get(i));
			configs.add(config);
		}
		int rows = this.configService.batchUpdateSelective(configs);
		return rows > 0 ? BaseOutput.success() : BaseOutput.failure();
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/gantt.json")
	public BaseOutput<Object> gantJson(@RequestParam Long projectId, String[] actionCode) {
		return BaseOutput.success().setData(this.projectActionRecordService.getGanntData(projectId,
				actionCode != null ? Arrays.asList(actionCode) : null));
	}

	@ResponseBody
	@RequestMapping("/actions.json")
	public List<Map<String, String>> actions() {
		ProjectActionRecordConfig query = DTOUtils.newDTO(ProjectActionRecordConfig.class);
		query.setShowDate(true);
		List<ProjectActionRecordConfig> configs = this.configService.list(query);
		List<Map<String, String>> target = new ArrayList<>(configs.size());
		configs.forEach(c -> target.add(new HashMap<String, String>() {
			{
				put("name", actionProvider.getDisplayText(c.getActionCode(), null, null));
				put("code", c.getActionCode());
			}
		}));
		return target;
	}

	@ResponseBody
	@RequestMapping("/export")
	public void export(@RequestParam Long projectId, String[] actionCode, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		List<ProjectActionRecordExportDto> datas = this.projectActionRecordService.getExportData(projectId,
				Arrays.asList(actionCode));

		// 标题
		List<ExcelExportEntity> entityList = new ArrayList<ExcelExportEntity>();
		// 内容
		List<Map<String, Object>> dataResult = new ArrayList<Map<String, Object>>();
		entityList.add(new ExcelExportEntity("活动", "actionName", 25));
		entityList.add(new ExcelExportEntity("计划开始时间", "startDate", 25));
		entityList.add(new ExcelExportEntity("计划结束时间", "endDate", 25));
		entityList.add(new ExcelExportEntity("发生时间", "actionDate", 25));

		entityList.add(new ExcelExportEntity("逾期天数", "overDays", 25));
		// 内容
		if (CollectionUtils.isNotEmpty(datas)) {
			for (ProjectActionRecordExportDto dto : datas) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("actionName", dto.getActionName());
				map.put("startDate", dto.getStartDate());
				map.put("endDate", dto.getEndDate());
				map.put("actionDate", dto.getActionDate());
				map.put("overDays", dto.getOverDays());
				dataResult.add(map);
			}
		}

		HSSFWorkbook excel = (HSSFWorkbook) ExcelExportUtil.exportExcel(new ExportParams("项目进程展示", "项目进程展示"),
				entityList, dataResult);
		String rtn = getRtn("项目里程碑数据.xls", request);
		response.setContentType("text/html");
		response.setHeader("Content-Disposition", "attachment;" + rtn);
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			excel.write(os);
			os.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	public static String getRtn(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
		String userAgent = request.getHeader("User-Agent");
		String rtn = "filename=\"" + fileName + "\"";
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			// IE浏览器，只能采用URLEncoder编码
			if (userAgent.contains("msie")) {
				rtn = "filename=\"" + fileName + "\"";
			}
			// Opera浏览器只能采用filename*
			else if (userAgent.contains("opera")) {
				rtn = "filename*=UTF-8''" + fileName;
			}
			// Safari浏览器，只能采用ISO编码的中文输出
			else if (userAgent.contains("safari")) {
				rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
			} else if (userAgent.contains("mozilla")) {
				rtn = "filename*=UTF-8''" + fileName;
			}
		}
		return rtn;
	}

}