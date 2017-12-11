package com.dili.alm.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

	public static File getFile(File file,ProjectWeeklyDto pd, List<String> projectVersion, List<String> projectPhase, List<String> nextprojectPhase, List<TaskDto> td, List<NextWeeklyDto> wk,
			JSONArray weeklyRistJson, JSONArray weeklyQuestionJson, WeeklyDetails wDetails

	    ) throws Exception {
		
		FileOutputStream  out=new FileOutputStream(file);
		
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

		getProjectDesc(pd, wDetails, document);//周报详情

		document.createParagraph().createRun().setText("\r");

		getThisWeek(projectVersion, projectPhase, td, document);//// 本周进展情况

		document.createParagraph().createRun().setText("\r");

		getNextWeekTable(nextprojectPhase, wk, document);//下周工作计划

		document.createParagraph().createRun().setText("\r");
		getRistTable(weeklyRistJson, document);//当前重要风险

		document.createParagraph().createRun().setText("\r");
		getQuestionTable(weeklyQuestionJson, document);//当前重要问题

		document.createParagraph().createRun().setText("\r");

		getTableOther(wDetails, document);//预期偏差和其他

		
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
		personTableRowone.addNewTableCell().setText("新建阶段人：");
		personTableRowone.addNewTableCell().setText(pd.getStageMan());
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
		out.close();
		return  file;
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
		otherTableRow.addNewTableCell().setText(wDetails!=null?wDetails.getExpectedDeviation():"");
		otherTableRow.addNewTableCell();
		mergeCellsHorizontal(tableOther, 0, 1, 2);
		XWPFTableRow otherTableRowone = tableOther.createRow();
		otherTableRowone.getCell(0).setText("其他");
		otherTableRowone.getCell(1).setText(wDetails!=null?wDetails.getOther():"");
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

		List<WeeklyJson> questionList = weeklyQuestionJson.toJavaList(WeeklyJson.class);
		XWPFTableRow questionTabledd;
		for (int i = 0; i < questionList.size(); i++) {
			questionTabledd = questionTable.createRow();
			questionTabledd.getCell(0).setText(questionList.get(i).getName());
			questionTabledd.getCell(1).setText(questionList.get(i).getDesc());
			questionTabledd.getCell(2).setText(questionList.get(i).getStatus());
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

		List<WeeklyJson> ristList = weeklyRistJson.toJavaList(WeeklyJson.class);
		XWPFTableRow ristTabledd;
		for (int i = 0; i < ristList.size(); i++) {
			ristTabledd = ristTable.createRow();

			ristTabledd.getCell(0).setText(ristList.get(i).getName());
			ristTabledd.getCell(1).setText(ristList.get(i).getDesc());
			ristTabledd.getCell(2).setText(ristList.get(i).getStatus());
		}
	}

	private static void getNextWeekTable(List<String> nextprojectPhase, List<NextWeeklyDto> wk, XWPFDocument document) {
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

		mergeCellsHorizontal(nextWeekTable, 0, 0, 5);
		getParagraph(nextWeekTableRowone.getCell(0), "下周工作计划");
		// 表格第二行

		XWPFTableRow nextWeekTTableRowTwo = nextWeekTable.createRow();
		nextWeekTTableRowTwo.getCell(0).setText("下周所处阶段");
		mergeCellsHorizontal(nextWeekTable, 1, 0, 1);
		nextWeekTTableRowTwo.getCell(2).setText(StringUtils.join(nextprojectPhase.toArray(), ","));
		mergeCellsHorizontal(nextWeekTable, 1, 2, 5);

		XWPFTableRow nextTableTHREE = nextWeekTable.createRow();
		nextTableTHREE.getCell(0).setText("序号");
		nextTableTHREE.getCell(1).setText("任务名称");
		nextTableTHREE.getCell(2).setText("责任人");
		nextTableTHREE.getCell(3).setText("下周计划工时");
		nextTableTHREE.getCell(4).setText("计划完成日期");
		nextTableTHREE.getCell(5).setText("备注");

		XWPFTableRow nextTabledd;
		for (int i = 0; i < wk.size(); i++) {
			nextTabledd = nextWeekTable.createRow();

			nextTabledd.getCell(0).setText(wk.get(i).getNumber() + "");
			nextTabledd.getCell(1).setText(wk.get(i).getName() + "");
			nextTabledd.getCell(2).setText(wk.get(i).getOwner());
			nextTabledd.getCell(3).setText(wk.get(i).getPlanTime());
			nextTabledd.getCell(4).setText(wk.get(i).getEndDate());
			nextTabledd.getCell(5).setText(wk.get(i).getDescribe());

		}
	}

	private static void getThisWeek(List<String> projectVersion, List<String> projectPhase, List<TaskDto> td, XWPFDocument document) {
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
		thisWeekTTableRowTwo.getCell(5).setText("本周项目阶段");
		mergeCellsHorizontal(thisWeekTable, 1, 5, 7);
		thisWeekTTableRowTwo.getCell(8).setText(StringUtils.join(projectPhase.toArray(), ","));
		mergeCellsHorizontal(thisWeekTable, 1, 8, 10);

		XWPFTableRow thisTableTHREE = thisWeekTable.createRow();
		thisTableTHREE.getCell(0).setText("序号");
		thisTableTHREE.getCell(1).setText("任务名称");
		thisTableTHREE.getCell(2).setText("版本");
		thisTableTHREE.getCell(3).setText("阶段");
		thisTableTHREE.getCell(4).setText("责任人");
		thisTableTHREE.getCell(5).setText("完成情况");
		thisTableTHREE.getCell(6).setText("本周工时");
		thisTableTHREE.getCell(7).setText("预计工时");
		thisTableTHREE.getCell(8).setText("实际工时");
		thisTableTHREE.getCell(9).setText("工时偏差%");
		thisTableTHREE.getCell(10).setText("备注");

		XWPFTableRow thisTabledd;
		for (int i = 0; i < td.size(); i++) {
			thisTabledd = thisWeekTable.createRow();

			thisTabledd.getCell(0).setText(td.get(i).getNumber() + "");
			thisTabledd.getCell(1).setText(td.get(i).getName() + "");
			thisTabledd.getCell(2).setText(td.get(i).getVersionId());
			thisTabledd.getCell(3).setText(td.get(i).getPhaseId());
			thisTabledd.getCell(4).setText(td.get(i).getOwner() + "");
			thisTabledd.getCell(5).setText(td.get(i).getStatus() + "");
			thisTabledd.getCell(6).setText(td.get(i).getPlanTime() + "");
			thisTabledd.getCell(7).setText(td.get(i).getWeekHour());
			thisTabledd.getCell(8).setText(td.get(i).getRealHour());
			thisTabledd.getCell(9).setText(td.get(i).getHourDeviation());
			thisTabledd.getCell(10).setText(td.get(i).getDescribe());
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
		  if(Integer.parseInt(pd.getCompletedProgress())<8){
			  comTableRowThree.getCell(5).setText("正常--偏差<8%");
		  } else if(Integer.parseInt(pd.getCompletedProgress())>15){
			  comTableRowThree.getCell(5).setText("警戒--偏差>15%");
		  }else{
			  comTableRowThree.getCell(5).setText("预警--8%<偏差<15%");
		  }
		

		XWPFTableRow comTableRowFive = ComTable.createRow();
		getParagraph(comTableRowFive.getCell(0), "项目总体情况描述");

		XWPFTableRow comTableRowsix = ComTable.createRow();
		getParagraph(comTableRowsix.getCell(0), wDetails!=null?wDetails.getProjectDescription():"");

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
