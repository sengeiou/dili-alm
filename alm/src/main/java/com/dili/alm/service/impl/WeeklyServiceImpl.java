package com.dili.alm.service.impl;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.dili.alm.domain.dto.Page;
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
	public static  final  String PROJECTTYPEID="3";
	public static  final  Long PHASENAMEVALUE=10L;
	                             
	
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
    	pd.setCompletedProgressInt(Integer.parseInt(pd.getCompletedProgress()));
    	pd.setId(id);
		map.put("pd", pd);
		Weekly wkly=weeklyMapper.selectByPrimaryKey(Long.parseLong(id));
		/*String[]  strDate=pd.getBeginAndEndTime().split("到");*/
		WeeklyPara weeklyPara=  new WeeklyPara();
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		weeklyPara.setStartDate(DateUtil.getDateStr(wkly.getStartDate()));
		weeklyPara.setEndDate(DateUtil.getDateStr(wkly.getEndDate()));
		
		// 本周项目版本
		List<String> projectVersion=selectProjectVersion(weeklyPara);
		map.put("pv", StringUtils.join(projectVersion.toArray(),","));
		//本周项目阶段
		List<String> projectPhase=selectProjectPhase(weeklyPara);
		map.put("pp", StringUtils.join(projectPhase.toArray(),","));
		//本周进展情况 
		List<TaskDto> td=selectWeeklyProgress(weeklyPara);
		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i+1);
		}
		map.put("td", td);	
		weeklyPara.setId(Long.parseLong(id));
		//当前重要风险
		String weeklyRist=selectWeeklyRist(weeklyPara);
		if(weeklyRist!=null){
			JSONArray  weeklyRistJson=JSON.parseArray(weeklyRist);
			map.put("wr", weeklyRistJson.toJavaList(WeeklyJson.class));
		}else
			map.put("wr", null);
		
		//当前重要问题
		String weeklyQuestion=selectWeeklyQuestion(weeklyPara);
		if(weeklyQuestion!=null){
		JSONArray  weeklyQuestionJson=JSON.parseArray(weeklyQuestion);
	    map.put("wq", weeklyQuestionJson);
		}else{
			 map.put("wq", null);
		}
		
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		String dateone=DateUtil.getFirstAndFive(wkly.getStartDate()).get("one");
		String datefive= DateUtil.getFirstAndFive(wkly.getEndDate()).get("five");
		//Date  dateDateone=DateUtil.getStrDateyyyyMMdd(dateone);
		//Date  dateDatetwo= DateUtil.getStrDateyyyyMMdd(datefive);
		weeklyPara.setStartDate(DateUtil.getAddDay(dateone+" 00:00:00",7));
		weeklyPara.setEndDate(DateUtil.getAddDay(datefive+" 23:59:59",7));
		
		//下周工作计划
		List<NextWeeklyDto> wk=selectNextWeeklyProgress(weeklyPara);
	
		for (int i = 0; i < wk.size(); i++) {
			wk.get(i).setNumber(i+1);
		}
		map.put("wk", wk);
		
		//下周项目阶段
		List<String> nextprojectPhase=selectNextProjectPhase(weeklyPara);
		map.put("npp", StringUtils.join(nextprojectPhase.toArray(),","));
		
	    //项目总体情况描述
	    WeeklyDetails wDetails=  weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(id));
	  //（实际项目发生工时/立项申请预估工时-1）%
	    map.put("wDetails", wDetails);
	    
	    return  map;
	}

	@Override
	public Map<Object, Object> getDescAddById(String id) {
		Map<Object, Object>  map=new HashMap<Object, Object> ();
		//项目周报
    	ProjectWeeklyDto pd=getProjectAddWeeklyDtoById(Long.parseLong(id));
    	pd.setCompletedProgressInt(Integer.parseInt(pd.getCompletedProgress()));
    	pd.setId(id);
		map.put("pd", pd);
		Weekly wkly=weeklyMapper.selectByPrimaryKey(Long.parseLong(id));
		/*String[]  strDate=pd.getBeginAndEndTime().split("到");*/
		WeeklyPara weeklyPara=  new WeeklyPara();
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		weeklyPara.setStartDate(DateUtil.getDateStr(wkly.getStartDate()));
		weeklyPara.setEndDate(DateUtil.getDateStr(wkly.getEndDate()));
		
		// 本周项目版本
		List<String> projectVersion=selectProjectVersion(weeklyPara);
		map.put("pv", StringUtils.join(projectVersion.toArray(),","));
		//本周项目阶段
		List<String> projectPhase=selectProjectPhase(weeklyPara);
		map.put("pp", StringUtils.join(projectPhase.toArray(),","));
		//本周进展情况 
		List<TaskDto> td=selectWeeklyProgress(weeklyPara);
		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i+1);
		}
		map.put("td", td);	
		weeklyPara.setId(Long.parseLong(id));
		//当前重要风险
		String weeklyRist=selectWeeklyRist(weeklyPara);
		if(weeklyRist!=null){
			JSONArray  weeklyRistJson=JSON.parseArray(weeklyRist);
			map.put("wr", weeklyRistJson.toJavaList(WeeklyJson.class));
		}else
			map.put("wr", null);
		
		//当前重要问题
		String weeklyQuestion=selectWeeklyQuestion(weeklyPara);
		if(weeklyQuestion!=null){
		JSONArray  weeklyQuestionJson=JSON.parseArray(weeklyQuestion);
	    map.put("wq", weeklyQuestionJson);
		}else{
			 map.put("wq", null);
		}
		
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		String dateone=DateUtil.getFirstAndFive(wkly.getStartDate()).get("one");
		String datefive= DateUtil.getFirstAndFive(wkly.getEndDate()).get("five");
		//Date  dateDateone=DateUtil.getStrDateyyyyMMdd(dateone);
		//Date  dateDatetwo= DateUtil.getStrDateyyyyMMdd(datefive);
		weeklyPara.setStartDate(DateUtil.getAddDay(dateone+" 00:00:00",7));
		weeklyPara.setEndDate(DateUtil.getAddDay(datefive+" 23:59:59",7));
		
		//下周工作计划
		List<NextWeeklyDto> wk=selectNextWeeklyProgress(weeklyPara);
	
		for (int i = 0; i < wk.size(); i++) {
			wk.get(i).setNumber(i+1);
		}
		map.put("wk", wk);
		
		//下周项目阶段
		List<String> nextprojectPhase=selectNextProjectPhase(weeklyPara);
		map.put("npp", StringUtils.join(nextprojectPhase.toArray(),","));
		
	    //项目总体情况描述
	    WeeklyDetails wDetails=  weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(id));
	  //（实际项目发生工时/立项申请预估工时-1）%
	    map.put("wDetails", wDetails);
	    
	    return  map;
	}
	@Override
	public EasyuiPageOutput getListPage(WeeklyPara weeklyPara) {
		
		//参数转换 项目类型
		if(weeklyPara.getProjectType()!=null&&!weeklyPara.getProjectType().endsWith("-1")){
			 DataDictionaryValue  ddv=DTOUtils.newDTO(DataDictionaryValue.class);
			 ddv.setId(Long.parseLong(weeklyPara.getProjectType()));//项目类型
			 weeklyPara.setProjectType(dataDictionaryValueService.list(ddv).get(0).getValue());
		}else{
			 weeklyPara.setProjectType(null);
		}
		//参数转换 项目状态
		if(weeklyPara.getProjectStatus()!=null&&!weeklyPara.getProjectStatus().endsWith("-1")){
		 DataDictionaryValue  ddv=DTOUtils.newDTO(DataDictionaryValue.class);
		 ddv.setId(Long.parseLong(weeklyPara.getProjectStatus()));//项目类型
		 weeklyPara.setProjectStatus(dataDictionaryValueService.list(ddv).get(0).getValue());
		}else{
			 weeklyPara.setProjectStatus(null);
		}
		
		//参数转换 项目状态
		if(weeklyPara.getUserName()!=null&&weeklyPara.getUserName().endsWith("-1")){
			weeklyPara.setUserName(null);
		}
		if(weeklyPara.getEndDate()!=null){
			String dateString =DateUtil.getAddDay(weeklyPara.getEndDate(), 1);//加一天
		    weeklyPara.setEndDate(dateString);  
		}
	     
		// 查询总页数
		int total = weeklyMapper.selectPageByWeeklyParaCount(weeklyPara);
		// 查询list
		Page page=new Page();
		page.setPageIndex(weeklyPara.getPage());
		page.setPageSize(weeklyPara.getRows());
		CopyOnWriteArrayList<WeeklyPara> list=null;
		try {
			 list = weeklyMapper.selectListPageByWeeklyPara(weeklyPara,page);
		} catch (Exception e) {
		e.printStackTrace();
		}
		
		
		
		CopyOnWriteArrayList<WeeklyPara> copyList = new CopyOnWriteArrayList();
		
		DataDictionary  ddit=DTOUtils.newDTO(DataDictionary.class);
		ddit.setName(PROJECTTYPE);
		ddit.setId(Long.parseLong(PROJECTTYPEID));
		
		List<DataDictionary> 	dditList=dataDictionaryService.list(ddit);
		//List<DataDictionary> dditList=dictionaryMapper.selectByExample(ddit);
		
		DataDictionaryValue  ddv;
		 User user =null;
		if (list != null && list.size() > 0) {
			for (WeeklyPara weeklyPara2 : list) {
				
				// 项目负责人
				 user = new User();
				 user.setId(Long.parseLong(weeklyPara2.getUserName()));
				 BaseOutput<List<User>>  listByExample = userRpc.listByExample(user);
				 List<User> listUserParty = listByExample.getData();
				 if(listUserParty!=null&&listUserParty.size()>0){
				 weeklyPara2.setUserName(listUserParty.get(0).getRealName());
				 }
				 if(Integer.parseInt(weeklyPara2.getProjectStatus())<8){
					 weeklyPara2.setProjectStatus(" <font color='green'>正常--偏差<8%</font>");
				 }else if(Integer.parseInt(weeklyPara2.getProjectStatus())>15){
					 weeklyPara2.setProjectStatus("<font color='red'>警戒--偏差>15%</font>");
				 }else{
					 weeklyPara2.setProjectStatus("<font color='orange'>预警--8%<偏差<15%</font>");
				 }
				 
				 weeklyPara2.setDate(weeklyPara2.getStartDate().substring(0,10) + " 到 " + weeklyPara2.getEndDate().substring(0,10));
				
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
	public ProjectWeeklyDto getProjectWeeklyDtoById(Long id) {
		
		ProjectWeeklyDto pd = weeklyMapper.selectProjectWeeklyDto(id);
		if (pd != null && pd.getPlanDate() != null)
			pd.setPlanDate(pd.getPlanDate().substring(0, 10));
		
		pd.setBeginAndEndTime(pd.getStartDate().substring(0,10) + "到" +pd.getEndDate().substring(0,10));
		
		if(DateUtil.getWeekOfDate(new Date()).endsWith("星期日")){
			HashMap<String,String>  map =DateUtil.getFirstAndFive();
			
			pd.setBeginAndEndTime(map.get("one").substring(0,10) + "到" +map.get("five").substring(0,10));
		}
		
		 if(pd.getStageMan()!=null|pd.getStageMan()!=""){
			 User userStageMan = new User();
			 if(pd.getStageMan()!=null&&pd.getStageMan()!=""){
				 userStageMan.setId(Long.parseLong(pd.getStageMan()));
				 BaseOutput<List<User>>  listStageMan = userRpc.listByExample(userStageMan);
				 List<User> listUserlistStageMan = listStageMan.getData();
				 if(listUserlistStageMan!=null&&listUserlistStageMan.size()>0)
				      pd.setStageMan(listUserlistStageMan.get(0).getRealName());
			 }
			
		 
		 }

		 User user = new User();
		 user.setId(Long.parseLong(pd.getBusinessParty()));
		// 业务方
		BaseOutput<List<User>>  listByExample = userRpc.listByExample(user);
		 List<User> listUserParty = listByExample.getData();
		 if(listUserParty!=null&&listUserParty.size()>0)
		      pd.setBusinessParty(listUserParty.get(0).getRealName());
		 

		// 查询项目经理
		 user.setId(Long.parseLong(pd.getUserName()));
		 BaseOutput<List<User>>  listUserByExample = userRpc.listByExample(user);
		 List<User> listUsernName = listUserByExample.getData();
		 if(listUsernName!=null&&listUsernName.size()>0)
		    pd.setUserName(listUsernName.get(0).getRealName());
		

		// 项目所在部门
	     Department department=new Department();
	     department.setId(Long.parseLong(pd.getProjectInDept()));
	     BaseOutput<List<Department>>   departmentByExample = departmentRpc.list(department);
		 List<Department> departmentList= departmentByExample.getData();
		 if(departmentList!=null&&departmentList.size()>0)
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

	
	@Override
	public ProjectWeeklyDto getProjectAddWeeklyDtoById(Long id) {
		
		ProjectWeeklyDto pd = weeklyMapper.selectProjectWeeklyDto(id);
		if (pd != null && pd.getPlanDate() != null)
			pd.setPlanDate(pd.getPlanDate().substring(0, 10));
		
		pd.setBeginAndEndTime(pd.getStartDate().substring(0,10) + "到" +pd.getEndDate().substring(0,10));
		
		if(DateUtil.getWeekOfDate(new Date()).endsWith("星期日")){
			HashMap<String,String>  map =DateUtil.getFirstAndFive();
			
			pd.setBeginAndEndTime(map.get("one").substring(0,10) + "到" +map.get("five").substring(0,10));
		}
		
		 UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		 pd.setStageMan(userTicket.getRealName());
		 
		

		 User user = new User();
		 user.setId(Long.parseLong(pd.getBusinessParty()));
		// 业务方
		BaseOutput<List<User>>  listByExample = userRpc.listByExample(user);
		 List<User> listUserParty = listByExample.getData();
		 if(listUserParty!=null&&listUserParty.size()>0)
		      pd.setBusinessParty(listUserParty.get(0).getRealName());
		 

		// 查询项目经理
		 user.setId(Long.parseLong(pd.getUserName()));
		 BaseOutput<List<User>>  listUserByExample = userRpc.listByExample(user);
		 List<User> listUsernName = listUserByExample.getData();
		 if(listUsernName!=null&&listUsernName.size()>0)
		    pd.setUserName(listUsernName.get(0).getRealName());
		

		// 项目所在部门
	     Department department=new Department();
	     department.setId(Long.parseLong(pd.getProjectInDept()));
	     BaseOutput<List<Department>>   departmentByExample = departmentRpc.list(department);
		 List<Department> departmentList= departmentByExample.getData();
		 if(departmentList!=null&&departmentList.size()>0)
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
	public String selectWeeklyQuestion(WeeklyPara weeklyPara) {
		return weeklyMapper.selectWeeklyQuestion(weeklyPara);
	}

	/**
	 * 当前重要风险
	 * */
	@Override
	public String selectWeeklyRist(WeeklyPara weeklyPara) {
		return weeklyMapper.selectWeeklyRist(weeklyPara);
	}

	/**
	 * 本周进展情况
	 * */
	@Override
	public List<TaskDto> selectWeeklyProgress(WeeklyPara weeklyPara) {

		
		List<TaskDto> td = weeklyMapper.selectWeeklyProgress(weeklyPara);

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
			 if(listUserParty!=null&&listUserParty.size()>0)
			    td.get(i).setOwner(listUserParty.get(0).getRealName());
			 
			// 版本
			td.get(i).setVersionId(projectVersionMapper.selectByPrimaryKey(Long.parseLong(td.get(i).getVersionId())).getVersion());
			// 阶段
			String  phaseName=projectPhaseMapper.selectByPrimaryKey(Long.parseLong(td.get(i).getPhaseId())).getName();
			DataDictionaryValue  ddvPhaseName=DTOUtils.newDTO(DataDictionaryValue.class);
			ddvPhaseName.setValue(phaseName);
			ddvPhaseName.setDdId(PHASENAMEVALUE);
			td.get(i).setPhaseId(dataDictionaryValueService.list(ddvPhaseName).get(0).getCode());
			
			// 本周工时
			Integer intweekHour=Integer.parseInt(DateUtil.differentDays( td.get(i).getStartDate(),td.get(i).getEndDate()));
			td.get(i).setWeekHour((intweekHour)*8+"");
			// 实际工时
			td.get(i).setRealHour(td.get(i).getOverHour() + td.get(i).getTaskHour() + "");
			// 工时偏差% （实际任务工时/预估任务工时-1）%
			double  realHourandOverHour=td.get(i).getOverHour() + td.get(i).getTaskHour();
			double  planTime=td.get(i).getPlanTime();
			DecimalFormat    df   = new DecimalFormat("######0.00");   
			
			
			// 完成情况
			//DataDictionaryValue  ddv=DTOUtils.newDTO(DataDictionaryValue.class);
			//ddv.setValue(td.get(i).getStatus());
			//ddv.setDdId(dditList.get(0).getId());
			
			 if(td.get(i).getStatus().equals("2")){//已完成就是等于二
				 td.get(i).setStatus("YES");
				 double pro = ( realHourandOverHour/(intweekHour*8) -1) * 100;
					td.get(i).setHourDeviation(df.format(pro) );
				 
			 }else{
				 td.get(i).setStatus("NO");
				 
				 double pro = ((intweekHour- realHourandOverHour)/intweekHour) * 100;
					td.get(i).setHourDeviation(df.format(pro) );
			 }
			
			

		}
		return td;
	}

	/**
	 * 下周工作计划
	 * */
	@Override
	public List<NextWeeklyDto> selectNextWeeklyProgress(WeeklyPara weeklyPara) {
		List<NextWeeklyDto> nwd = weeklyMapper.selectNextWeeklyProgress(weeklyPara);
		User user ;
		
		for (NextWeeklyDto nextWeeklyDto : nwd) {
			 user = new User();
		     user.setId(Long.parseLong(nextWeeklyDto.getOwner()));
			 BaseOutput<List<User>>  listByExample = userRpc.listByExample(user);
			 List<User> listUserParty = listByExample.getData();
			 if(listUserParty!=null&&listUserParty.size()>0)
			   nextWeeklyDto.setOwner(listUserParty.get(0).getRealName());
		}
		
		return nwd;
	}

	/**
	 * 下周项目阶段
	 * */
	@Override
	public List<String> selectNextProjectPhase(WeeklyPara weeklyPara) {
		
		List<String> listStr= weeklyMapper.selectNextProjectPhase(weeklyPara);
		DataDictionaryValue  ddvPhaseName=DTOUtils.newDTO(DataDictionaryValue.class);
		ddvPhaseName.setDdId(PHASENAMEVALUE);
		
		List<String>   list=new ArrayList<String>();
		for (String phaseName : listStr) {
		       ddvPhaseName.setValue(phaseName);
		       list.add( dataDictionaryValueService.list(ddvPhaseName).get(0).getCode() );
		}
		
		return list;
	}

	/**
	 * 本周项目阶段
	 * */
	@Override
	public List<String> selectProjectPhase(WeeklyPara weeklyPara) {
		
		List<String> listStr= weeklyMapper.selectProjectPhase(weeklyPara);
		DataDictionaryValue  ddvPhaseName=DTOUtils.newDTO(DataDictionaryValue.class);
		ddvPhaseName.setDdId(PHASENAMEVALUE);
		
		List<String>   list=new ArrayList<String>();
		for (String phaseName : listStr) {
		       ddvPhaseName.setValue(phaseName);
		       list.add( dataDictionaryValueService.list(ddvPhaseName).get(0).getCode() );
		}
		
		return list;
	}

	/**
	 * 本周项目版本
	 * */
	@Override
	public List<String> selectProjectVersion(WeeklyPara weeklyPara) {
		return weeklyMapper.selectProjectVersion(weeklyPara);
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
		WeeklyPara weeklyPara = new WeeklyPara();
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		
		//weeklyPara.setStartDate(DateUtil.getFirstAndFive().get("one")+" 00:00:00");
		weeklyPara.setEndDate(DateUtil.getFirstAndFive().get("five")+" 23:59:59");
		Weekly wkly=weeklyMapper.selectByPrimaryKey(Long.parseLong(id));
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		weeklyPara.setStartDate(DateUtil.getDateStr(wkly.getStartDate()));
		weeklyPara.setEndDate(DateUtil.getDateStr(wkly.getEndDate()));
		
		// 本周项目版本
		List<String> projectVersion = selectProjectVersion(weeklyPara);
		// 本周项目阶段
		List<String> projectPhase = selectProjectPhase(weeklyPara);
		// 本周进展情况
		List<TaskDto> td = selectWeeklyProgress(weeklyPara);
		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i + 1);
		}
		weeklyPara.setId(Long.parseLong(id));
		// 当前重要风险
		String weeklyRist = selectWeeklyRist(weeklyPara);
		JSONArray weeklyRistJson = JSON.parseArray(weeklyRist);
		// 当前重要风险
		String weeklyQuestion = selectWeeklyQuestion(weeklyPara);
		JSONArray weeklyQuestionJson = JSON.parseArray(weeklyQuestion);
	
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		// 下周项目阶段
		//weeklyPara.setStartDate(DateUtil.getNextMonday(new Date())+" 00:00:00");
		//weeklyPara.setEndDate(DateUtil.getNextFive(new Date())+" 23:59:59");
		String dateone=DateUtil.getFirstAndFive(wkly.getStartDate()).get("one");
		String datefive= DateUtil.getFirstAndFive(wkly.getEndDate()).get("five");
	
		weeklyPara.setStartDate(DateUtil.getAddDay(dateone+" 00:00:00",7));
		weeklyPara.setEndDate(DateUtil.getAddDay(datefive+" 23:59:59",7));
		
		
		List<String> nextprojectPhase = selectNextProjectPhase(weeklyPara);
		
		// 下周工作计划
		List<NextWeeklyDto> wk = selectNextWeeklyProgress(weeklyPara);
		for (int i = 0; i < wk.size(); i++) {
			wk.get(i).setNumber(i + 1);
		}
		
		
		// 项目总体情况描述
		WeeklyDetails wDetails = weeklyDetailsService.getWeeklyDetailsByWeeklyId(Long.parseLong(id));
		
		try {
			file=WordExport.getFile(file,pd, projectVersion, projectPhase, nextprojectPhase, td, wk, weeklyRistJson, weeklyQuestionJson, wDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}
       return  file;
		
	}

	/**
	 * 根据任务表中个的项目经理id 查询项目经理
	 * @return
	 */
	@Override
	public List<WeeklyPara> getUser() {
		User user ;
		WeeklyPara Wp=new WeeklyPara();
		Wp.setUserName("项目经理选择");
		Wp.setId(Long.parseLong("-1"));
		List<WeeklyPara> userList=weeklyMapper.getUser();
		
		for (int j = 0; j < userList.size(); j++) {
			 user=new User();
		     user.setId(Long.parseLong(userList.get(j).getUserName()));
			 BaseOutput<List<User>>  listByExample = userRpc.listByExample(user);
			 List<User> listUserParty = listByExample.getData();
			 userList.get(j).setUserName(listUserParty.get(0).getRealName());
			 userList.get(j).setId(listUserParty.get(0).getId());
		}
		
		userList.add(0, Wp);
		
		return userList;
	}

	@Override
	public Map<Object, Object> addWeekly(Weekly wk) {
		
		//Weekly wk = insertWeeklyByprojectId(projectId);
		Map<Object, Object>  map=new HashMap<Object, Object> ();
		//项目周报
    	ProjectWeeklyDto pd=getProjectWeeklyDtoById(wk.getId());
    	pd.setId(wk.getId()+"");
		map.put("pd", pd);
		
		WeeklyPara weeklyPara=  new WeeklyPara();
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		weeklyPara.setStartDate(DateUtil.getFirstAndFive().get("one")+" 00:00:00");
		weeklyPara.setEndDate(DateUtil.getFirstAndFive().get("five")+" 23:59:59");
		
		// 本周项目版本
		List<String> projectVersion=selectProjectVersion(weeklyPara);
		map.put("pv", StringUtils.join(projectVersion.toArray(),","));
		//本周项目阶段
		List<String> projectPhase=selectProjectPhase(weeklyPara);
		map.put("pp", StringUtils.join(projectPhase.toArray(),","));
		//本周进展情况 
		List<TaskDto> td=selectWeeklyProgress(weeklyPara);
		for (int i = 0; i < td.size(); i++) {
			td.get(i).setNumber(i+1);
		}
		map.put("td", td);	
		weeklyPara.setId(wk.getId());
		//当前重要风险
		String weeklyRist=selectWeeklyRist(weeklyPara);
		if(weeklyRist!=null){
			JSONArray  weeklyRistJson=JSON.parseArray(weeklyRist);
			map.put("wr", weeklyRistJson.toJavaList(WeeklyJson.class));
		}else
			map.put("wr", null);
		
		//当前重要问题
		String weeklyQuestion=selectWeeklyQuestion(weeklyPara);
		if(weeklyQuestion!=null){
		JSONArray  weeklyQuestionJson=JSON.parseArray(weeklyQuestion);
	    map.put("wq", weeklyQuestionJson);
		}else{
			 map.put("wq", null);
		}
		
		weeklyPara.setId(Long.parseLong(pd.getProjectId()));
		weeklyPara.setStartDate(DateUtil.getNextMonday(new Date())+" 00:00:00");
		weeklyPara.setEndDate(DateUtil.getNextFive(new Date())+" 23:59:59");
		//下周工作计划
		List<NextWeeklyDto> wek=selectNextWeeklyProgress(weeklyPara);
	
		for (int i = 0; i < wek.size(); i++) {
			wek.get(i).setNumber(i+1);
		}
		map.put("wk", wek);
		
		//下周项目阶段
		List<String> nextprojectPhase=selectNextProjectPhase(weeklyPara);
		map.put("npp", StringUtils.join(nextprojectPhase.toArray(),","));
		
	    //项目总体情况描述
	    WeeklyDetails wDetails=  weeklyDetailsService.getWeeklyDetailsByWeeklyId(wk.getId());
	  //（实际项目发生工时/立项申请预估工时-1）%
	    map.put("wDetails", wDetails);
	    
	    return  map;
	}

	public  Map<String, Weekly> insertWeeklyByprojectId(String projectId) {
		
		Map<String, Weekly>  map=new HashMap<String, Weekly>();
		
		Weekly wk=DTOUtils.newDTO(Weekly.class);
		WeeklyPara weeklyPara= new WeeklyPara();
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		weeklyPara.setStartDate(DateUtil.getFirstAndFive().get("one") +" 00:00:00");
		weeklyPara.setEndDate  (DateUtil.getFirstAndFive().get("five")+" 23:59:59");
		
		if(userTicket!=null){
			weeklyPara.setUserId(userTicket.getId());
		}
		weeklyPara.setProjectId(Long.parseLong(projectId));
		
	    wk = weeklyMapper.selecByProjectId(weeklyPara);
	    Weekly wkk=DTOUtils.newDTO(Weekly.class);
		if(wk==null){
			wkk.setProjectId(Long.parseLong(projectId));
			wkk.setCreated(new Date());
			wkk.setModified(null);
			wkk.setStartDate(DateUtil.getStrDate(DateUtil.getFirstAndFive().get("one")+" 00:00:00"));
			wkk.setEndDate(DateUtil.getStrDate(DateUtil.getFirstAndFive().get("five")+" 23:59:59"));
			
			if (userTicket != null) {
				wkk.setCreateMemberId(userTicket.getId());
				wkk.setModifyMemberId(userTicket.getId());
			}
	
			weeklyMapper.insertSelective(wkk);
			map.put("one", wkk);
				
		}else{
			map.put("two", wk);
		}
		map.put("three",  DTOUtils.newDTO(Weekly.class));
		return map;
	}

	
}