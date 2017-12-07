package com.dili.alm.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;  
import org.apache.poi.xwpf.usermodel.*;  
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;  

import java.io.File;  
import java.io.FileOutputStream;  
import java.math.BigInteger;  

import javax.swing.table.DefaultTableCellRenderer;
  
  
/** 
 * Created by zhouhs on 2017/1/9. 
 */  
public class WordExportController {  
  
    public static void main(String[] args)throws Exception {  
        //Blank Document  
        XWPFDocument document= new XWPFDocument();  
  
        //Write the Document in file system  
        FileOutputStream out = new FileOutputStream(new File("create_table.docx"));  
  
  
        //添加标题  
        XWPFParagraph titleParagraph = document.createParagraph();  
        //设置段落居中  
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);  
 
  
        XWPFRun titleParagraphRun = titleParagraph.createRun();  
        titleParagraphRun.setText("Java PoI");  
        titleParagraphRun.setColor("000000");  
        titleParagraphRun.setFontSize(20);  
      
        
  
        //段落  
       /* XWPFParagraph firstParagraph = document.createParagraph();  
        XWPFRun run = firstParagraph.createRun();  
        run.setText("Java POI 生成word文件。");  
        run.setColor("696969");  
        run.setFontSize(16);  */
  
        //设置段落背景颜色  
      /*  CTShd cTShd = run.getCTR().addNewRPr().addNewShd();  
        cTShd.setVal(STShd.CLEAR);  
        cTShd.setFill("97FFFF");  
  */
  
        //换行  
        /*XWPFParagraph paragraph1 = document.createParagraph();  
        XWPFRun paragraphRun1 = paragraph1.createRun();  
        paragraphRun1.setText("\r");  
  
  
        //基本信息表格  
        XWPFTable infoTable = document.createTable();  
        //去表格边框  
        infoTable.getCTTbl().getTblPr().unsetTblBorders();  
  
  
        //列宽自动分割  
        CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();  
        infoTableWidth.setType(STTblWidth.DXA);  
        infoTableWidth.setW(BigInteger.valueOf(9072));  
  
  
        //表格第一行  
        XWPFTableRow infoTableRowOne = infoTable.getRow(0);  
        infoTableRowOne.getCell(0).setText("职位");  
        infoTableRowOne.addNewTableCell().setText(": Java 开发工程师");  
  
        //表格第二行  
        XWPFTableRow infoTableRowTwo = infoTable.createRow();  
        infoTableRowTwo.getCell(0).setText("姓名");  
        infoTableRowTwo.getCell(1).setText(": seawater");  
  
        //表格第三行  
        XWPFTableRow infoTableRowThree = infoTable.createRow();  
        infoTableRowThree.getCell(0).setText("生日");  
        infoTableRowThree.getCell(1).setText(": xxx-xx-xx");  
  
        //表格第四行  
        XWPFTableRow infoTableRowFour = infoTable.createRow();  
        infoTableRowFour.getCell(0).setText("性别");  
        infoTableRowFour.getCell(1).setText(": 男");  
  
        //表格第五行  
        XWPFTableRow infoTableRowFive = infoTable.createRow();  
        infoTableRowFive.getCell(0).setText("现居地");  
        infoTableRowFive.getCell(1).setText(": xx");  
  
  
        //两个表格之间加个换行  
        XWPFParagraph paragraph = document.createParagraph();  
        XWPFRun paragraphRun = paragraph.createRun();  
        paragraphRun.setText("\r");  */
  
  
  
        //工作经历表格  
     
        XWPFTable ComTable = document.createTable();  
       

        
        //列宽自动分割  
        CTTblWidth comTableWidth = ComTable.getCTTbl().addNewTblPr().addNewTblW();  
        comTableWidth.setType(STTblWidth.DXA);  
        comTableWidth.setW(BigInteger.valueOf(9072));  
        
        //表格第一行  
        XWPFTableRow comTableRowOne = ComTable.getRow(0);  
        comTableRowOne.getCell(0).setText("项目名称");  
        comTableRowOne.addNewTableCell().setText("结束时间");  
        comTableRowOne.addNewTableCell().setText("项目编号");  
        comTableRowOne.addNewTableCell().setText("title"); 
        comTableRowOne.addNewTableCell().setText("项目经理");  
        comTableRowOne.addNewTableCell().setText("title");  
  
        //表格第二行  
        XWPFTableRow comTableRowTwo = ComTable.createRow();  
        comTableRowTwo.getCell(0).setText("项目类型");  
        comTableRowTwo.getCell(1).setText("至今");  
        comTableRowTwo.getCell(2).setText("项目所在部门");  
        comTableRowTwo.getCell(3).setText("Java开发工程师");
        comTableRowTwo.getCell(4).setText("业务方");  
        comTableRowTwo.getCell(5).setText("Java开发工程师");  
  
  
        //表格第三行  
        XWPFTableRow comTableRowThree = ComTable.createRow();  
        comTableRowThree.getCell(0).setText("2016-09-06");  
        comTableRowThree.getCell(1).setText("至今");  
        comTableRowThree.getCell(2).setText("seawater");  
        comTableRowThree.getCell(3).setText("Java开发工程师");  
        comTableRowThree.getCell(4).setText("seawater");  
        comTableRowThree.getCell(5).setText("Java开发工程师");  
        
        XWPFTableRow comTableRowFive= ComTable.createRow(); 
        //comTableRowFive.getCell(0).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);    
        comTableRowFive.getCell(0).setText("项目总体情况描述");
        XWPFTableCell cell =comTableRowFive.getCell(0);
        getParagraph(comTableRowFive.getCell(0),"sss");
        
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER); //垂直居中
        
      //  comTableRowFive.getCell(0).setVerticalAlignment(XWPFVertAlign.CENTER);
       
       
      //  comTableRowFive.getCell(0).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);    
        
        
       /* cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER); //垂直居中
        comTableRowFive.getCell(0).set*/
      //  DefaultTableCellRenderer renderer=new DefaultTableCellRenderer();
       // renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        //.setDefaultRenderer(Object.class, renderer);
        //comTableRowFive.setRepeatHeader(true);
      
        
        XWPFTableRow comTableRowsix= ComTable.createRow(); 
        comTableRowsix.getCell(0).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);    
        comTableRowsix.getCell(0).setText("跨行");
       // mergeCellsVertically(ComTable, 0, 0, 3); 
           mergeCellsHorizontal(ComTable, 3, 0, 5);
           mergeCellsHorizontal(ComTable, 4, 0, 5);
        
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();  
        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);  
  
        //添加页眉  
        CTP ctpHeader = CTP.Factory.newInstance();  
        CTR ctrHeader = ctpHeader.addNewR();  
        CTText ctHeader = ctrHeader.addNewT();  
        String headerText = "周报文件";  
        ctHeader.setStringValue(headerText);  
        XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);  
        //设置为右对齐  
        headerParagraph.setAlignment(ParagraphAlignment.RIGHT);  
        XWPFParagraph[] parsHeader = new XWPFParagraph[1];  
        parsHeader[0] = headerParagraph;  
        policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);  
  
  
        //添加页脚  
        CTP ctpFooter = CTP.Factory.newInstance();  
        CTR ctrFooter = ctpFooter.addNewR();  
        CTText ctFooter = ctrFooter.addNewT();  
        String footerText = "有事联系我";  
        ctFooter.setStringValue(footerText);  
        XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooter, document);  
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);  
        XWPFParagraph[] parsFooter = new XWPFParagraph[1];  
        parsFooter[0] = footerParagraph;  
        policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, parsFooter);  
  
  
        document.write(out);  
        out.close();  
        System.out.println("create_table document written success.");  
    }  
  
    public  void fillTable(XWPFTable table) {  
        for (int rowIndex = 0; rowIndex < table.getNumberOfRows(); rowIndex++) {  
            XWPFTableRow row = table.getRow(rowIndex);  
            row.setHeight(380);  
            for (int colIndex = 0; colIndex < row.getTableCells().size(); colIndex++) {  
                XWPFTableCell cell = row.getCell(colIndex);  
                if(rowIndex%2==0){  
                     setCellText(cell, " cell " + rowIndex + colIndex + " ", "D4DBED", 1000);  
                }else{  
                     setCellText(cell, " cell " + rowIndex + colIndex + " ", "AEDE72", 1000);  
                }  
            }  
        }  
    }  
      
    public  void setCellText(XWPFTableCell cell,String text, String bgcolor, int width) {  
        CTTc cttc = cell.getCTTc();  
        CTTcPr cellPr = cttc.addNewTcPr();  
        cellPr.addNewTcW().setW(BigInteger.valueOf(width));  
        //cell.setColor(bgcolor);  
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
    public static  void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell) {  
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {  
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);  
            if ( cellIndex == fromCell ) {  
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
    public static  void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {  
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {  
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);  
            if ( rowIndex == fromRow ) {  
                // The first merged cell is set with RESTART merge value  
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);  
            } else {  
                // Cells which join (merge) the first one, are set with CONTINUE  
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);  
            }  
        }  
    }  
      
    public static  void setTableWidth(XWPFTable table,String width){  
        CTTbl ttbl = table.getCTTbl();  
        CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl.getTblPr();  
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();  
        CTJc cTJc=tblPr.addNewJc();  
        cTJc.setVal(STJc.Enum.forString("center"));  
        tblWidth.setW(new BigInteger(width));  
        tblWidth.setType(STTblWidth.DXA);  
    }  
    private static void getParagraph(XWPFTableCell cell,String cellText){  
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
