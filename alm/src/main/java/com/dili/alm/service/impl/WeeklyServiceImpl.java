package com.dili.alm.service.impl;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.dao.WeeklyDetailsMapper;
import com.dili.alm.dao.WeeklyMapper;
import com.dili.alm.dao.WorkDayMapper;
import com.dili.alm.domain.DataDictionary;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WeeklyDetails;
import com.dili.alm.domain.WeeklyJson;
import com.dili.alm.domain.WorkDay;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.Page;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.DataDictionaryValueService;
import com.dili.alm.service.WeeklyDetailsService;
import com.dili.alm.service.WeeklyService;
import com.dili.alm.service.WorkDayService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WordExport;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.rpc.DepartmentRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;

/**
 * ��MyBatis Generator�����Զ����� This file was generated on 2017-11-29
 * 14:08:40.
 */
@Service
@Transactional
public class WeeklyServiceImpl extends BaseServiceImpl<Weekly, Long> implements WeeklyService {

	public static final String PROJECTTYPE = "项目类型";
	public static final String PROJECTTYPECODE = "project_type";
	public static final String PROJECTSTATUS = "项目状态";
	public static final String PROJECTTASKSTATUS = "3";
	public static final String YES = "YES";
	public static final String NO = "NO";
	public static final int ISSUBMIT = 1;// 已经提交
	public static final DecimalFormat df = new DecimalFormat("######0.00");
	@Autowired
	WeeklyMapper weeklyMapper;

	@Autowired
	WeeklyDetailsMapper weeklyDetailsMapper;
	@Autowired
	ProjectMapper projectMapper;
	@Autowired
	ProjectVersionMapper projectVersionMapper;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private DepartmentRpc departmentRpc;
	@Autowired
	WeeklyDetailsService weeklyDetailsService;
	@Autowired
	DataDictionaryService dataDictionaryService;
	@Autowired
	DataDictionaryValueService dataDictionaryValueService;
	@Autowired
	WorkDayService workDayService;
	@Autowired
	WorkDayMapper workDayMapper;
	@Autowired
	TeamMapper teamMapper;

	private static final String WEEKLY_SUBIMT_ROLE = "project_manager";

	public WeeklyMapper getActualDao() {
		return (WeeklyMapper) getDao();
	}

	@Override
	public Map<Object, Object> getDescById(String id) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		// 项目周报
		ProjectWeeklyDto pd = getProjectWeeklyDtoById(Long.parseLong(id));
		pd.setCompletedProgressInt(Integer.parseInt(pd.getCompletedProgress()));
		pd.setId(id);
		map.put("pd", pd);

		Weekly wkly = weeklyMapper.selectByPrimaryKey(Long.parseLong(id));
		WeeklyPara weeklyPara = new WeeklyPara();
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		weeklyPara.setStartDate(DateUtil.getDateStr(wkly.getStartDate()));
		weeklyPara.setEndDate(DateUtil.getDateStr(wkly.getEndDate()));

		// 本周进展情况
		JSONArray tdJson = JSON.parseArray(wkly.getCurrentWeek());
		List<TaskDto> td = new ArrayList<TaskDto>();
		if (tdJson != null)
			td = tdJson.toJavaList(TaskDto.class);

		List<String> listVersion = new ArrayList<String>();

		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i + 1);
			listVersion.add(td.get(i).getVersionId());
		}

		HashSet setVersion = new HashSet();
		setVersion.addAll(listVersion);// 给set填充
		listVersion.clear();//
		listVersion.addAll(setVersion);// 把set的

		// 本周项目版本
		map.put("pv", StringUtils.join(listVersion.toArray(), ","));

		map.put("td", td);
		weeklyPara.setId(Long.parseLong(id));
		// 当前重要风险
		String weeklyRist = wkly.getRisk();
		if (weeklyRist != null) {
			JSONArray weeklyRistJson = JSON.parseArray(weeklyRist);
			map.put("wr", weeklyRistJson.toJavaList(WeeklyJson.class));
		} else
			map.put("wr", null);

		// 当前重要问题
		String weeklyQuestion = wkly.getQuestion();
		if (weeklyQuestion != null) {
			JSONArray weeklyQuestionJson = JSON.parseArray(weeklyQuestion);
			map.put("wq", weeklyQuestionJson);
		} else {
			map.put("wq", null);
		}

		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		WorkDay nextWeeklyWorkDays = workDayService.getNextWeeklyWorkDays();
		weeklyPara.setStartDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkStartTime()) + " 00:00:00");
		weeklyPara.setEndDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkEndTime()) + " 23:59:59");

		// 下周工作计划
		JSONArray wkJson = JSON.parseArray(wkly.getNextWeek());
		List<NextWeeklyDto> wk = new ArrayList<NextWeeklyDto>();
		if (wkJson != null)
			wk = wkJson.toJavaList(NextWeeklyDto.class);

		for (int i = 0; i < wk.size(); i++) {
			wk.get(i).setNumber(i + 1);

		}
		map.put("wk", wk);

		// 项目总体情况描述
		WeeklyDetails wDetails = weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(id));
		// （实际项目发生工时/立项申请预估工时-1）%
		if (wDetails != null && wDetails.getIsSubmit() != null && wDetails.getIsSubmit() == 0) {
			wDetails.setIsSubmit(0);
		}
		map.put("wDetails", wDetails);

		return map;
	}

	@Override
	public Map<Object, Object> getDescByIdWeek(String id) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		// 项目周报
		ProjectWeeklyDto pd = getProjectWeeklyDtoById(Long.parseLong(id));
		pd.setId(id);
		map.put("pd", pd);

		Weekly wkly = weeklyMapper.selectByPrimaryKey(Long.parseLong(id));
		if (wkly.getProgress() != null) {
			pd.setCompletedProgressInt(Integer.parseInt(wkly.getProgress()));
		} else {
			pd.setCompletedProgressInt(0);
		}

		// 本周进展情况
		JSONArray tdJson = JSON.parseArray(wkly.getCurrentWeek());
		List<TaskDto> td = new ArrayList<TaskDto>();
		if (tdJson != null)
			td = tdJson.toJavaList(TaskDto.class);

		List<String> listVersion = new ArrayList<String>();

		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i + 1);
			listVersion.add(td.get(i).getVersionId());
		}

		HashSet setVersion = new HashSet();
		setVersion.addAll(listVersion);// 给set填充
		listVersion.clear();//
		listVersion.addAll(setVersion);// 把set的

		// 本周项目版本
		map.put("pv", StringUtils.join(listVersion.toArray(), ","));

		map.put("td", td);

		// 当前重要风险
		String weeklyRist = wkly.getRisk();
		if (weeklyRist != null) {
			JSONArray weeklyRistJson = JSON.parseArray(weeklyRist);
			map.put("wr", weeklyRistJson.toJavaList(WeeklyJson.class));
		} else
			map.put("wr", null);

		// 当前重要问题
		String weeklyQuestion = wkly.getQuestion();
		if (weeklyQuestion != null) {
			JSONArray weeklyQuestionJson = JSON.parseArray(weeklyQuestion);
			map.put("wq", weeklyQuestionJson);
		} else {
			map.put("wq", null);
		}

		/*
		 * weeklyPara.setId(Long.parseLong(pd.getProjectId())); WorkDay
		 * nextWeeklyWorkDays = workDayService.getNextWorkDay(wkly.getStartDate());
		 * weeklyPara.setStartDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkStartTime(
		 * ))+" 00:00:00");
		 * weeklyPara.setEndDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkStartTime())
		 * +" 23:59:59");
		 */
		// 下周工作计划
		JSONArray wkJson = JSON.parseArray(wkly.getNextWeek());
		List<NextWeeklyDto> wk = new ArrayList<NextWeeklyDto>();
		if (wkJson != null)
			wk = wkJson.toJavaList(NextWeeklyDto.class);

		for (int i = 0; i < wk.size(); i++) {
			wk.get(i).setNumber(i + 1);

		}
		map.put("wk", wk);

		// 项目总体情况描述
		WeeklyDetails wDetails = weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(id));
		// （实际项目发生工时/立项申请预估工时-1）%
		if (wDetails != null && wDetails.getIsSubmit() != null && wDetails.getIsSubmit() == 0) {
			wDetails.setIsSubmit(0);
		}
		map.put("wDetails", wDetails);

		return map;
	}

	@Override
	public Map<Object, Object> getDescAddById(String id) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		// 项目周报
		ProjectWeeklyDto pd = getProjectAddWeeklyDtoById(Long.parseLong(id));
		pd.setCompletedProgressInt(Integer.parseInt(pd.getCompletedProgress()));
		pd.setId(id);
		map.put("pd", pd);
		Weekly wkly = weeklyMapper.selectByPrimaryKey(Long.parseLong(id));
		/* String[] strDate=pd.getBeginAndEndTime().split("到"); */
		WeeklyPara weeklyPara = new WeeklyPara();
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		weeklyPara.setStartDate(DateUtil.getDateStr(wkly.getStartDate()));
		weeklyPara.setEndDate(DateUtil.getDateStr(wkly.getEndDate()));

		// 本周进展情况
		// List<TaskDto> td=selectWeeklyProgress(weeklyPara);
		JSONArray tdJson = JSON.parseArray(wkly.getCurrentWeek());
		List<TaskDto> td = new ArrayList<TaskDto>();
		if (tdJson != null)
			td = tdJson.toJavaList(TaskDto.class);

		List<String> listVersion = new ArrayList<String>();

		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i + 1);
			listVersion.add(td.get(i).getVersionId());
		}
		map.put("td", td);
		weeklyPara.setId(Long.parseLong(id));

		HashSet setVersion = new HashSet();
		setVersion.addAll(listVersion);// 给set填充

		listVersion.clear();//
		listVersion.addAll(setVersion);// 把set的

		// 本周项目版本
		// List<String> projectVersion=selectProjectVersion(listVersion);
		map.put("pv", StringUtils.join(listVersion.toArray(), ","));

		// 当前重要风险
		String weeklyRist = wkly.getRisk();
		if (weeklyRist != null) {
			JSONArray weeklyRistJson = JSON.parseArray(weeklyRist);
			map.put("wr", weeklyRistJson.toJavaList(WeeklyJson.class));
		} else
			map.put("wr", null);

		// 当前重要问题
		String weeklyQuestion = wkly.getQuestion();
		if (weeklyQuestion != null) {
			JSONArray weeklyQuestionJson = JSON.parseArray(weeklyQuestion);
			map.put("wq", weeklyQuestionJson);
		} else {
			map.put("wq", null);
		}

		weeklyPara.setId(Long.parseLong(pd.getProjectId()));

		WorkDay nextWeeklyWorkDays = workDayService.getNextWeeklyWorkDays();
		weeklyPara.setStartDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkStartTime()) + " 00:00:00");
		weeklyPara.setEndDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkEndTime()) + " 23:59:59");

		// 下周工作计划
		// List<NextWeeklyDto> wk=selectNextWeeklyProgress(weeklyPara);
		JSONArray nextWeeklyJson = JSON.parseArray(wkly.getNextWeek());
		List<NextWeeklyDto> wk = new ArrayList<NextWeeklyDto>();
		if (nextWeeklyJson != null && nextWeeklyJson.size() > 0)
			wk = nextWeeklyJson.toJavaList(NextWeeklyDto.class);

		for (int i = 0; i < wk.size(); i++) {
			wk.get(i).setNumber(i + 1);
		}
		map.put("wk", wk);

		// 项目总体情况描述
		WeeklyDetails wDetails = weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(id));
		// （实际项目发生工时/立项申请预估工时-1）%
		if (wDetails != null && wDetails.getIsSubmit() != null && wDetails.getIsSubmit() == 0) {
			wDetails.setIsSubmit(0);
		}

		map.put("wDetails", wDetails);

		return map;
	}

	@Override
	public EasyuiPageOutput getListPage(WeeklyPara weeklyPara) {

		// 参数转换 项目类型
		if (weeklyPara.getProjectType() != null && !weeklyPara.getProjectType().endsWith("-1")) {
			DataDictionaryValue ddv = DTOUtils.newDTO(DataDictionaryValue.class);
			ddv.setId(Long.parseLong(weeklyPara.getProjectType()));// 项目类型
			weeklyPara.setProjectType(dataDictionaryValueService.listDataDictionaryValue(ddv).get(0).getCode());
		} else {
			weeklyPara.setProjectType(null);
		}
		// 参数转换 项目状态
		if (weeklyPara.getProjectStatus() != null && !weeklyPara.getProjectStatus().endsWith("-1")) {
			DataDictionaryValue ddv = DTOUtils.newDTO(DataDictionaryValue.class);
			ddv.setId(Long.parseLong(weeklyPara.getProjectStatus()));// 项目类型
			weeklyPara.setProjectStatus(dataDictionaryValueService.listDataDictionaryValue(ddv).get(0).getCode());
		} else {
			weeklyPara.setProjectStatus(null);
		}

		// 参数转换 项目状态
		if (weeklyPara.getUserName() != null && weeklyPara.getUserName().endsWith("-1")) {
			weeklyPara.setUserName(null);
		}

		// 查询总页数
		int total = weeklyMapper.selectPageByWeeklyParaCount(weeklyPara);
		// 查询list
		Page page = new Page();
		page.setPageIndex(weeklyPara.getPage());
		page.setPageSize(weeklyPara.getRows());
		List<WeeklyPara> list = weeklyMapper.selectListPageByWeeklyPara(weeklyPara, page);

		DataDictionary ddit = DTOUtils.newDTO(DataDictionary.class);
		ddit.setName(PROJECTTYPE);
		ddit.setCode(PROJECTTYPECODE);
		List<DataDictionary> dditList = dataDictionaryService.listDataDictionary(ddit);

		DataDictionaryValue ddv;
		User user = null;
		if (list != null && list.size() > 0) {
			for (WeeklyPara weeklyPara2 : list) {

				// 项目负责人
				user = DTOUtils.newDTO(User.class);
				user.setFirmCode(AlmConstants.ALM_FIRM_CODE);
				user.setId(Long.parseLong(weeklyPara2.getUserName()));
				BaseOutput<List<User>> listByExample = userRpc.listByExample(user);
				List<User> listUserParty = listByExample.getData();
				if (listUserParty != null && listUserParty.size() > 0) {
					weeklyPara2.setUserName(listUserParty.get(0).getRealName());
				}

				weeklyPara2.setDate(weeklyPara2.getStartDate().substring(0, 10) + " 到 " + weeklyPara2.getEndDate().substring(0, 10));

				ddv = DTOUtils.newDTO(DataDictionaryValue.class);
				
				ddv.setValue(weeklyPara2.getProjectType());
				ddv.setDdId(dditList.get(0).getId());
				weeklyPara2.setProjectType(dataDictionaryValueService.listDataDictionaryValue(ddv).get(0).getCode());
				weeklyPara2.setCreated(weeklyPara2.getCreated().substring(0, 19));
			}
		}

		EasyuiPageOutput out = new EasyuiPageOutput(total, list);
		return out;
	}

	@Override
	public ProjectWeeklyDto getProjectWeeklyDtoById(Long id) {

		ProjectWeeklyDto pd = weeklyMapper.selectProjectWeeklyDto(id);
		if (pd != null && pd.getPlanDate() != null)
			pd.setPlanDate(pd.getPlanDate().substring(0, 10));

		pd.setBeginAndEndTime(pd.getStartDate().substring(0, 10) + "到" + pd.getEndDate().substring(0, 10));

		User user = DTOUtils.newDTO(User.class);
		user.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		user.setId(Long.parseLong(pd.getBusinessParty()));
		// 业务方
		BaseOutput<List<User>> listByExample = userRpc.listByExample(user);
		List<User> listUserParty = listByExample.getData();
		if (listUserParty != null && listUserParty.size() > 0)
			pd.setBusinessParty(listUserParty.get(0).getRealName());

		// 查询项目经理
		user.setId(Long.parseLong(pd.getUserName()));
		BaseOutput<List<User>> listUserByExample = userRpc.listByExample(user);
		List<User> listUsernName = listUserByExample.getData();
		if (listUsernName != null && listUsernName.size() > 0)
			pd.setUserName(listUsernName.get(0).getRealName());

		// 项目所在部门
		Department department = DTOUtils.newDTO(Department.class);
		department.setId(Long.parseLong(pd.getProjectInDept()));
		department.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<Department>> departmentByExample = departmentRpc.listByDepartment(department);
		List<Department> departmentList = departmentByExample.getData();
		if (departmentList != null && departmentList.size() > 0)
			pd.setProjectInDept(departmentList.get(0).getName());

		// 查询项目类型
		DataDictionary ddit = DTOUtils.newDTO(DataDictionary.class);
		ddit.setName(PROJECTTYPE);
		ddit.setCode(PROJECTTYPECODE);
		List<DataDictionary> dditList = dataDictionaryService.listDataDictionary(ddit);
		// List<DataDictionary> dditList=dictionaryMapper.selectByExample(ddit);//查询出id

		DataDictionaryValue ddv = DTOUtils.newDTO(DataDictionaryValue.class);
		ddv.setValue(pd.getProjectType());
		ddv.setDdId(dditList.get(0).getId());
		pd.setProjectType(dataDictionaryValueService.listDataDictionaryValue(ddv).get(0).getCode());
		pd.setCompletedProgressInt(Integer.parseInt(pd.getCompletedProgress()));
		return pd;
	}

	@Override
	public ProjectWeeklyDto getProjectAddWeeklyDtoById(Long id) {

		ProjectWeeklyDto pd = weeklyMapper.selectProjectWeeklyDto(id);
		if (pd != null && pd.getPlanDate() != null)
			pd.setPlanDate(pd.getPlanDate().substring(0, 10));

		pd.setBeginAndEndTime(pd.getStartDate().substring(0, 10) + "到" + pd.getEndDate().substring(0, 10));

		User user = DTOUtils.newDTO(User.class);
		user.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		user.setId(Long.parseLong(pd.getBusinessParty()));
		// 业务方
		BaseOutput<List<User>> listByExample = userRpc.listByExample(user);
		List<User> listUserParty = listByExample.getData();
		if (listUserParty != null && listUserParty.size() > 0)
			pd.setBusinessParty(listUserParty.get(0).getRealName());

		// 查询项目经理
		user.setId(Long.parseLong(pd.getUserName()));
		BaseOutput<List<User>> listUserByExample = userRpc.listByExample(user);
		List<User> listUsernName = listUserByExample.getData();
		if (listUsernName != null && listUsernName.size() > 0)
			pd.setUserName(listUsernName.get(0).getRealName());

		// 项目所在部门
		Department department = DTOUtils.newDTO(Department.class);
		department.setId(Long.parseLong(pd.getProjectInDept()));
		department.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<Department>> departmentByExample = departmentRpc.listByDepartment(department);
		List<Department> departmentList = departmentByExample.getData();
		if (departmentList != null && departmentList.size() > 0)
			pd.setProjectInDept(departmentList.get(0).getName());

		// 查询项目类型
		DataDictionary ddit = DTOUtils.newDTO(DataDictionary.class);
		ddit.setName(PROJECTTYPE);
		ddit.setCode(PROJECTTYPECODE);
		List<DataDictionary> dditList = dataDictionaryService.listDataDictionary(ddit);

		DataDictionaryValue ddv = DTOUtils.newDTO(DataDictionaryValue.class);
		ddv.setValue(pd.getProjectType());
		ddv.setDdId(dditList.get(0).getId());
		pd.setProjectType(dataDictionaryValueService.listDataDictionaryValue(ddv).get(0).getCode());
		//
		pd.setCompletedProgressInt(Integer.parseInt(pd.getCompletedProgress()));
		return pd;
	}

	/**
	 * 当前重大问题 -
	 */
	@Override
	public String selectWeeklyQuestion(WeeklyPara weeklyPara) {
		return weeklyMapper.selectWeeklyQuestion(weeklyPara);
	}

	/**
	 * 当前重要风险
	 */
	@Override
	public String selectWeeklyRist(WeeklyPara weeklyPara) {
		return weeklyMapper.selectWeeklyRist(weeklyPara);
	}

	/**
	 * 本周进展情况
	 */
	@Override
	public List<TaskDto> selectWeeklyProgress(WeeklyPara weeklyPara) {

		List<TaskDto> td = weeklyMapper.selectWeeklyProgress(weeklyPara);

		DataDictionary ddit = DTOUtils.newDTO(DataDictionary.class);
		ddit.setName(PROJECTSTATUS);
		User user;
		WeeklyPara weekPara;
		if (td.size() == 0) {
			return td;
		}
		for (int i = 0; i < td.size(); i++) {

			// 责任人
			user = DTOUtils.newDTO(User.class);
			user.setFirmCode(AlmConstants.ALM_FIRM_CODE);
			user.setId(Long.parseLong(td.get(i).getOwner()));
			BaseOutput<List<User>> listByExample = userRpc.listByExample(user);
			List<User> listUserParty = listByExample.getData();
			if (listUserParty != null && listUserParty.size() > 0)
				td.get(i).setOwner(listUserParty.get(0).getRealName());

			// 版本
			td.get(i).setVersionId(projectVersionMapper.selectByPrimaryKey(Long.parseLong(td.get(i).getVersionId())).getVersion());
			// 预计结束时间
			String endtime = DateUtil.getDateStr(td.get(i).getEndDate()).substring(0, 10).toString();
			td.get(i).setEndDateStr(endtime);
			// 设置加班时间 和 正常工时
			weekPara = new WeeklyPara();
			weekPara.setId(td.get(i).getId());
			weekPara.setStartDate(weeklyPara.getStartDate());
			weekPara.setEndDate(weeklyPara.getEndDate());
			TaskDto task = weeklyMapper.selectWeeklyOverAndtaskHour(weekPara);
			if (task != null) {
				td.get(i).setTaskHour(task.getTaskHour());
				td.get(i).setOverHour(task.getOverHour());
				td.get(i).setWeekHour(task.getTaskHour() + task.getOverHour() + "");// 本周工时
			} else {
				td.get(i).setTaskHour(0);
				td.get(i).setOverHour(0);
				td.get(i).setWeekHour(String.valueOf(0));
			}
			NextWeeklyDto realHourTotal = weeklyMapper.selectNextWeeklyTaskHour(td.get(i).getId());// 算出总加班时间和总正常时间

			if (realHourTotal != null) {
				// 实际总工时 =正常工时+加班工时 -------------只是实际总工时
				int overHour = 0;
				int taskHour = 0;
				if (realHourTotal.getOverHour() != null) {
					overHour = Integer.parseInt(realHourTotal.getOverHour());
				}
				if (realHourTotal.getTaskHour() != null) {
					taskHour = Integer.parseInt(realHourTotal.getTaskHour());
				}

				td.get(i).setRealHour(overHour + taskHour + "");
			} else {
				td.get(i).setRealHour("0");
			}

			// 工时偏差% （实际任务工时/预估任务工时-1）% ***也是算加班工时的
			double realHourandOverHour = td.get(i).getOverHour() + td.get(i).getTaskHour();// 实际工时
			double planTime = td.get(i).getPlanTime();// 计划工时

			if (td.get(i).getStatus().equals(PROJECTTASKSTATUS)) {// 已完成就是等于
				td.get(i).setStatus(YES);
				double pro = (realHourandOverHour / (planTime) - 1) * 100;
				td.get(i).setHourDeviation(df.format(pro));
			} else {
				td.get(i).setStatus(NO);
				double pro = ((planTime - realHourandOverHour) / planTime) * 100;
				td.get(i).setHourDeviation(df.format(Math.abs(pro)));
			}

			if (td.get(i).getFackEndDate() != null) {
				td.get(i).setFackEndDate(td.get(i).getFackEndDate().substring(0, 10));
			} else {
				td.get(i).setFackEndDate(null);
			}

		}

		return td;
	}

	/**
	 * 下周工作计划
	 */
	@Override
	public List<NextWeeklyDto> selectNextWeeklyProgress(WeeklyPara weeklyPara) {
		List<NextWeeklyDto> nwd = weeklyMapper.selectNextWeeklyProgress(weeklyPara);
		User user;
		NextWeeklyDto nd = null;
		for (NextWeeklyDto nextWeeklyDto : nwd) {

			if (nextWeeklyDto.getEndDate() != null) {
				nextWeeklyDto.setEndDate(nextWeeklyDto.getEndDate().substring(0, 10));// 时间截取
			}

			nd = null;
			nd = weeklyMapper.selectNextWeeklyTaskHour(nextWeeklyDto.getId());// 算出正常工时和加班工时
			// 计划工时-正常工时-加班工时=剩余工时
			int planTime = 0;
			int taskHour = 0;
			int overHour = 0;
			if (nextWeeklyDto.getPlanTime() != null) {
				planTime = Integer.parseInt(nextWeeklyDto.getPlanTime());
			}
			if (nd != null && nd.getTaskHour() != null) {
				taskHour = Integer.parseInt(nd.getTaskHour());
			}
			if (nd != null && nd.getOverHour() != null) {
				overHour = Integer.parseInt(nd.getOverHour());
			}

			int surplus = planTime - overHour - taskHour;
			// int
			// surplus=Integer.parseInt(nextWeeklyDto.getPlanTime())-Integer.parseInt(nd.getTaskHour())-Integer.parseInt(nd.getOverHour());
			nextWeeklyDto.setSurplus(surplus + "");// 剩余工时
			user =DTOUtils.newDTO(User.class);// 字典接口
			user.setFirmCode(AlmConstants.ALM_FIRM_CODE);
			user.setId(Long.parseLong(nextWeeklyDto.getOwner()));
			BaseOutput<List<User>> listByExample = userRpc.listByExample(user);
			List<User> listUserParty = listByExample.getData();
			if (listUserParty != null && listUserParty.size() > 0)
				nextWeeklyDto.setOwner(listUserParty.get(0).getRealName());

		}

		return nwd;
	}

	/**
	 * 本周项目版本
	 */
	@Override
	public List<String> selectProjectVersion(List list) {
		return weeklyMapper.selectProjectVersion(list);
	}

	@Override
	public Integer updateMaxQuestion(String question, Long id) {
		WeeklyPara weeklyPara = new WeeklyPara();
		weeklyPara.setId(id);
		weeklyPara.setQuestion(question);
		return weeklyMapper.updateRiskOrByQuestion(weeklyPara);
	}

	@Override
	public Integer updateMaxRist(String risk, Long id) {
		// JSONArray weeklyQuestionJson=JSON.parseArray(question);
		// List<WeeklyJson> list=(List)
		// weeklyQuestionJson.toJavaList(WeeklyJson.class);

		WeeklyPara weeklyPara = new WeeklyPara();
		weeklyPara.setId(id);
		weeklyPara.setRisk(risk);

		return weeklyMapper.updateRiskOrByQuestion(weeklyPara);
	}

	@Override
	public File downLoad(File file, String id) {
		// 项目周报
		ProjectWeeklyDto pd = getProjectWeeklyDtoById(Long.parseLong(id));
		pd.setId(id);
		/* WeeklyPara weeklyPara = new WeeklyPara(); */
		Weekly wkly = weeklyMapper.selectByPrimaryKey(Long.parseLong(id));

		pd.setCompletedProgress(wkly.getProgress());
		// 本周进展情况
		JSONArray tdJson = JSON.parseArray(wkly.getCurrentWeek());

		List<TaskDto> td = new ArrayList<TaskDto>();
		if (tdJson != null)
			td = tdJson.toJavaList(TaskDto.class);
		List<String> listVersion = new ArrayList<String>();

		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i + 1);
			listVersion.add(td.get(i).getVersionId());
		}

		HashSet setVersion = new HashSet();
		setVersion.addAll(listVersion);// 给set填充

		listVersion.clear();//
		listVersion.addAll(setVersion);// 把set的

		// 本周项目版本
		List<String> projectVersion = listVersion;

		// 当前重要风险
		String weeklyRist = wkly.getRisk();
		JSONArray weeklyRistJson = JSON.parseArray(weeklyRist);
		// 当前重要风险
		String weeklyQuestion = wkly.getQuestion();
		JSONArray weeklyQuestionJson = JSON.parseArray(weeklyQuestion);

		// 下周工作计划
		JSONArray wkJson = JSON.parseArray(wkly.getNextWeek());
		List<NextWeeklyDto> wk = new ArrayList<NextWeeklyDto>();

		if (wkJson != null)
			wk = wkJson.toJavaList(NextWeeklyDto.class);

		for (int i = 0; i < wk.size(); i++) {
			wk.get(i).setNumber(i + 1);

		}

		// 项目总体情况描述
		WeeklyDetails wDetails = weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(id));

		try {
			file = WordExport.getFileExecel(file, pd, projectVersion, td, wk, weeklyRistJson, weeklyQuestionJson, wDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;

	}

	/**
	 * 根据任务表中个的项目经理id 查询项目经理
	 * 
	 * @return
	 */
	@Override
	public List<WeeklyPara> getUser() {
		User user;
		WeeklyPara Wp = new WeeklyPara();
		Wp.setUserName("项目经理选择");
		Wp.setId(Long.parseLong("-1"));
		List<WeeklyPara> userList = weeklyMapper.getUser();

		for (int j = 0; j < userList.size(); j++) {
			user = DTOUtils.newDTO(User.class);
			user.setFirmCode(AlmConstants.ALM_FIRM_CODE);
			user.setId(Long.parseLong(userList.get(j).getUserName()));
			BaseOutput<List<User>> listByExample = userRpc.listByExample(user);
			List<User> listUserParty = listByExample.getData();
			userList.get(j).setUserName(listUserParty.get(0).getRealName());
			userList.get(j).setId(listUserParty.get(0).getId());
		}

		userList.add(0, Wp);

		return userList;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public Map<String, Weekly> insertWeeklyByprojectId(String projectId) {

		Map<String, Weekly> map = new HashMap<String, Weekly>();

		Weekly wk = DTOUtils.newDTO(Weekly.class);
		WeeklyPara weeklyPara = new WeeklyPara();
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		WorkDay workDay = workDayService.getNowWeeklyWorkDay();
		weeklyPara.setStartDate(DateUtil.getDate(workDay.getWorkStartTime()) + " 00:00:00");
		weeklyPara.setEndDate(DateUtil.getDate(workDay.getWorkEndTime()) + " 23:59:59");

		if (userTicket != null) {
			weeklyPara.setUserId(userTicket.getId());
		}
		weeklyPara.setProjectId(Long.parseLong(projectId));

		wk = weeklyMapper.selecByProjectId(weeklyPara);

		Weekly wkk = DTOUtils.newDTO(Weekly.class);
		if (wk == null) {
			insertWeekAndWeekDetail(projectId, userTicket, wkk);
			map.put("one", wkk);

		} else {

			WeeklyDetails weeklyDetails = DTOUtils.newDTO(WeeklyDetails.class);
			weeklyDetails.setWeeklyId(wk.getId());

			weeklyDetails = weeklyDetailsMapper.selectOne(weeklyDetails);
			if (weeklyDetails == null || weeklyDetails.getIsSubmit() != ISSUBMIT) {

				Weekly weekly = weeklyMapper.selectByPrimaryKey(wk.getId());

				wkk = updateWeekAndWeekDetail(projectId, userTicket, weekly);

				map.put("two", wkk);
			} else
				map.put("two", wk);

		}

		return map;
	}

	@Override
	public Weekly insertWeekAndWeekDetail(String projectId, UserTicket userTicket, Weekly wkk) {
		WorkDay workDay = workDayService.getNowWeeklyWorkDay();
		WorkDay nextWeeklyWorkDays = workDayService.getNextWeeklyWorkDays();
		wkk.setProjectId(Long.parseLong(projectId));
		wkk.setCreated(new Date());
		wkk.setModified(null);
		wkk.setStartDate(DateUtil.getStrDate(DateUtil.getDate(workDay.getWorkStartTime()) + " 00:00:00"));
		wkk.setEndDate(DateUtil.getStrDate(DateUtil.getDate(workDay.getWorkEndTime()) + " 23:59:59"));

		if (userTicket != null) {
			wkk.setCreateMemberId(userTicket.getId());
			wkk.setModifyMemberId(userTicket.getId());
		}

		weeklyMapper.insertSelective(wkk);
		ProjectWeeklyDto pd = getProjectWeeklyDtoById(wkk.getId());

		WeeklyPara weeklyParaWeek = new WeeklyPara();
		weeklyParaWeek.setId(Long.parseLong(projectId));
		weeklyParaWeek.setStartDate(DateUtil.getDate(workDay.getWorkStartTime()) + " 00:00:00");
		weeklyParaWeek.setEndDate(DateUtil.getDate(workDay.getWorkEndTime()) + " 23:59:59");
		// 本周进展情况
		List<TaskDto> td = selectWeeklyProgress(weeklyParaWeek);

		weeklyParaWeek.setStartDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkStartTime()) + " 00:00:00");
		weeklyParaWeek.setEndDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkEndTime()) + " 23:59:59");
		// 下周工作计划
		List<NextWeeklyDto> wek = selectNextWeeklyProgress(weeklyParaWeek);

		Weekly nextAndcurrentWeek = DTOUtils.newDTO(Weekly.class);
		nextAndcurrentWeek.setId(wkk.getId());
		nextAndcurrentWeek.setCurrentWeek(JSON.toJSONString(td));
		nextAndcurrentWeek.setNextWeek(JSON.toJSONString(wek));
		// completedProgress
		nextAndcurrentWeek.setProgress(pd.getCompletedProgress());
		weeklyMapper.updateByPrimaryKeySelective(nextAndcurrentWeek);// 更新week本周周报

		return wkk;
	}

	public Weekly updateWeekAndWeekDetail(String projectId, UserTicket userTicket, Weekly wkk) {
		wkk.setProjectId(Long.parseLong(projectId));
		wkk.setCreated(new Date());
		wkk.setModified(null);
		WorkDay workDay = workDayService.getNowWeeklyWorkDay();
		wkk.setStartDate(DateUtil.getStrDate(DateUtil.getDate(workDay.getWorkStartTime()) + " 00:00:00"));
		wkk.setEndDate(DateUtil.getStrDate(DateUtil.getDate(workDay.getWorkStartTime()) + " 23:59:59"));

		if (userTicket != null) {
			wkk.setModifyMemberId(userTicket.getId());
		}
		WeeklyPara weeklyParaWeek = new WeeklyPara();
		weeklyParaWeek.setId(Long.parseLong(projectId));
		weeklyParaWeek.setStartDate(DateUtil.getDate(workDay.getWorkStartTime()) + " 00:00:00");
		weeklyParaWeek.setEndDate(DateUtil.getDate(workDay.getWorkEndTime()) + " 23:59:59");
		// 本周进展情况
		List<TaskDto> td = selectWeeklyProgress(weeklyParaWeek);
		WorkDay nextWeeklyWorkDays = workDayService.getNextWeeklyWorkDays();

		weeklyParaWeek.setStartDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkStartTime()) + " 00:00:00");
		weeklyParaWeek.setEndDate(DateUtil.getDate(nextWeeklyWorkDays.getWorkEndTime()) + " 23:59:59");
		// 下周工作计划
		List<NextWeeklyDto> wek = selectNextWeeklyProgress(weeklyParaWeek);

		Weekly nextAndcurrentWeek = DTOUtils.newDTO(Weekly.class);
		nextAndcurrentWeek.setId(wkk.getId());
		nextAndcurrentWeek.setCurrentWeek(JSON.toJSONString(td));
		nextAndcurrentWeek.setNextWeek(JSON.toJSONString(wek));
		ProjectWeeklyDto pd = getProjectWeeklyDtoById(wkk.getId());
		nextAndcurrentWeek.setProgress(pd.getCompletedProgress());
		weeklyMapper.updateByPrimaryKeySelective(nextAndcurrentWeek);// 更新week本周周报

		return wkk;
	}

	@Override
	public Weekly getWeeklyById(Long weeklyId) {
		Weekly weekly = weeklyMapper.selectByPrimaryKey(weeklyId);
		return weekly;
	}

	/**
	 * 定时刷过期任务
	 */
	@Override
	@Transactional
	public void submitWeekly() {
		Date as = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
		WorkDay workDayNowDate = workDayMapper.getWorkDayNowDate(DateUtil.getDate(as));
		WorkDay nextWeeklyWorkDays = workDayService.getNextWorkDay(as);
		if (workDayNowDate == null) {
			return;
		}
		if (!DateUtil.getDate(as).equals(DateUtil.getDate(workDayNowDate.getWorkEndTime()))) {
			return;
		}
		String starThisTimeStr = DateUtil.getDate(workDayNowDate.getWorkStartTime()) + " 00:00:00";
		String endThisTimeStr = DateUtil.getDate(workDayNowDate.getWorkEndTime()) + " 23:59:59";
		String endNextTimeStr = DateUtil.getDate(nextWeeklyWorkDays.getWorkEndTime()) + " 23:59:59";
		String starNextTimeStr = DateUtil.getDate(nextWeeklyWorkDays.getWorkStartTime()) + " 00:00:00";
		List<Long> projectIds = projectMapper.selectNotSubmitWeekly(starThisTimeStr, endThisTimeStr);
		Weekly weekly = DTOUtils.newDTO(Weekly.class);
		weekly.setStartDate(DateUtil.getStrDate(starThisTimeStr));
		weekly.setEndDate(DateUtil.getStrDate(endThisTimeStr));
		List<Weekly> weeklyList = this.weeklyMapper.select(weekly);
		List<Long> weeklyProjectId = new ArrayList<Long>();
		for (Weekly weekly2 : weeklyList) {
			WeeklyDetails weeklyDetails = DTOUtils.newDTO(WeeklyDetails.class);
			weeklyDetails.setWeeklyId(weekly2.getId());
			WeeklyDetails selectOne = this.weeklyDetailsMapper.selectOne(weeklyDetails);
			if (selectOne != null) {
				if (selectOne.getIsSubmit() == 0) {
					selectOne.setIsSubmit(1);
					this.weeklyDetailsMapper.updateByPrimaryKeySelective(selectOne);
				}
			} else {

				WeeklyPara weeklyParaWeek = new WeeklyPara();
				weeklyParaWeek.setId(weekly2.getProjectId());
				weeklyParaWeek.setStartDate(starThisTimeStr);
				weeklyParaWeek.setEndDate(endThisTimeStr);
				// 本周进展情况
				List<TaskDto> td = selectWeeklyProgress(weeklyParaWeek);

				weeklyParaWeek.setStartDate(starNextTimeStr);
				weeklyParaWeek.setEndDate(endNextTimeStr);
				// 下周工作计划
				List<NextWeeklyDto> wek = selectNextWeeklyProgress(weeklyParaWeek);

				Weekly nextAndcurrentWeek = DTOUtils.newDTO(Weekly.class);
				nextAndcurrentWeek.setId(weekly2.getId());
				nextAndcurrentWeek.setCurrentWeek(JSON.toJSONString(td));
				nextAndcurrentWeek.setNextWeek(JSON.toJSONString(wek));
				ProjectWeeklyDto pd = getProjectWeeklyDtoById(weekly2.getId());
				nextAndcurrentWeek.setProgress(pd.getCompletedProgress());
				weeklyMapper.updateByPrimaryKeySelective(nextAndcurrentWeek);// 更新week本周周报
				weeklyDetails.setIsSubmit(1);
				weeklyDetailsMapper.createInsert(weeklyDetails);
			}
			weeklyProjectId.add(weekly2.getProjectId());
		}
		projectIds.removeAll(weeklyProjectId);
		for (Long projectId : projectIds) {
			Weekly newWeekly = DTOUtils.newDTO(Weekly.class);
			WeeklyDetails newWeeklyDetails = DTOUtils.newDTO(WeeklyDetails.class);

			WeeklyPara weeklyParaWeek = new WeeklyPara();
			weeklyParaWeek.setId(projectId);
			weeklyParaWeek.setStartDate(starThisTimeStr);
			weeklyParaWeek.setEndDate(endThisTimeStr);
			// 本周进展情况

			List<TaskDto> td = selectWeeklyProgress(weeklyParaWeek);

			weeklyParaWeek.setStartDate(starNextTimeStr);
			weeklyParaWeek.setEndDate(endNextTimeStr);
			// 下周工作计划
			List<NextWeeklyDto> wek = selectNextWeeklyProgress(weeklyParaWeek);

			newWeekly.setStartDate(DateUtil.getStrDate(starThisTimeStr));
			newWeekly.setEndDate(DateUtil.getStrDate(endThisTimeStr));
			newWeekly.setProjectId(projectId);
			newWeekly.setRisk("[]");
			newWeekly.setQuestion("[]");
			newWeekly.setCurrentWeek(JSON.toJSONString(td));
			newWeekly.setNextWeek(JSON.toJSONString(wek));
			newWeekly.setCreated(new Date());
			newWeekly.setModified(new Date());
			// 获取项目经理
			Team team = DTOUtils.newDTO(Team.class);
			team.setProjectId(projectId);
			team.setRole(WEEKLY_SUBIMT_ROLE);
			Team projectManager = teamMapper.selectOne(team);

			newWeekly.setCreateMemberId(projectManager.getMemberId());
			newWeekly.setModifyMemberId(projectManager.getMemberId());

			Project selectByPrimaryKey = projectMapper.selectByPrimaryKey(projectId);
			newWeekly.setProgress(selectByPrimaryKey.getCompletedProgress().toString());
			this.weeklyMapper.insert(newWeekly);
			newWeeklyDetails.setWeeklyId(newWeekly.getId());
			newWeeklyDetails.setIsSubmit(1);
			this.weeklyDetailsMapper.insert(newWeeklyDetails);
		}

	}

}
