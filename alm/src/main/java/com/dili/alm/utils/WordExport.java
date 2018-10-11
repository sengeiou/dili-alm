package com.dili.alm.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import com.alibaba.fastjson.JSONArray;
import com.dili.alm.domain.WeeklyDetails;
import com.dili.alm.domain.WeeklyJson;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;

/**
 * Created by on 2017/1/9.
 */
public class WordExport {

	public static File getFileExecel(File file, ProjectWeeklyDto pd, List<String> projectVersion, List<TaskDto> td, List<NextWeeklyDto> wk, JSONArray weeklyRistJson, JSONArray weeklyQuestionJson,
			WeeklyDetails wDetails) throws Exception {

		FileOutputStream out = new FileOutputStream(file);
		// 创建HSSFWorkbook对象(excel的文档对象)
		HSSFWorkbook wb = new HSSFWorkbook();
		// 建立新的sheet对象（excel的表单）
		HSSFSheet sheet = wb.createSheet("周报");
		// 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
		getExeclProjectDesc(pd, wDetails, sheet);

		/*********************************** 本周进展情况 **********************************/
		getExecelThisWeekHeader(projectVersion, sheet);

		int descLength = 11;
		int length = td.size();
		getExecelThisWeekList(td, sheet, descLength);
		/*********************************** 下周工作计划 **********************************/

		getExeclNextWeekTableHeader(sheet, descLength, length);

		int nextWeekLength = descLength + length + 2;
		int nextLength = wk.size();

		getExeclNextWeekTableList(wk, sheet, nextWeekLength, nextLength);

		/*********************************** 当前重要风险 **********************************/
		int currentRistLength = nextWeekLength + nextLength + 1;
		getExcelRistHeader(sheet, currentRistLength);

		int currentRistLengthTemp = currentRistLength + 1;
		int currentRistfor = 0;

		if (weeklyRistJson != null) {

			List<WeeklyJson> ristList = weeklyRistJson.toJavaList(WeeklyJson.class);
			currentRistfor = ristList.size();
			for (int i = 0; i < currentRistfor; i++) {

				HSSFRow row15Temp = sheet.createRow(currentRistLengthTemp + 1 + i);

				row15Temp.createCell(0).setCellValue(ristList.get(i).getName());
				sheet.addMergedRegion(new CellRangeAddress(currentRistLengthTemp + 1 + i, currentRistLengthTemp + 1 + i, 0, 3));
				row15Temp.createCell(4).setCellValue(ristList.get(i).getDesc());
				sheet.addMergedRegion(new CellRangeAddress(currentRistLengthTemp + 1 + i, currentRistLengthTemp + 1 + i, 4, 7));
				row15Temp.createCell(8).setCellValue(ristList.get(i).getStatus());
				sheet.addMergedRegion(new CellRangeAddress(currentRistLengthTemp + 1 + i, currentRistLengthTemp + 1 + i, 8, 11));

			}
		}

		if (currentRistfor == 0) {
			HSSFRow row15Temp = sheet.createRow(currentRistLengthTemp + 1);
			row15Temp.createCell(0).setCellValue("");
			sheet.addMergedRegion(new CellRangeAddress(currentRistLengthTemp + 1, currentRistLengthTemp + 1, 0, 11));
			currentRistfor = 1;
		}

		/*********************************** 当前重大问题 **********************************/

		int questlength = currentRistLengthTemp + currentRistfor + 1;
		getExcelquestHeader(sheet, questlength);

		int questTempLength = questlength + 1;
		int questTemp = 0;

		if (weeklyQuestionJson != null) {
			List<WeeklyJson> questionList = weeklyQuestionJson.toJavaList(WeeklyJson.class);
			questTemp = questionList.size();
			for (int i = 0; i < questTemp; i++) {

				HSSFRow row17Temp = sheet.createRow(questTempLength + 1 + i);
				row17Temp.createCell(0).setCellValue(questionList.get(i).getName());
				sheet.addMergedRegion(new CellRangeAddress(questTempLength + 1 + i, questTempLength + 1 + i, 0, 3));
				row17Temp.createCell(4).setCellValue(questionList.get(i).getDesc());
				sheet.addMergedRegion(new CellRangeAddress(questTempLength + 1 + i, questTempLength + 1 + i, 4, 7));
				row17Temp.createCell(8).setCellValue(questionList.get(i).getStatus());
				sheet.addMergedRegion(new CellRangeAddress(questTempLength + 1 + i, questTempLength + 1 + i, 8, 11));

			}
		}
		if (questTemp == 0) {
			HSSFRow row17Temp = sheet.createRow(questTempLength + 1);
			row17Temp.createCell(0).setCellValue("");
			sheet.addMergedRegion(new CellRangeAddress(questTempLength + 1, questTempLength + 1, 0, 11));
			questTemp = 1;
		}

		execlOther(wDetails, sheet, questTempLength, questTemp);//

		int other = questTempLength + questTemp + 2 + 1;

		HSSFRow row20 = sheet.createRow(other);
		row20.createCell(0).setCellValue("");
		sheet.addMergedRegion(new CellRangeAddress(other, other, 0, 11));

		int userLength = other + 1;
		HSSFRow row21 = sheet.createRow(userLength);
		row21.createCell(0).setCellValue("创建人:");
		sheet.addMergedRegion(new CellRangeAddress(userLength, userLength, 0, 1));
		row21.createCell(2).setCellValue(pd.getUserName());
		sheet.addMergedRegion(new CellRangeAddress(userLength, userLength, 2, 3));

		row21.createCell(4).setCellValue("创建时间  : ");
		sheet.addMergedRegion(new CellRangeAddress(userLength, userLength, 4, 5));

		row21.createCell(6).setCellValue(pd.getCreated());
		sheet.addMergedRegion(new CellRangeAddress(userLength, userLength, 6, 11));

		out.flush();
		wb.write(out);

		out.close();
		return file;
	}

	private static void getExcelquestHeader(HSSFSheet sheet, int questlength) {
		HSSFRow row16 = sheet.createRow(questlength);
		row16.createCell(0).setCellValue("当前重要问题");
		sheet.addMergedRegion(new CellRangeAddress(questlength, questlength, 0, 11));

		HSSFRow row17 = sheet.createRow(questlength + 1);
		row17.createCell(0).setCellValue("风险名称");
		sheet.addMergedRegion(new CellRangeAddress(questlength + 1, questlength + 1, 0, 3));
		row17.createCell(4).setCellValue("跟踪简述");
		sheet.addMergedRegion(new CellRangeAddress(questlength + 1, questlength + 1, 4, 7));
		row17.createCell(8).setCellValue("当前状态");
		sheet.addMergedRegion(new CellRangeAddress(questlength + 1, questlength + 1, 8, 11));
	}

	private static void getExcelRistHeader(HSSFSheet sheet, int currentRistLength) {
		HSSFRow row14 = sheet.createRow(currentRistLength);
		row14.createCell(0).setCellValue("当前重要风险");
		sheet.addMergedRegion(new CellRangeAddress(currentRistLength, currentRistLength, 0, 11));

		HSSFRow row15 = sheet.createRow(currentRistLength + 1);
		row15.createCell(0).setCellValue("风险名称");
		sheet.addMergedRegion(new CellRangeAddress(currentRistLength + 1, currentRistLength + 1, 0, 3));
		row15.createCell(4).setCellValue("跟踪简述");
		sheet.addMergedRegion(new CellRangeAddress(currentRistLength + 1, currentRistLength + 1, 4, 7));
		row15.createCell(8).setCellValue("当前状态");
		sheet.addMergedRegion(new CellRangeAddress(currentRistLength + 1, currentRistLength + 1, 8, 11));
	}

	private static void getExeclNextWeekTableList(List<NextWeeklyDto> wk, HSSFSheet sheet, int nextWeekLength, int nextLength) {
		for (int i = 0; i < nextLength; i++) {

			HSSFRow row13Temp = sheet.createRow(nextWeekLength + 1 + i);
			row13Temp.createCell(0).setCellValue(wk.get(i).getNumber() + "");
			row13Temp.createCell(1).setCellValue(wk.get(i).getName() + "");
			sheet.addMergedRegion(new CellRangeAddress(nextWeekLength + i + 1, nextWeekLength + i + 1, 1, 2));
			row13Temp.createCell(3).setCellValue(wk.get(i).getOwner());
			sheet.addMergedRegion(new CellRangeAddress(nextWeekLength + i + 1, nextWeekLength + i + 1, 3, 4));
			row13Temp.createCell(5).setCellValue(wk.get(i).getPlanTime());
			sheet.addMergedRegion(new CellRangeAddress(nextWeekLength + i + 1, nextWeekLength + i + 1, 5, 6));
			row13Temp.createCell(7).setCellValue(wk.get(i).getSurplus());
			sheet.addMergedRegion(new CellRangeAddress(nextWeekLength + i + 1, nextWeekLength + i + 1, 7, 8));
			row13Temp.createCell(9).setCellValue(wk.get(i).getEndDate());
			sheet.addMergedRegion(new CellRangeAddress(nextWeekLength + i + 1, nextWeekLength + i + 1, 9, 10));
			row13Temp.createCell(11).setCellValue(wk.get(i).getDescribe());

		}
	}

	private static void getExeclNextWeekTableHeader(HSSFSheet sheet, int descLength, int length) {
		HSSFRow row11 = sheet.createRow(descLength + length);

		row11.createCell(0).setCellValue("下周工作计划");
		sheet.addMergedRegion(new CellRangeAddress(descLength + length, descLength + length, 0, 11));

		HSSFRow row12 = sheet.createRow(descLength + length + 1);
		row12.createCell(5).setCellValue("需求确认阶段");
		sheet.addMergedRegion(new CellRangeAddress(descLength + length + 1, descLength + length + 1, 5, 11));
		/***********/

		HSSFRow row13 = sheet.createRow(descLength + length + 1 + 1);
		row13.createCell(0).setCellValue("序号");
		row13.createCell(1).setCellValue("任务名称");
		sheet.addMergedRegion(new CellRangeAddress(descLength + length + 1 + 1, descLength + length + 1 + 1, 1, 2));
		row13.createCell(3).setCellValue("责任人");
		sheet.addMergedRegion(new CellRangeAddress(descLength + length + 1 + 1, descLength + length + 1 + 1, 3, 4));
		row13.createCell(5).setCellValue("下周计划工时  ");
		sheet.addMergedRegion(new CellRangeAddress(descLength + length + 1 + 1, descLength + length + 1 + 1, 5, 6));
		row13.createCell(7).setCellValue("剩余计划工时");
		sheet.addMergedRegion(new CellRangeAddress(descLength + length + 1 + 1, descLength + length + 1 + 1, 7, 8));
		row13.createCell(9).setCellValue("计划完成日期");
		sheet.addMergedRegion(new CellRangeAddress(descLength + length + 1 + 1, descLength + length + 1 + 1, 9, 10));
		row13.createCell(11).setCellValue("备注");
		// sheet.addMergedRegion(new
		// CellRangeAddress(descLength+length+1+1,descLength+length+1+1,10,11));
	}

	private static void getExecelThisWeekList(List<TaskDto> td, HSSFSheet sheet, int descLength) {
		for (int i = 0; i < td.size(); i++) {
			HSSFRow row10Temp = sheet.createRow(descLength + i);

			row10Temp.createCell(0).setCellValue(td.get(i).getNumber() + "");
			row10Temp.createCell(1).setCellValue(td.get(i).getName() + "");
			row10Temp.createCell(2).setCellValue(td.get(i).getVersionId());
			row10Temp.createCell(4).setCellValue(td.get(i).getOwner() + "");
			row10Temp.createCell(5).setCellValue(td.get(i).getStatus() + "");

			row10Temp.createCell(6).setCellValue(td.get(i).getEndDateStr());
			row10Temp.createCell(7).setCellValue(td.get(i).getPlanTime() + "");
			row10Temp.createCell(8).setCellValue(td.get(i).getWeekHour());
			row10Temp.createCell(9).setCellValue(td.get(i).getRealHour());
			row10Temp.createCell(10).setCellValue(td.get(i).getFackEndDate());

		}
	}

	private static void getExecelThisWeekHeader(List<String> projectVersion, HSSFSheet sheet) {
		HSSFRow row8 = sheet.createRow(8);
		row8.createCell(0).setCellValue("本周进展情况");
		sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 11));

		HSSFRow row9 = sheet.createRow(9);
		row9.createCell(0).setCellValue("本周项目版本:");
		sheet.addMergedRegion(new CellRangeAddress(9, 9, 0, 2));

		row9.createCell(3).setCellValue(StringUtils.join(projectVersion.toArray(), ","));
		sheet.addMergedRegion(new CellRangeAddress(9, 9, 3, 5));

		sheet.addMergedRegion(new CellRangeAddress(9, 9, 9, 11));
		/***********/
		HSSFRow row10 = sheet.createRow(10);
		row10.createCell(0).setCellValue("序号");
		row10.createCell(1).setCellValue("任务名称");
		row10.createCell(2).setCellValue("版本");
		row10.createCell(4).setCellValue("责任人");
		row10.createCell(5).setCellValue("是否完成");
		row10.createCell(6).setCellValue("计划完成日期");
		row10.createCell(7).setCellValue("任务计划工时");
		row10.createCell(8).setCellValue("本周实际工时");
		row10.createCell(9).setCellValue("实际总工时");
		row10.createCell(10).setCellValue("实际完成日期");
	}

	private static void getExeclProjectDesc(ProjectWeeklyDto pd, WeeklyDetails wDetails, HSSFSheet sheet) {
		HSSFRow row1 = sheet.createRow(0);
		// 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
		HSSFCell cell = row1.createCell(0);
		// 设置单元格内容
		cell.setCellValue("项目周报");
		// 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
		// int firstRow, int lastRow, int firstCol, int lastCol
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 11));
		// 在sheet里创建第二行
		HSSFRow row2 = sheet.createRow(2);
		row2.createCell(0).setCellValue("项目名称:");
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));
		// 创建单元格并设置单元格内容

		row2.createCell(2).setCellValue(pd.getProjectName());
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 3));

		row2.createCell(4).setCellValue("项目编号:");
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 5));

		row2.createCell(6).setCellValue(pd.getSerialNumber());
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 6, 7));

		row2.createCell(8).setCellValue("项目经理");
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 8, 9));

		row2.createCell(10).setCellValue(pd.getUserName());
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 10, 11));

		// 在sheet里创建第三行
		HSSFRow row3 = sheet.createRow(3);
		row3.createCell(0).setCellValue("项目类型:");
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
		// 创建单元格并设置单元格内容
		row3.createCell(2).setCellValue(pd.getProjectType());
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 3));
		row3.createCell(4).setCellValue("项目所在部门:");
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 4, 5));
		row3.createCell(6).setCellValue(pd.getProjectInDept());
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 6, 7));
		row3.createCell(8).setCellValue("业务方：");
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 8, 9));
		row3.createCell(10).setCellValue(pd.getBusinessParty());
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 10, 11));

		// 在sheet里创建第三行
		HSSFRow row4 = sheet.createRow(4);
		row4.createCell(0).setCellValue("计划上线日期:");
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 1));
		// 创建单元格并设置单元格内容
		row4.createCell(2).setCellValue(pd.getPlanDate());
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 2, 3));
		row4.createCell(4).setCellValue("本周起止日期:");
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 5));
		row4.createCell(6).setCellValue(pd.getBeginAndEndTime());
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 6, 7));
		row4.createCell(8).setCellValue("总体进展：");
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 8, 9));

		if (Integer.parseInt(pd.getCompletedProgress()) > 100) {
			pd.setCompletedProgress("100");
		}
		row4.createCell(10).setCellValue(pd.getCompletedProgress() + "%");
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 10, 11));

		HSSFRow row5 = sheet.createRow(5);
		row5.createCell(0).setCellValue("项目总体情况描述");
		sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 11));

		HSSFRow row6 = sheet.createRow(6);
		row6.createCell(0).setCellValue(wDetails != null ? wDetails.getProjectDescription() : "");
		sheet.addMergedRegion(new CellRangeAddress(6, 7, 0, 11));
	}

	private static void execlOther(WeeklyDetails wDetails, HSSFSheet sheet, int questTempLength, int questTemp) {
		HSSFRow row18 = sheet.createRow(questTempLength + questTemp + 1);
		row18.createCell(0).setCellValue("预期偏差");
		sheet.addMergedRegion(new CellRangeAddress(questTempLength + questTemp + 1, questTempLength + questTemp + 1, 0, 3));
		row18.createCell(4).setCellValue(wDetails != null ? wDetails.getExpectedDeviation() : "");
		sheet.addMergedRegion(new CellRangeAddress(questTempLength + questTemp + 1, questTempLength + questTemp + 1, 4, 11));

		HSSFRow row19 = sheet.createRow(questTempLength + questTemp + 2);
		row19.createCell(0).setCellValue("其他");
		sheet.addMergedRegion(new CellRangeAddress(questTempLength + questTemp + 2, questTempLength + questTemp + 2, 0, 3));
		row19.createCell(4).setCellValue(wDetails != null ? wDetails.getOther() : "");
		sheet.addMergedRegion(new CellRangeAddress(questTempLength + questTemp + 2, questTempLength + questTemp + 2, 4, 11));
	}

	public static File getFile(File file, ProjectWeeklyDto pd, List<String> projectVersion, List<TaskDto> td, List<NextWeeklyDto> wk, JSONArray weeklyRistJson, JSONArray weeklyQuestionJson,
			WeeklyDetails wDetails

	) throws Exception {

		FileOutputStream out = new FileOutputStream(file);

		// Blank Document
		XWPFDocument document = new XWPFDocument();
		// Write the Document in file system

		// 添加标题
		XWPFParagraph titleParagraph = document.createParagraph();
		// 设置段落居中
		titleParagraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun titleParagraphRun = titleParagraph.createRun();
		titleParagraphRun.setText("项目周报");
		titleParagraphRun.setColor("000000");
		titleParagraphRun.setFontSize(20);

		// 两个表格之间加个换行
		XWPFParagraph paragraph = document.createParagraph();
		XWPFRun paragraphRun = paragraph.createRun();
		paragraphRun.setText("\r");

		getProjectDesc(pd, wDetails, document);// 周报详情

		document.createParagraph().createRun().setText("\r");

		document.createParagraph().createRun().setText("\r");

		document.createParagraph().createRun().setText("\r");
		getRistTable(weeklyRistJson, document);// 当前重要风险

		document.createParagraph().createRun().setText("\r");
		getQuestionTable(weeklyQuestionJson, document);// 当前重要问题

		document.createParagraph().createRun().setText("\r");

		getTableOther(wDetails, document);// 预期偏差和其他

		document.createParagraph().createRun().setText("\r");
		XWPFTable personTable = document.createTable();
		personTable.getCTTbl().getTblPr().unsetTblBorders();

		// 列宽自动分割
		CTTblWidth personTableWidth = personTable.getCTTbl().addNewTblPr().addNewTblW();
		personTableWidth.setType(STTblWidth.DXA);
		personTableWidth.setW(BigInteger.valueOf(9072));

		XWPFTableRow personTableRowone = personTable.getRow(0);
		personTableRowone.addNewTableCell().setText("创建人：");
		personTableRowone.addNewTableCell().setText(pd.getUserName());
		personTableRowone.addNewTableCell();
		personTableRowone.addNewTableCell();
		personTableRowone.addNewTableCell().setText("创建时间  :   ");
		personTableRowone.addNewTableCell().setText(pd.getCreated());

		CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
		XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
		// 添加页眉
		CTP ctpHeader = CTP.Factory.newInstance();
		CTR ctrHeader = ctpHeader.addNewR();
		CTText ctHeader = ctrHeader.addNewT();
		String headerText = "集团文件";
		ctHeader.setStringValue(headerText);
		XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
		// 设置为右对齐
		headerParagraph.setAlignment(ParagraphAlignment.RIGHT);
		XWPFParagraph[] parsHeader = new XWPFParagraph[1];
		parsHeader[0] = headerParagraph;
		policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);

		// 添加页脚
		CTP ctpFooter = CTP.Factory.newInstance();
		CTR ctrFooter = ctpFooter.addNewR();
		CTText ctFooter = ctrFooter.addNewT();
		String footerText = "北京研发提供";
		ctFooter.setStringValue(footerText);
		XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooter, document);
		headerParagraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFParagraph[] parsFooter = new XWPFParagraph[1];
		parsFooter[0] = footerParagraph;
		policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, parsFooter);

		document.write(out);
		out.flush();
		out.close();
		return file;
	}

	private static void getTableOther(WeeklyDetails wDetails, XWPFDocument document) {
		XWPFTable tableOther = document.createTable();
		// 列宽自动分割
		CTTblWidth tableOtherTableWidth = tableOther.getCTTbl().addNewTblPr().addNewTblW();
		tableOtherTableWidth.setType(STTblWidth.DXA);
		tableOtherTableWidth.setW(BigInteger.valueOf(9072));

		XWPFTableRow otherTableRow = tableOther.getRow(0);
		;
		otherTableRow.getCell(0).setText("预期偏差");
		otherTableRow.addNewTableCell().setText(wDetails != null ? wDetails.getExpectedDeviation() : "");
		otherTableRow.addNewTableCell();
		mergeCellsHorizontal(tableOther, 0, 1, 2);
		XWPFTableRow otherTableRowone = tableOther.createRow();
		otherTableRowone.getCell(0).setText("其他");
		otherTableRowone.getCell(1).setText(wDetails != null ? wDetails.getOther() : "");
		otherTableRowone.getCell(2);
		mergeCellsHorizontal(tableOther, 1, 1, 2);
	}

	private static void getQuestionTable(JSONArray weeklyQuestionJson, XWPFDocument document) {
		// 当前重大问题
		XWPFTable questionTable = document.createTable();
		// 列宽自动分割
		CTTblWidth questionTableWidth = questionTable.getCTTbl().addNewTblPr().addNewTblW();
		questionTableWidth.setType(STTblWidth.DXA);
		questionTableWidth.setW(BigInteger.valueOf(9072));

		XWPFTableRow questionTableRowone = questionTable.getRow(0);
		questionTableRowone.addNewTableCell();
		questionTableRowone.addNewTableCell();
		mergeCellsHorizontal(questionTable, 0, 0, 2);
		getParagraph(questionTableRowone.getCell(0), "当前重大问题");

		XWPFTableRow questionTableRowTwo = questionTable.createRow();
		questionTableRowTwo.getCell(0).setText("问题名称");
		questionTableRowTwo.getCell(1).setText("跟踪简述");
		questionTableRowTwo.getCell(2).setText("当前状态");

		if (weeklyQuestionJson != null) {
			List<WeeklyJson> questionList = weeklyQuestionJson.toJavaList(WeeklyJson.class);
			XWPFTableRow questionTabledd;
			for (int i = 0; i < questionList.size(); i++) {
				questionTabledd = questionTable.createRow();
				questionTabledd.getCell(0).setText(questionList.get(i).getName());
				questionTabledd.getCell(1).setText(questionList.get(i).getDesc());
				questionTabledd.getCell(2).setText(questionList.get(i).getStatus());
			}
		}
	}

	private static void getRistTable(JSONArray weeklyRistJson, XWPFDocument document) {
		// 当前重要风险
		XWPFTable ristTable = document.createTable();
		// 列宽自动分割
		CTTblWidth ristTableWidth = ristTable.getCTTbl().addNewTblPr().addNewTblW();
		ristTableWidth.setType(STTblWidth.DXA);
		ristTableWidth.setW(BigInteger.valueOf(9072));

		XWPFTableRow ristTableRowone = ristTable.getRow(0);
		ristTableRowone.addNewTableCell();
		ristTableRowone.addNewTableCell();
		mergeCellsHorizontal(ristTable, 0, 0, 2);
		getParagraph(ristTableRowone.getCell(0), "当前重要风险");

		XWPFTableRow ristTableRowTwo = ristTable.createRow();
		ristTableRowTwo.getCell(0).setText("风险名称");
		ristTableRowTwo.getCell(1).setText("跟踪简述");
		ristTableRowTwo.getCell(2).setText("当前状态");

		if (weeklyRistJson != null) {
			List<WeeklyJson> ristList = weeklyRistJson.toJavaList(WeeklyJson.class);
			XWPFTableRow ristTabledd;
			for (int i = 0; i < ristList.size(); i++) {
				ristTabledd = ristTable.createRow();

				ristTabledd.getCell(0).setText(ristList.get(i).getName());
				ristTabledd.getCell(1).setText(ristList.get(i).getDesc());
				ristTabledd.getCell(2).setText(ristList.get(i).getStatus());
			}
		}

	}

	private static void getNextWeekTable(List<NextWeeklyDto> wk, XWPFDocument document) {
		XWPFTable nextWeekTable = document.createTable();
		// 列宽自动分割
		CTTblWidth nextWeekTableWidth = nextWeekTable.getCTTbl().addNewTblPr().addNewTblW();
		nextWeekTableWidth.setType(STTblWidth.DXA);
		nextWeekTableWidth.setW(BigInteger.valueOf(9072));

		XWPFTableRow nextWeekTableRowone = nextWeekTable.getRow(0);
		// thisWeekTableRowone.getCell(0).setText("本周进展情况");
		nextWeekTableRowone.addNewTableCell();
		nextWeekTableRowone.addNewTableCell();
		nextWeekTableRowone.addNewTableCell();
		nextWeekTableRowone.addNewTableCell();
		nextWeekTableRowone.addNewTableCell();
		nextWeekTableRowone.addNewTableCell();

		mergeCellsHorizontal(nextWeekTable, 0, 0, 5);
		getParagraph(nextWeekTableRowone.getCell(0), "下周工作计划");
		// 表格第二行

		XWPFTableRow nextWeekTTableRowTwo = nextWeekTable.createRow();

		XWPFTableRow nextTableTHREE = nextWeekTable.createRow();
		nextTableTHREE.getCell(0).setText("序号");
		nextTableTHREE.getCell(1).setText("任务名称");
		nextTableTHREE.getCell(2).setText("责任人");
		nextTableTHREE.getCell(3).setText("任务计划工时");
		nextTableTHREE.getCell(4).setText("剩余计划工时");
		nextTableTHREE.getCell(5).setText("计划完成日期");
		nextTableTHREE.getCell(6).setText("备注");
		XWPFTableRow nextTabledd;
		for (int i = 0; i < wk.size(); i++) {
			nextTabledd = nextWeekTable.createRow();

			nextTabledd.getCell(0).setText(wk.get(i).getNumber() + "");
			nextTabledd.getCell(1).setText(wk.get(i).getName() + "");
			nextTabledd.getCell(2).setText(wk.get(i).getOwner());
			nextTabledd.getCell(3).setText(wk.get(i).getPlanTime());
			nextTabledd.getCell(4).setText(wk.get(i).getSurplus());
			nextTabledd.getCell(5).setText(wk.get(i).getEndDate());
			nextTabledd.getCell(6).setText(wk.get(i).getDescribe());

		}
	}

	private static void getThisWeek(List<String> projectVersion, List<TaskDto> td, XWPFDocument document) {
		// 本周进展情况

		XWPFTable thisWeekTable = document.createTable();
		// 列宽自动分割
		CTTblWidth thisTableWidth = thisWeekTable.getCTTbl().addNewTblPr().addNewTblW();
		thisTableWidth.setType(STTblWidth.DXA);
		thisTableWidth.setW(BigInteger.valueOf(9072));

		XWPFTableRow thisWeekTableRowone = thisWeekTable.getRow(0);
		thisWeekTableRowone.addNewTableCell();
		thisWeekTableRowone.addNewTableCell();
		thisWeekTableRowone.addNewTableCell();
		thisWeekTableRowone.addNewTableCell();
		thisWeekTableRowone.addNewTableCell();
		thisWeekTableRowone.addNewTableCell();
		thisWeekTableRowone.addNewTableCell();
		thisWeekTableRowone.addNewTableCell();
		thisWeekTableRowone.addNewTableCell();
		thisWeekTableRowone.addNewTableCell();

		mergeCellsHorizontal(thisWeekTable, 0, 0, 10);
		getParagraph(thisWeekTableRowone.getCell(0), "本周进展情况");
		// 表格第二行
		XWPFTableRow thisWeekTTableRowTwo = thisWeekTable.createRow();
		thisWeekTTableRowTwo.getCell(0).setText("本周项目版本");
		mergeCellsHorizontal(thisWeekTable, 1, 0, 1);
		thisWeekTTableRowTwo.getCell(2).setText(StringUtils.join(projectVersion.toArray(), ","));
		mergeCellsHorizontal(thisWeekTable, 1, 2, 4);

		XWPFTableRow thisTableTHREE = thisWeekTable.createRow();
		thisTableTHREE.getCell(0).setText("序号");
		thisTableTHREE.getCell(1).setText("任务名称");
		thisTableTHREE.getCell(2).setText("版本");
		thisTableTHREE.getCell(3).setText("责任人");
		thisTableTHREE.getCell(4).setText("是否完成");
		thisTableTHREE.getCell(5).setText("计划完成日期");
		thisTableTHREE.getCell(6).setText("任务计划工时");
		thisTableTHREE.getCell(7).setText("本周实际工时");
		thisTableTHREE.getCell(8).setText("实际总工时");
		thisTableTHREE.getCell(9).setText("实际完成日期");

		XWPFTableRow thisTabledd;
		for (int i = 0; i < td.size(); i++) {
			thisTabledd = thisWeekTable.createRow();

			thisTabledd.getCell(0).setText(td.get(i).getNumber() + "");
			thisTabledd.getCell(1).setText(td.get(i).getName() + "");
			thisTabledd.getCell(2).setText(td.get(i).getVersionId());
			thisTabledd.getCell(4).setText(td.get(i).getOwner() + "");
			thisTabledd.getCell(5).setText(td.get(i).getStatus() + "");

			thisTabledd.getCell(6).setText(td.get(i).getEndDateStr());
			thisTabledd.getCell(7).setText(td.get(i).getPlanTime() + "");
			thisTabledd.getCell(8).setText(td.get(i).getWeekHour());
			thisTabledd.getCell(9).setText(td.get(i).getRealHour());
			thisTabledd.getCell(10).setText(td.get(i).getFackEndDate());
		}
	}

	private static void getProjectDesc(ProjectWeeklyDto pd, WeeklyDetails wDetails, XWPFDocument document) {
		// 工作经历表格
		XWPFTable ComTable = document.createTable();

		// 列宽自动分割
		CTTblWidth comTableWidth = ComTable.getCTTbl().addNewTblPr().addNewTblW();
		comTableWidth.setType(STTblWidth.DXA);
		comTableWidth.setW(BigInteger.valueOf(9072));

		// 表格第一行
		XWPFTableRow comTableRowOne = ComTable.getRow(0);
		comTableRowOne.getCell(0).setText("项目名称");
		comTableRowOne.addNewTableCell().setText(pd.getProjectName());
		comTableRowOne.addNewTableCell().setText("项目编号");
		comTableRowOne.addNewTableCell().setText(pd.getSerialNumber());
		comTableRowOne.addNewTableCell().setText("项目经理");
		comTableRowOne.addNewTableCell().setText(pd.getUserName());

		// 表格第二行
		XWPFTableRow comTableRowTwo = ComTable.createRow();
		comTableRowTwo.getCell(0).setText("项目类型");
		comTableRowTwo.getCell(1).setText(pd.getProjectType());
		comTableRowTwo.getCell(2).setText("项目所在部门");
		comTableRowTwo.getCell(3).setText(pd.getProjectInDept());
		comTableRowTwo.getCell(4).setText("业务方");
		comTableRowTwo.getCell(5).setText(pd.getBusinessParty());
		// 表格第三行
		XWPFTableRow comTableRowThree = ComTable.createRow();
		comTableRowThree.getCell(0).setText("计划上线日期");
		comTableRowThree.getCell(1).setText(pd.getPlanDate());
		comTableRowThree.getCell(2).setText("本周起止日期");
		comTableRowThree.getCell(3).setText(pd.getBeginAndEndTime());
		comTableRowThree.getCell(4).setText("总体进展");
		// if(Integer.parseInt(pd.getCompletedProgress())<8){
		if (Integer.parseInt(pd.getCompletedProgress()) > 100) {
			pd.setCompletedProgress("100");
		}
		comTableRowThree.getCell(5).setText(pd.getCompletedProgress() + "%");
		// } else if(Integer.parseInt(pd.getCompletedProgress())>15){
		// comTableRowThree.getCell(5).setText("警戒--偏差>15%");
		// }else{
		// comTableRowThree.getCell(5).setText("预警--8%<偏差<15%");
		// }

		XWPFTableRow comTableRowFive = ComTable.createRow();
		getParagraph(comTableRowFive.getCell(0), "项目总体情况描述");

		XWPFTableRow comTableRowsix = ComTable.createRow();
		getParagraph(comTableRowsix.getCell(0), wDetails != null ? wDetails.getProjectDescription() : "");

		mergeCellsHorizontal(ComTable, 3, 0, 5);
		mergeCellsHorizontal(ComTable, 4, 0, 5);
	}

	public static void fillTable(XWPFTable table) {
		for (int rowIndex = 0; rowIndex < table.getNumberOfRows(); rowIndex++) {
			XWPFTableRow row = table.getRow(rowIndex);
			row.setHeight(380);
			for (int colIndex = 0; colIndex < row.getTableCells().size(); colIndex++) {
				XWPFTableCell cell = row.getCell(colIndex);
				if (rowIndex % 2 == 0) {
					setCellText(cell, " cell " + rowIndex + colIndex + " ", "D4DBED", 1000);
				} else {
					setCellText(cell, " cell " + rowIndex + colIndex + " ", "AEDE72", 1000);
				}
			}
		}
	}

	public static void setCellText(XWPFTableCell cell, String text, String bgcolor, int width) {
		CTTc cttc = cell.getCTTc();
		CTTcPr cellPr = cttc.addNewTcPr();
		cellPr.addNewTcW().setW(BigInteger.valueOf(width));
		// cell.setColor(bgcolor);
		CTTcPr ctPr = cttc.addNewTcPr();
		CTShd ctshd = ctPr.addNewShd();
		ctshd.setFill(bgcolor);
		ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
		cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
		cell.setText(text);
	}

	/**
	 * @Description: 跨列合并
	 */
	public static void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell) {
		for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
			XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
			if (cellIndex == fromCell) {
				// The first merged cell is set with RESTART merge value
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
			} else {
				// Cells which join (merge) the first one, are set with CONTINUE
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
			}
		}
	}

	/**
	 * @Description: 跨行合并
	 * @see http://stackoverflow.com/questions/24907541/row-span-with-xwpftable
	 */
	public static void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
			if (rowIndex == fromRow) {
				// The first merged cell is set with RESTART merge value
				cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
			} else {
				// Cells which join (merge) the first one, are set with CONTINUE
				cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
			}
		}
	}

	public static void setTableWidth(XWPFTable table, String width) {
		CTTbl ttbl = table.getCTTbl();
		CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl.getTblPr();
		CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
		CTJc cTJc = tblPr.addNewJc();
		cTJc.setVal(STJc.Enum.forString("center"));
		tblWidth.setW(new BigInteger(width));
		tblWidth.setType(STTblWidth.DXA);
	}

	private static void getParagraph(XWPFTableCell cell, String cellText) {
		CTP ctp = CTP.Factory.newInstance();
		XWPFParagraph p = new XWPFParagraph(ctp, cell);
		p.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = p.createRun();
		run.setText(cellText);
		CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
		CTFonts fonts = rpr.isSetRFonts() ? rpr.getRFonts() : rpr.addNewRFonts();
		fonts.setAscii("仿宋");
		fonts.setEastAsia("仿宋");
		fonts.setHAnsi("仿宋");
		cell.setParagraph(p);
	}
}
