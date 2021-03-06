package com.dili.alm.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WeeklyDetails;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.service.WeeklyDetailsService;
import com.dili.alm.service.WeeklyService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.session.SessionContext;



/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 12:37:17.
 */

@Controller
@RequestMapping("/weekly")
public class WeeklyController {
	private static final String WEEKLY = "weekly";

	private static final String DATA_AUTH_TYPE = "project";

	@Autowired
	WeeklyService weeklyService;
	@Autowired
	WeeklyDetailsService weeklyDetailsService;


	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "weekly/index";
	}


	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Weekly> list(Weekly weekly) {
		return weeklyService.list(weekly);
	}


	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(WeeklyPara weeklyPara) throws Exception {
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			new EasyuiPageOutput(0L, Collections.emptyList()).toString();
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
		weeklyPara.setProjectIds(projectIds);
		return weeklyService.getListPage(weeklyPara).toString();
	}

	/**
	 * 返回项目经理
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listUserByWeekly.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<WeeklyPara> listUserByWeekly() throws Exception {
		return weeklyService.getUser();
	}


	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(Weekly weekly) {
		weeklyService.insertSelective(weekly);
		return BaseOutput.success("新增成功");
	}

	@RequestMapping(value = "/save", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput save(WeeklyDetails WeeklyDetails) {
		WeeklyDetails.setIsSubmit(1);
		WeeklyDetails wd = weeklyDetailsService.getWeeklyDetailsByWeeklyId(WeeklyDetails.getWeeklyId());
		Weekly weekly = null;
		if (wd == null) {
			weeklyDetailsService.createInsert(WeeklyDetails);
			weekly = weeklyService.getWeeklyById(WeeklyDetails.getWeeklyId());
		} else {
			weeklyDetailsService.updateSelective(WeeklyDetails);
			weekly = weeklyService.getWeeklyById(WeeklyDetails.getWeeklyId());

		}

		return BaseOutput.success("周报提交成功")
				.setData(String.valueOf(weekly.getId() + ":" + DateUtil.getDate(weekly.getCreated()) + "周报提交"));
	}

	@RequestMapping(value = "/updateWeeklyDetails", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput updateWeeklyDetails(WeeklyDetails WeeklyDetails) {

		WeeklyDetails wd = weeklyDetailsService.getWeeklyDetailsByWeeklyId(WeeklyDetails.getWeeklyId());
		if (wd == null) {
			weeklyDetailsService.createInsert(WeeklyDetails);
			Weekly weekly = weeklyService.getWeeklyById(WeeklyDetails.getWeeklyId());
			return BaseOutput.success("周报保存成功")
					.setData(String.valueOf(weekly.getId() + ":" + DateUtil.getDate(weekly.getCreated()) + "周报"));
		} else {
			weeklyDetailsService.updateSelective(WeeklyDetails);
			Weekly weekly = weeklyService.getWeeklyById(wd.getWeeklyId());
			return BaseOutput.success("周报保存成功")
					.setData(String.valueOf(weekly.getId() + ":" + DateUtil.getDate(weekly.getCreated()) + "周报"));
		}

	}

	@RequestMapping(value = "/updateWeeklyDetailsCancel", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput updateWeeklyDetailsCancel(WeeklyDetails WeeklyDetails) {

		weeklyDetailsService.updateSelective(WeeklyDetails);
		return BaseOutput.success("修改成功");
	}

	@RequestMapping(value = "/saveMaxQu", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput saveMaxQu(String str, String id) {
		long longid = Long.parseLong(id);
		weeklyService.updateMaxQuestion(str, longid);
		return BaseOutput.success("重大问题保存成功");
	}

	@RequestMapping(value = "/saveMaxRist", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput saveMaxRist(String str, String id) {
		long longid = Long.parseLong(id);
		weeklyService.updateMaxRist(str, longid);
		return BaseOutput.success("重要风险保存成功");
	}


	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(Weekly weekly) {
		weeklyService.updateSelective(weekly);
		return BaseOutput.success("修改成功");
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		weeklyService.delete(id);
		WeeklyDetails newDTO = DTOUtils.newDTO(WeeklyDetails.class);
		newDTO.setWeeklyId(id);
		List<WeeklyDetails> list = weeklyDetailsService.listByExample(newDTO);
		List<Long> keys = new ArrayList<Long>();
		for (WeeklyDetails weeklyDetails : list) {
			keys.add(weeklyDetails.getId());
		}
		weeklyDetailsService.delete(keys);
		return BaseOutput.success("删除成功");
	}


	@RequestMapping(value = "/getDescById", method = RequestMethod.GET)
	public ModelAndView getDescById(String id) {

		Map<Object, Object> map = weeklyService.getDescByIdWeek(id);
		ModelAndView mv = new ModelAndView();
		// 项目周报
		mv.addObject("pd", (ProjectWeeklyDto) map.get("pd"));
		// 本周项目版本
		mv.addObject("pv", (String) map.get("pv"));
		// 本周进展情况
		mv.addObject("td", (List<TaskDto>) map.get("td"));
		// 下周工作计划
		mv.addObject("wk", (List<NextWeeklyDto>) map.get("wk"));
		// 当前重要风险
		mv.addObject("wr", map.get("wr"));
		// 当前重要问题
		mv.addObject("wq", map.get("wq"));
		// 项目总体情况描述
		mv.addObject("wDetails", map.get("wDetails"));

		mv.setViewName("weekly/indexDesc");
		return mv;
	}

	@RequestMapping("download")
	public ResponseEntity<byte[]> download(String id) throws IOException {

		File file = new File(WEEKLY + ".xls");
		weeklyService.downLoad(file, id);

		HttpHeaders headers = new HttpHeaders();
		String fileName = new String("周报下载.xls".getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
	}


	@RequestMapping(value = "/getDescAddByProjectId", method = RequestMethod.GET)
	public ModelAndView getDescAddByProjectId(String projectId) {

		ModelAndView mv = new ModelAndView();
		mv.addObject("returnProjectId", projectId);

		Map<String, Weekly> wkMap = weeklyService.insertWeeklyByprojectId(projectId);
		Weekly wk = DTOUtils.newDTO(Weekly.class);
		Map<Object, Object> map = null;
		if (wkMap.get("one") == null) {
			map = weeklyService.getDescAddById(wkMap.get("two").getId() + "");
		} else {
			map = weeklyService.getDescAddById(wkMap.get("one").getId() + "");
		}

		// 项目周报
		mv.addObject("pd", (ProjectWeeklyDto) map.get("pd"));
		wk.setProgress(((ProjectWeeklyDto) map.get("pd")).getCompletedProgress());// 存项目
		wk.setProjectId(Long.parseLong(projectId));
		wk.setId(Long.parseLong(((ProjectWeeklyDto) map.get("pd")).getId()));
		weeklyService.updateSelective(wk);
		// 本周项目版本
		mv.addObject("pv", (String) map.get("pv"));
		// 本周进展情况
		mv.addObject("td", (List<TaskDto>) map.get("td"));
		// 下周工作计划
		mv.addObject("wk", (List<NextWeeklyDto>) map.get("wk"));
		// 当前重要风险
		mv.addObject("wr", map.get("wr"));
		// 当前重要问题
		mv.addObject("wq", map.get("wq"));
		// 项目总体情况描述
		mv.addObject("wDetails", map.get("wDetails"));

		mv.setViewName("weekly/addWeeklyDesc");
		return mv;
	}

	@ResponseBody
	@RequestMapping(value = "/IsWeeklySubmit", method = { RequestMethod.GET, RequestMethod.POST })
	public boolean IsWeeklySubmit(String weeklyId) {
		boolean flat = false;
		if (weeklyId != null) {
			WeeklyDetails wd = weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(weeklyId));
			if (wd != null) {
				if (wd.getIsSubmit() == 1) {
					flat = true;
				}
			}
		}
		return flat;

	}
}