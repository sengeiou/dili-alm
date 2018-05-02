package com.dili.alm.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.beetl.ext.fn.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.util.PoiPublicUtil;

import com.alibaba.fastjson.JSONArray;
import com.dili.alm.dao.WorkDayMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.WorkDay;
import com.dili.alm.domain.dto.WorkDayRoleDto;
import com.dili.alm.rpc.ResourceRpc;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.WorkDayService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
@Service
public class WorkDayServiceImpl extends BaseServiceImpl<WorkDay, Long> implements WorkDayService{
	public WorkDayMapper getActualDao() {
        return (WorkDayMapper)getDao();
    }
	static final String MILESTONES_PATH_PREFIX = "fileupload";
	@Autowired
	private ResourceRpc resourceRpc;
	private static final String SET_WORK_DAY="setWorkDay";
	private static final String WEEK_NUM="周数";
	private static final String START_WORK_DAY="工作日开始日期";
	private static final String END_WORK_DAY="工作日结束日期";
		/**
		 * 得到下周工作时间
		 */
		@Override
		public WorkDay getNextWeeklyWorkDays() {
//			Date as = new Date(new Date().getTime()-24*60*60*1000);
			WorkDay selectByPrimaryKey = this.getActualDao().getWorkDayNowDate(DateUtil.getDate(new Date()));
			WorkDay workDay=DTOUtils.newDTO(WorkDay.class);
			workDay.setWorkDayYear(selectByPrimaryKey.getWorkDayYear());
			workDay.setWordDayWeek(selectByPrimaryKey.getWordDayWeek()+1);
			WorkDay maxWeekWorkDay = this.getActualDao().getMaxOrMinWeekWorkDay(1, selectByPrimaryKey.getWorkDayYear());
			WorkDay nextWork=DTOUtils.newDTO(WorkDay.class); 
			nextWork = this.getActualDao().selectOne(workDay);
			if(maxWeekWorkDay.getId()==nextWork.getId()){
				String workDayYear = nextWork.getWorkDayYear();
				int newYear = Integer.parseInt(workDayYear)+1;
				WorkDay minWeekWorkDay = this.getActualDao().getMaxOrMinWeekWorkDay(0,String.valueOf(newYear));
				int oldWorkDaysByMillisecond = DateUtil.differentDaysByMillisecond(nextWork.getWorkStartTime(),nextWork.getWorkEndTime());
				int newWorkDaysByMillisecond = DateUtil.differentDaysByMillisecond( minWeekWorkDay.getWorkStartTime(),minWeekWorkDay.getWorkEndTime());
				if(oldWorkDaysByMillisecond<=2&&newWorkDaysByMillisecond<=2){
					nextWork.setWorkStartTime(nextWork.getWorkStartTime());
					nextWork.setWorkEndTime(minWeekWorkDay.getWorkEndTime());
				}
				
			}
			return nextWork;	
			
		}
		/**
		 * 得到本周工作时间
		 */
		@Override
		public WorkDay getNowWeeklyWorkDay() {
			WorkDay workDayNowDate = this.getActualDao().getWorkDayNowDate(DateUtil.getDate(new Date()));	
			if(workDayNowDate==null){
				return null;
			}
			WorkDay maxWeekWorkDay = this.getActualDao().getMaxOrMinWeekWorkDay(1, workDayNowDate.getWorkDayYear());
			if(maxWeekWorkDay.getId()==workDayNowDate.getId()){
				String workDayYear = workDayNowDate.getWorkDayYear();
				int newYear = Integer.parseInt(workDayYear)+1;
				WorkDay minWeekWorkDay = this.getActualDao().getMaxOrMinWeekWorkDay(0,String.valueOf(newYear));
				int oldWorkDaysByMillisecond = DateUtil.differentDaysByMillisecond(workDayNowDate.getWorkStartTime(),workDayNowDate.getWorkEndTime());
				int newWorkDaysByMillisecond = DateUtil.differentDaysByMillisecond( minWeekWorkDay.getWorkStartTime(),minWeekWorkDay.getWorkEndTime());
				if(oldWorkDaysByMillisecond<=2&&newWorkDaysByMillisecond<=2){
					workDayNowDate.setWorkStartTime(workDayNowDate.getWorkStartTime());
					workDayNowDate.setWorkEndTime(minWeekWorkDay.getWorkEndTime());
				}
			}
			
			return workDayNowDate;
		}
		/**
		 * 上传的年份
		 */
		@Override
		public List<String> getWorkDayYaers() {
			return this.getActualDao().getWorkYear();	
		}
		
		@Override
		@Transactional
		public BaseOutput upload(MultipartFile myfile,String year) {
			
			boolean flat=false;
			//先删除
			this.getActualDao().deteleWorkDaysByYear(year);
			// 指定当前项目的相对路径
			String path = MILESTONES_PATH_PREFIX + "/";
			File pathFile = new File(path);
			// 父目录不存在则创建
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			String oildFileName = myfile.getOriginalFilename();
			String fileName=null;
			String sname = oildFileName.substring(oildFileName.lastIndexOf("."));
			if(sname.equals(".xls")){
				 fileName = "年度工作日.xls";
			}else if(sname.equals(".xlsx")){
				 fileName = "年度工作日.xlsx";
			}
			File dest = new File(path + fileName);
			BufferedOutputStream buffStream = null;
			try {
				byte[] bytes = myfile.getBytes();
				buffStream = new BufferedOutputStream(new FileOutputStream(dest));
				buffStream.write(bytes);
				buffStream.flush();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException("上传失败");
			} finally {
				if (buffStream != null) {
					try {
						buffStream.close();
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
			BaseOutput dataFromExcel = getDataFromExcel(path + fileName,year);
			List<WorkDay> workDayList=new ArrayList<WorkDay>();
			if(dataFromExcel.getCode().equals("true")){
				workDayList=JSONArray.parseArray(dataFromExcel.getResult(), WorkDay.class);
			}
			if(workDayList!=null&&workDayList.size()>0){
				 int batchInsert = this.getActualDao().insertList(workDayList);
				 if(batchInsert==workDayList.size()){
						return BaseOutput.success("导入成功");
				 }    
			}

				
	        return BaseOutput.failure(dataFromExcel.getResult());

		}
		
		/**
		 * 读取出filePath中的所有数据信息
		 * @param filePath excel文件的绝对路径
		 * 
		 */
		 
		public static BaseOutput getDataFromExcel(String filePath,String year){
		  //String filePath = "E:\\123.xlsx";
		   
		   List<WorkDay> list=new ArrayList<WorkDay>();
		  Map<String,Object> map=new HashMap<String, Object>();
		   
		  FileInputStream fis =null;
		  Workbook wookbook = null;
		   
		  try
		  {
		    //获取一个绝对地址的流
		     fis = new FileInputStream(filePath);
		  }
		  catch(Exception e)
		  {
		    e.printStackTrace();
		  }
		  String sname = filePath.substring(filePath.lastIndexOf("."));
		  if(sname.equals(".xls")){
			  try
			  {
			    //2003版本的excel，用.xls结尾
				  wookbook = new HSSFWorkbook(fis);//得到工作簿
			  } 
			  catch (Exception ex) 
			  {
			    ex.printStackTrace();
			  }
		  }else if(sname.equals(".xlsx")){
			  try
			  {
			    //2007版本的excel，用.xls结尾
			  
			    wookbook = new XSSFWorkbook(fis);
			  } 
			  catch (Exception ex) 
			  {
			    ex.printStackTrace();
			  }
		  } 
		  //得到一个工作表
		  Sheet sheet = wookbook.getSheetAt(0);
		   
		  //获得数据的总行数
		  int totalRowNum = sheet.getLastRowNum();
		  Row title = sheet.getRow(0);
		  String WeekName;
		  String startWorkName;
		  String endWorkName;
		  try {
				  WeekName = title.getCell((short)0).getStringCellValue();
				  startWorkName = title.getCell((short)1).getStringCellValue();
				  endWorkName = title.getCell((short)2).getStringCellValue();
		  } catch (Exception e1) {
				return new BaseOutput("false", "第1行标头信息错误，必须以：周报，工作日开始日期，工作日结束日期");
		  }
		  if((!WEEK_NUM.equals(WeekName))||(!START_WORK_DAY.equals(startWorkName))||(!END_WORK_DAY.equals(endWorkName))){
			  return new BaseOutput("false", "第1行标头信息错误，必须以：周报，工作日开始日期，工作日结束日期");
		  }
		  //获得所有数据
		  for(int i = 1 ; i <= totalRowNum ; i++)
		  {
			WorkDay workDay =DTOUtils.newDTO(WorkDay.class);
		    //获得第i行对象
		    Row row = sheet.getRow(i);     
		    try {
				//获得获得第i行第0列的 String类型对象
				//		    workDay.setId(new Long((long)i));
				Cell cell = row.getCell((short) 0);
				Integer weekNum=(int) cell.getNumericCellValue();
				if(weekNum!=i){
					return new BaseOutput("false", "第"+(i+1)+"行的周数错误");
				}
				workDay.setWordDayWeek(weekNum);
				workDay.setWorkDayYear(year);
				cell = row.getCell((short) 1);
				if(!isExcelDateType(cell,year)){
					return new BaseOutput("false", "第"+(i+1)+"行的工作日开始日期格式错误或为空");
				}
				if(list!=null&&list.size()>0){
					WorkDay workDay2 = list.get(i-2);
					if(workDay2.getWorkEndTime().compareTo(cell.getDateCellValue())>=0){
						return new BaseOutput("false", "第"+(i+1)+"行的工作日开始日期小于等于第"+i+"行工作日结束日期");
					}
				}
				workDay.setWorkStartTime(cell.getDateCellValue());
				cell = row.getCell((short) 2);
				if(!isExcelDateType(cell,year)){
					return new BaseOutput("false", "第"+(i+1)+"行的工作日结束日期格式错误或为空");
				}
				if(workDay.getWorkStartTime().compareTo(cell.getDateCellValue())>=0){
					return new BaseOutput("false", "第"+(i+1)+"行的工作日开始日期大于等于工作日结束日期");
				}
				workDay.setWorkEndTime(cell.getDateCellValue());
			} catch (Exception e) {
				// TODO: handle exception
			    return new BaseOutput("false", "第"+(i+1)+"行信息类型错误");
			}
			list.add(workDay);
		  }
		  if(list!=null&&list.size()>0){
			  return new BaseOutput("true", JSONArray.toJSONString(list));
		  }else{
			  return new BaseOutput("false", "导入excel数据为空");
		  }
		}
		@Override
		public WorkDay getNextWorkDay(Date date) {
//			Date as = new Date(new Date().getTime()-24*60*60*1000);
			WorkDay selectByPrimaryKey = this.getActualDao().getWorkDayNowDate(DateUtil.getDate(date));
			WorkDay workDay=DTOUtils.newDTO(WorkDay.class);
			workDay.setWorkDayYear(selectByPrimaryKey.getWorkDayYear());
			workDay.setWordDayWeek(selectByPrimaryKey.getWordDayWeek()+1);
			WorkDay maxWeekWorkDay = this.getActualDao().getMaxOrMinWeekWorkDay(1, selectByPrimaryKey.getWorkDayYear());
			WorkDay nextWork=DTOUtils.newDTO(WorkDay.class); 
			nextWork = this.getActualDao().selectOne(workDay);
			if(maxWeekWorkDay.getId()==nextWork.getId()){
			String workDayYear = nextWork.getWorkDayYear();
			int newYear = Integer.parseInt(workDayYear)+1;
			WorkDay minWeekWorkDay = this.getActualDao().getMaxOrMinWeekWorkDay(0,String.valueOf(newYear));
				int oldWorkDaysByMillisecond = DateUtil.differentDaysByMillisecond(nextWork.getWorkStartTime(),nextWork.getWorkEndTime());
				int newWorkDaysByMillisecond = DateUtil.differentDaysByMillisecond( minWeekWorkDay.getWorkStartTime(),minWeekWorkDay.getWorkEndTime());
				if(oldWorkDaysByMillisecond<=2&&newWorkDaysByMillisecond<=2){
					nextWork.setWorkStartTime(nextWork.getWorkStartTime());
					nextWork.setWorkEndTime(minWeekWorkDay.getWorkEndTime());
				}	
			}
			return nextWork;	
			
		}
		@Override
		public BaseOutput showWorkDay(Long userId) {
			
			
			WorkDay workDayNowDate = this.getActualDao().getWorkDayNowDate(DateUtil.getDate(new Date()));
			WorkDayRoleDto workDayRoleDto=new WorkDayRoleDto();
			List<String> data = resourceRpc.listResourceCodeByUserId(userId).getData();
			if(data.contains(SET_WORK_DAY)){
				workDayRoleDto.setIsRole(1);
			}else{
				workDayRoleDto.setIsRole(0);
			}
			if(workDayNowDate==null){
				return new BaseOutput<WorkDayRoleDto>().setData(workDayRoleDto) ;
			}
			WorkDay selectOne = this.getActualDao().selectByPrimaryKey(workDayNowDate.getId()+1L);
			WorkDay maxWeekWorkDay = this.getActualDao().getMaxOrMinWeekWorkDay(1, workDayNowDate.getWorkDayYear());
			String workDayYear = workDayNowDate.getWorkDayYear();
			int newYear = Integer.parseInt(workDayYear)+1;
			WorkDay minWeekWorkDay = this.getActualDao().getMaxOrMinWeekWorkDay(0,String.valueOf(newYear));
			if(selectOne==null||selectOne.getId()==null){
				return new BaseOutput<WorkDayRoleDto>("false","需要导入工作日").setData(workDayRoleDto) ;
			}
			if(maxWeekWorkDay.getId().longValue()==selectOne.getId().longValue()&&minWeekWorkDay==null){
				return new BaseOutput<WorkDayRoleDto>("false","需要导入下一年的工作日").setData(workDayRoleDto) ;
			}
			if(maxWeekWorkDay.getId().longValue()==workDayNowDate.getId().longValue()){
				int oldWorkDaysByMillisecond = DateUtil.differentDaysByMillisecond(workDayNowDate.getWorkStartTime(),workDayNowDate.getWorkEndTime());
				int newWorkDaysByMillisecond = DateUtil.differentDaysByMillisecond( minWeekWorkDay.getWorkStartTime(),minWeekWorkDay.getWorkEndTime());
				if(oldWorkDaysByMillisecond<=2&&newWorkDaysByMillisecond<=2){
					workDayNowDate.setWorkStartTime(workDayNowDate.getWorkStartTime());
					workDayNowDate.setWorkEndTime(minWeekWorkDay.getWorkEndTime());
				}

			}
			
			workDayRoleDto.setId(workDayNowDate.getId());
			workDayRoleDto.setWorkDayYear(workDayNowDate.getWorkDayYear());
			workDayRoleDto.setWordDayWeek(workDayNowDate.getWordDayWeek());
			workDayRoleDto.setWorkStartTime(workDayNowDate.getWorkStartTime());
			workDayRoleDto.setWorkEndTime(workDayNowDate.getWorkEndTime());
			
			return  new BaseOutput<WorkDayRoleDto>().setData(workDayRoleDto) ;
		}
		public static  boolean isExcelDateType(Cell cell,String year) {
			if(cell.getCellTypeEnum()==CellType.BLANK){
				return false;
			}
			if(HSSFDateUtil.isCellDateFormatted(cell)){
				if(DateUtil.getStrYear(cell.getDateCellValue()).equals(year)){
					return true;
				}
			}
			return false;
			 
		}
}
