package com.dili.alm.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.WeeklyMapper;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.User;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WeeklyEntity;
import com.dili.alm.domain.WeeklyJson;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.WeeklyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

/**
 * ��MyBatis Generator�����Զ����� This file was generated on 2017-11-29
 * 14:08:40.
 */
@Service
public class WeeklyServiceImpl extends BaseServiceImpl<Weekly, Long> implements
		WeeklyService {

	@Autowired
	WeeklyMapper weeklyMapper;
	@Autowired
	ProjectMapper projectMapper;
	@Autowired
	ProjectVersionMapper projectVersionMapper;
	@Autowired
	ProjectPhaseMapper projectPhaseMapper;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private DepartmentRpc departmentRpc;

	public WeeklyMapper getActualDao() {
		return (WeeklyMapper) getDao();
	}

	@Override
	public EasyuiPageOutput getListPage(WeeklyPara weeklyPara) {

		// 查询总页数
		int total = weeklyMapper.selectPageByWeeklyParaCount(weeklyPara);
		// 查询list
		CopyOnWriteArrayList<WeeklyPara> list = weeklyMapper
				.selectListPageByWeeklyPara(weeklyPara);
		CopyOnWriteArrayList<WeeklyPara> copyList = new CopyOnWriteArrayList();
		if (list != null && list.size() > 0) {
			for (WeeklyPara weeklyPara2 : list) {

				weeklyPara2.setDate(weeklyPara2.getStartDate() + " 到 "
						+ weeklyPara2.getEndDate());
				if (weeklyPara2.getProjectType().equalsIgnoreCase("I")) {
					weeklyPara2.setProjectType("内部项目");
				} else if (weeklyPara2.getProjectType().equalsIgnoreCase("K")) {
					weeklyPara2.setProjectType("重点项目");
				} else if (weeklyPara2.getProjectType().equalsIgnoreCase("R")) {
					weeklyPara2.setProjectType("预约项目");
				} else if (weeklyPara2.getProjectType().equalsIgnoreCase("G")) {
					weeklyPara2.setProjectType("一般项目");
				}
				copyList.add(weeklyPara2);
			}
		}

		EasyuiPageOutput out = new EasyuiPageOutput(total, list);
		return out;
	}

	@Override
	public ProjectWeeklyDto getProjectWeeklyDtoById(Long projectId) {
		ProjectWeeklyDto pd = weeklyMapper.selectProjectWeeklyDto(projectId);
		if (pd != null && pd.getPlanDate() != null)
			pd.setPlanDate(pd.getPlanDate().substring(0, 10));
		pd.setBeginAndEndTime(getWeekFristDay() + "到" + getWeekFriday());
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket != null) {
			pd.setStageMan(userTicket.getUserName());
		}

		User user = new User();
		user.setId(Long.parseLong(pd.getUserName()));
		// 业务方

		/*List<User> listUserParty = (List<User>) userRpc.listByExample(user);
		pd.setBusinessParty(listUserParty.get(0).getUserName());*/

		// 查询项目经理
		/*List<User> listUser = (List<User>) userRpc.listByExample(user);
		pd.setUserName(listUser.get(0).getUserName());*/
		
		// 项目所在部门
		/*Department  department=DTOUtils.newDTO(Department.class);
		department.setId(Long.parseLong(pd.getProjectInDept()));
		departmentRpc.list(department);*/
		// 查询项目类型
		if (pd.getProjectType().equalsIgnoreCase("I")) {
			pd.setProjectType("内部项目");
		} else if (pd.getProjectType().equalsIgnoreCase("K")) {
			pd.setProjectType("重点项目");
		} else if (pd.getProjectType().equalsIgnoreCase("R")) {
			pd.setProjectType("预约项目");
		} else if (pd.getProjectType().equalsIgnoreCase("G")) {
			pd.setProjectType("一般项目");
		}

		return pd;
	}

	/**
	 * 当前重大问题 -
	 * */
	@Override
	public String selectWeeklyQuestion(String id) {
		return weeklyMapper.selectWeeklyQuestion(id);
	}

	/**
	 * 当前重要风险
	 * */
	@Override
	public String selectWeeklyRist(String id) {
		return weeklyMapper.selectWeeklyRist(id);
	}

	/**
	 * 本周进展情况
	 * */
	@Override
	public List<TaskDto> selectWeeklyProgress(Long id) {

		List<TaskDto> td = weeklyMapper.selectWeeklyProgress(id);

		for (int i = 0; i < td.size(); i++) {

			// 责任人

			// 版本
			td.get(i).setVersionId(
					projectVersionMapper.selectByPrimaryKey(
							Long.parseLong(td.get(i).getVersionId()))
							.getVersion());
			// 阶段
			td.get(i).setPhaseId(
					projectPhaseMapper.selectByPrimaryKey(
							Long.parseLong(td.get(i).getPhaseId())).getName());
			// 本周工时
			td.get(i).setWeekHour(
					getDatePoor(td.get(i).getStartDate(), td.get(i)
							.getEndDate()));
			// 实际工时
			td.get(i).setRealHour(
					td.get(i).getOverHour() + td.get(i).getTaskHour() + "");
			// 工时偏差% （100-实际/预计）%
			int pro = (100 - (td.get(i).getOverHour() + td.get(i).getTaskHour())
					/ td.get(i).getPlanTime()) * 100;
			td.get(i).setHourDeviation(pro + "");
			// 完成情况
			td.get(i).setStatus(
					getType(Integer.parseInt(td.get(i).getStatus())));
			;

		}
		return td;
	}

	/**
	 * 下周工作计划
	 * */
	@Override
	public List<NextWeeklyDto> selectNextWeeklyProgress(Long id) {
		List<NextWeeklyDto> nwd = weeklyMapper.selectNextWeeklyProgress(id);
		// 责任人
		return nwd;
	}

	/**
	 * 下周项目阶段
	 * */
	@Override
	public List<String> selectNextProjectPhase(Long id) {
		return weeklyMapper.selectNextProjectPhase(id);
	}

	/**
	 * 本周项目阶段
	 * */
	@Override
	public List<String> selectProjectPhase(Long id) {
		return weeklyMapper.selectProjectPhase(id);
	}

	/**
	 * 本周项目版本
	 * */
	@Override
	public List<String> selectProjectVersion(Long id) {
		return weeklyMapper.selectProjectVersion(id);
	}

	public static String getDatePoor(Date endDate, Date nowDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		// long hour = diff % nd / nh;
		// 计算差多少分钟
		// long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return day + "";
	}

	public String getType(int status) {
		String statusToStr = "";
		if (status == 0) {
			statusToStr = "未开始";
		} else if (status == 1) {
			statusToStr = "已开始";
		} else if (status == 2) {
			statusToStr = "暂停";
		} else if (status == 3) {
			statusToStr = "已完成";
		}
		return statusToStr;
	}

	public static String getWeekFristDay() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
		return df.format(cal.getTime());
	}

	public static String getWeekFriday() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 4);
		Date last = cal.getTime();
		return formater.format(last);
	}

	@Override
	public Integer updateMaxQuestion(String question, Long id) {

		// JSONArray weeklyQuestionJson=JSON.parseArray(question);
		// List<WeeklyJson> list=(List)
		// weeklyQuestionJson.toJavaList(WeeklyJson.class);
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

}