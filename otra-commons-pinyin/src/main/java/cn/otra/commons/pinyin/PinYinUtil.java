package cn.otra.commons.pinyin;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {
	public static String getPingYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					t4 = t4 + t2[0];
				} else {
					t4 = t4 + Character.toString(t1[i]);
				}
			}
			return t4;
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return t4;
	}

	public static String getPinYinHeadChar(String str) {
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null)
				convert = convert + pinyinArray[0].charAt(0);
			else {
				convert = convert + word;
			}
		}
		return convert;
	}

	public static String getPinYinOneHeadChar(String str) {
		String convert = "";
		if (str != null && str.length() > 0) {
			char word = str.charAt(0);
			if (!isHeadChar(str)) {
				String[] pinyinArray = PinyinHelper
						.toHanyuPinyinStringArray(word);
				convert = String.valueOf(pinyinArray[0].charAt(0));
			} else {
				convert = String.valueOf(word);
			}
		}
		return convert;
	}

	public static boolean isHeadChar(String s) {
		char c = s.charAt(0);
		int i = c;
		if (((i >= 65) && (i <= 90)) || ((i >= 97) && (i <= 122))) {
			return true;
		}
		return false;
	}

	public static String getCnASCII(String cnStr) {
		StringBuffer strBuf = new StringBuffer();
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			strBuf.append(Integer.toHexString(bGBK[i] & 0xFF));
		}
		return strBuf.toString();
	}

	public static void main(String[] args) {
		System.err.println(getPinYinOneHeadChar("张支晓").toUpperCase());
		System.err.println(getPinYinOneHeadChar("d支晓"));
	}
}