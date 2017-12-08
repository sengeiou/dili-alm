package com.dili.alm.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dili.alm.dao.DataDictionaryMapper;
import com.dili.alm.dao.DataDictionaryValueMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.WeeklyMapper;
import com.dili.alm.domain.DataDictionary;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.User;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WeeklyDetails;
import com.dili.alm.domain.WeeklyJson;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.DataDictionaryValueService;
import com.dili.alm.service.WeeklyDetailsService;
import com.dili.alm.service.WeeklyService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WordExport;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

/**
 * ��MyBatis Generator�����Զ����� This file was generated on 2017-11-29
 * 14:08:40.
 */
@Service
public class WeeklyServiceImpl extends BaseServiceImpl<Weekly, Long> implements WeeklyService {

	public static  final  String PROJECTTYPE="项目类型";
	public static  final  String PROJECTSTATUS="项目状态";
	
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
	@Autowired
	WeeklyDetailsService weeklyDetailsService;
	@Autowired
	DataDictionaryService dataDictionaryService;
	@Autowired
	DataDictionaryValueService dataDictionaryValueService;

	public WeeklyMapper getActualDao() {
		return (WeeklyMapper) getDao();
	}
	
	@Override
	public Map<Object, Object> getDescById(String id) {
		Map<Object, Object>  map=new HashMap<Object, Object> ();
		//项目周报
    	ProjectWeeklyDto pd=getProjectWeeklyDtoById(Long.parseLong(id));
    	pd.setId(id);
    
		map.put("pd", pd);
		
		// 本周项目版本
		List<String> projectVersion=selectProjectVersion(Long.parseLong(pd.getProjectId()));
		map.put("pv", StringUtils.join(projectVersion.toArray(),","));
		//本周项目阶段
		List<String> projectPhase=selectProjectPhase(Long.parseLong(pd.getProjectId()));
		map.put("pp", StringUtils.join(projectPhase.toArray(),","));
		//下周项目阶段
		List<String> nextprojectPhase=selectNextProjectPhase(Long.parseLong(pd.getProjectId()));
		map.put("npp", StringUtils.join(nextprojectPhase.toArray(),","));
		
		//本周进展情况 
		List<TaskDto> td=selectWeeklyProgress(Long.parseLong(pd.getProjectId()));
		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i+1);
		}
		map.put("td", td);		
		//下周工作计划
		List<NextWeeklyDto> wk=selectNextWeeklyProgress(Long.parseLong(pd.getProjectId()));
	
		for (int i = 0; i < wk.size(); i++) {
			wk.get(i).setNumber(i+1);
		}
		map.put("wk", wk);
		
		WeeklyPara weeklyPara=  new WeeklyPara();
		weeklyPara.setId(Long.parseLong(id));
		
		//当前重要风险
		String weeklyRist=selectWeeklyRist(id);
		JSONArray  weeklyRistJson=JSON.parseArray(weeklyRist);
		map.put("wr", weeklyRistJson.toJavaList(WeeklyJson.class));
		
		//当前重要问题
		String weeklyQuestion=selectWeeklyQuestion(id);
		JSONArray  weeklyQuestionJson=JSON.parseArray(weeklyQuestion);
	    map.put("wq", weeklyQuestionJson);
	    
	    //项目总体情况描述
	    WeeklyDetails wDetails=  weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(id));
	  //（实际项目发生工时/立项申请预估工时-1）%
	    map.put("wDetails", wDetails);
	    
	    return  map;
	}

	@Override
	public EasyuiPageOutput getListPage(WeeklyPara weeklyPara) {

		// 查询总页数
		int total = weeklyMapper.selectPageByWeeklyParaCount(weeklyPara);
		// 查询list
		CopyOnWriteArrayList<WeeklyPara> list = weeklyMapper.selectListPageByWeeklyPara(weeklyPara);
		CopyOnWriteArrayList<WeeklyPara> copyList = new CopyOnWriteArrayList();
		
		DataDictionary  ddit=DTOUtils.newDTO(DataDictionary.class);
		ddit.setName(PROJECTTYPE);
		ddit.setId(Long.parseLong("3"));
		
		List<DataDictionary> 	dditList=dataDictionaryService.list(ddit);
		//List<DataDictionary> dditList=dictionaryMapper.selectByExample(ddit);
		
		DataDictionaryValue  ddv;
		if (list != null && list.size() > 0) {
			for (WeeklyPara weeklyPara2 : list) {

				 weeklyPara2.setDate(weeklyPara2.getStartDate() + " 到 " + weeklyPara2.getEndDate());
				
				 ddv=DTOUtils.newDTO(DataDictionaryValue.class);
				 ddv.setValue(weeklyPara2.getProjectType());
				 ddv.setDdId(dditList.get(0).getId());
			     weeklyPara2.setProjectType(dataDictionaryValueService.list(ddv).get(0).getCode());
				
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
		pd.setBeginAndEndTime(DateUtil.getWeekFristDay() + "到" +DateUtil.getWeekFriday());
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket != null) {
			pd.setStageMan(userTicket.getUserName());
		}

		 User user = new User();
		 user.setId(Long.parseLong(pd.getBusinessParty()));
		// 业务方
		 BaseOutput<List<User>>  listByExample = userRpc.listByExample(user);
		 List<User> listUserParty = listByExample.getData();
		 pd.setBusinessParty(listUserParty.get(0).getUserName());
		 

		// 查询项目经理
		 user.setId(Long.parseLong(pd.getUserName()));
		 BaseOutput<List<User>>  listUserByExample = userRpc.listByExample(user);
		 List<User> listUsernName = listUserByExample.getData();
		 pd.setUserName(listUsernName.get(0).getUserName());
		

		// 项目所在部门
	     Department department=DTOUtils.newDTO(Department.class);
	     department.setId(Long.parseLong(pd.getProjectInDept()));
	     BaseOutput<List<Department>>   departmentByExample = departmentRpc.list(department);
		 List<Department> departmentList= departmentByExample.getData();
		 pd.setProjectInDept(departmentList.get(0).getName());
		 
		 
		// 查询项目类型
		DataDictionary  ddit=DTOUtils.newDTO(DataDictionary.class);
		ddit.setName(PROJECTTYPE);
		List<DataDictionary> 	dditList=dataDictionaryService.list(ddit);
		//List<DataDictionary> dditList=dictionaryMapper.selectByExample(ddit);//查询出id
		
		DataDictionaryValue  ddv=DTOUtils.newDTO(DataDictionaryValue.class);
		ddv.setValue(pd.getProjectType());
		ddv.setDdId(dditList.get(0).getId());
		pd.setProjectType(dataDictionaryValueService.list(ddv).get(0).getCode());
		pd.setCompletedProgressInt(Integer.parseInt(pd.getCompletedProgress()));
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

		DataDictionary  ddit=DTOUtils.newDTO(DataDictionary.class);
		ddit.setName(PROJECTSTATUS);
		List<DataDictionary> dditList=dataDictionaryService.list(ddit);//查询出id
		 User user ;
		for (int i = 0; i < td.size(); i++) {

			// 责任人
			 user = new User();
			 user.setId(Long.parseLong(td.get(i).getOwner()));
			 BaseOutput<List<User>>  listByExample = userRpc.listByExample(user);
			 List<User> listUserParty = listByExample.getData();
			 td.get(i).setOwner(listUserParty.get(0).getUserName());
			 
			// 版本
			td.get(i).setVersionId(projectVersionMapper.selectByPrimaryKey(Long.parseLong(td.get(i).getVersionId())).getVersion());
			// 阶段
			td.get(i).setPhaseId(projectPhaseMapper.selectByPrimaryKey(Long.parseLong(td.get(i).getPhaseId())).getName());
			// 本周工时
			td.get(i).setWeekHour(DateUtil.getDatePoor(td.get(i).getStartDate(), td.get(i).getEndDate()));
			// 实际工时
			td.get(i).setRealHour(td.get(i).getOverHour() + td.get(i).getTaskHour() + "");
			// 工时偏差% （实际任务工时/预估任务工时-1）%
			int pro = ( (td.get(i).getOverHour() + td.get(i).getTaskHour()) / td.get(i).getPlanTime()-1) * 100;
			
			td.get(i).setHourDeviation(pro + "");
			// 完成情况
			DataDictionaryValue  ddv=DTOUtils.newDTO(DataDictionaryValue.class);
			ddv.setValue(td.get(i).getStatus());
			ddv.setDdId(dditList.get(0).getId());
			td.get(i).setStatus(dataDictionaryValueService.list(ddv).get(0).getCode());
			

		}
		return td;
	}

	/**
	 * 下周工作计划
	 * */
	@Override
	public List<NextWeeklyDto> selectNextWeeklyProgress(Long id) {
		List<NextWeeklyDto> nwd = weeklyMapper.selectNextWeeklyProgress(id);
		User user ;
		
		for (NextWeeklyDto nextWeeklyDto : nwd) {
			 user = new User();
			 user.setId(Long.parseLong(nextWeeklyDto.getOwner()));
			 BaseOutput<List<User>>  listByExample = userRpc.listByExample(user);
			 List<User> listUserParty = listByExample.getData();
			 nextWeeklyDto.setOwner(listUserParty.get(0).getUserName());
		}
		
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
    public File downLoad(File file,String id) {
		// 项目周报
		ProjectWeeklyDto pd = getProjectWeeklyDtoById(Long.parseLong(id));
		pd.setId(id);
		// 本周项目版本
		List<String> projectVersion = selectProjectVersion(Long.parseLong(pd.getProjectId()));
		// 本周项目阶段
		List<String> projectPhase = selectProjectPhase(Long.parseLong(pd.getProjectId()));
		// 下周项目阶段
		List<String> nextprojectPhase = selectNextProjectPhase(Long.parseLong(pd.getProjectId()));
		// 本周进展情况
		List<TaskDto> td = selectWeeklyProgress(Long.parseLong(pd.getProjectId()));
		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i + 1);
		}
		// 下周工作计划
		List<NextWeeklyDto> wk = selectNextWeeklyProgress(Long.parseLong(pd.getProjectId()));
		for (int i = 0; i < wk.size(); i++) {
			wk.get(i).setNumber(i + 1);
		}
		WeeklyPara weeklyPara = new WeeklyPara();
		weeklyPara.setId(Long.parseLong(id));
		// 当前重要风险
		String weeklyRist = selectWeeklyRist(id);
		JSONArray weeklyRistJson = JSON.parseArray(weeklyRist);
		// 当前重要风险
		String weeklyQuestion = selectWeeklyQuestion(id);
		JSONArray weeklyQuestionJson = JSON.parseArray(weeklyQuestion);
		// 项目总体情况描述
		WeeklyDetails wDetails = weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(id));
		
		try {
			file=WordExport.getFile(file,pd, projectVersion, projectPhase, nextprojectPhase, td, wk, weeklyRistJson, weeklyQuestionJson, wDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}
       return  file;
		
	}

	
}