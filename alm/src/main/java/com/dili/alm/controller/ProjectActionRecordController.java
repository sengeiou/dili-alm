package com.dili.alm.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
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
import com.dili.alm.domain.ProjectAction;
import com.dili.alm.domain.ProjectActionRecordConfig;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.ProjectActionRecordConfigPostData;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectActionRecordConfigService;
import com.dili.alm.service.ProjectActionRecordService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.session.SessionContext;

import gui.ava.html.image.generator.HtmlImageGenerator;
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

	private static final String DATA_AUTH_TYPE = "Project";

	@Autowired
	ProjectActionRecordService projectActionRecordService;
	@Autowired
	private ProjectActionRecordConfigService configService;
	@Autowired
	private DataDictionaryService ddService;

	@ApiOperation("跳转到ProjectActionRecord页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		@SuppressWarnings({ "rawtypes" })
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (!CollectionUtils.isEmpty(dataAuths)) {
			List<Long> projectIds = new ArrayList<>();
			dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
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
			config.setShowDate(data.getShowDate().get(i));
			configs.add(config);
		}
		int rows = this.configService.batchUpdateSelective(configs);
		return rows > 0 ? BaseOutput.success() : BaseOutput.failure();
	}

	@ResponseBody
	@RequestMapping("/gantt.json")
	public BaseOutput<Object> gantJson(@RequestParam Long projectId, String[] actionCode) {
		return BaseOutput.success().setData(this.projectActionRecordService.getGanntData(projectId,
				actionCode != null ? Arrays.asList(actionCode) : null));
	}

	@ResponseBody
	@RequestMapping("/actions.json")
	public List<Map<String, String>> actions() {
		List<Map<String, String>> list = new ArrayList<>(ProjectAction.values().length);
		for (ProjectAction action : ProjectAction.values()) {
			Map<String, String> map = new HashMap<>(2);
			map.put("code", action.getCode());
			map.put("name", action.getName());
			list.add(map);
		}
		return list;
	}

	@ResponseBody
	@RequestMapping("/download")
	public void download(@RequestParam String html, HttpServletResponse response) {
		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		imageGenerator.loadHtml(html);
		try {
			ImageIO.write(imageGenerator.getBufferedImage(), "png", response.getOutputStream());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}