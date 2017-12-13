package com.dili.alm.utils;

import cn.afterturn.easypoi.word.WordExportUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class WordExportUtils {


    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "Easypoi");
        map.put("type", "JueYue");
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(
                    "/Users/shaofan/Desktop/apply.docx", map);
            FileOutputStream fos = new FileOutputStream("/Users/shaofan/Desktop/apply_export.docx");
            doc.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
