package com.dili.alm.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class GetFirstCharUtil {

	   /**
	     * 将字符串中的中文转化为拼音,其他字符不变
	     * 
	     * @param inputString
	     * @return
	     */
	    public static String getPingYin(String inputString) {
	        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
	        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	        format.setVCharType(HanyuPinyinVCharType.WITH_V);
	 
	        char[] input = inputString.trim().toCharArray();
	        String output = "";
	 
	        try {
	            for (int i = 0; i < input.length; i++) {
	                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
	                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
	                    output += temp[0];
	                } else
	                    output += java.lang.Character.toString(input[i]);
	            }
	        } catch (BadHanyuPinyinOutputFormatCombination e) {
	            e.printStackTrace();
	        }
	        return output;
	    }
	    /**  
	     * 获取汉字串拼音首字母，英文字符不变  
	     * @param chinese 汉字串  
	     * @return 汉语拼音首字母  
	     */  
	    public static String getFirstSpell(String chinese) {   
	            StringBuffer pybf = new StringBuffer();   
	            char[] arr = chinese.toCharArray();   
	            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();   
	            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);   
	            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);   
	            for (int i = 0; i < arr.length; i++) {   
	                    if (arr[i] > 128) {   
	                            try {   
	                                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);   
	                                    if (temp != null) {   
	                                            pybf.append(temp[0].charAt(0));   
	                                    }   
	                            } catch (BadHanyuPinyinOutputFormatCombination e) {   
	                                    e.printStackTrace();   
	                            }   
	                    } else {   
	                            pybf.append(arr[i]);   
	                    }   
	            }   
	            return pybf.toString().replaceAll("\\W", "").trim();   
	    }   
	    /**  
	     * 获取汉字串拼音，英文字符不变  
	     * @param chinese 汉字串  
	     * @return 汉语拼音  
	     */  
	    public static String getFullSpell(String chinese) {   
	            StringBuffer pybf = new StringBuffer();   
	            char[] arr = chinese.toCharArray();   
	            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();   
	            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);   
	            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);   
	            for (int i = 0; i < arr.length; i++) {   
	                    if (arr[i] > 128) {   
	                            try {   
	                                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);   
	                            } catch (BadHanyuPinyinOutputFormatCombination e) {   
	                                    e.printStackTrace();   
	                            }   
	                    } else {   
	                            pybf.append(arr[i]);   
	                    }   
	            }   
	            return pybf.toString();   
	    }  
		
	/*	public static void main(String[] args)
		{
			String cnStr = "香蕉你12個巴拉";
			String cnStr2 = "哈尔滨市场";
			System.out.println("香蕉你12個巴拉-->" + getPingYin(cnStr));
			String s = getFirstSpell("香蕉你12個巴拉");
			System.out.println("香蕉你12個巴拉-->" + s);
			StringBuffer sb = new StringBuffer(s);
			if (sb.length() > 1)
			{
				String ss = sb.delete(1, sb.length()).toString();
				System.out.println("香蕉你12個巴拉-->"
						+ Character.toUpperCase(ss.toCharArray()[0]) + "");
			}
			cnStr2 = cnStr2.substring(0,cnStr2.length()-2);
			GetFirstCharUtil.getFirstSpell(cnStr2).toUpperCase();
			System.out.println(GetFirstCharUtil.getFirstSpell(cnStr2).toUpperCase());
		}*/

}
