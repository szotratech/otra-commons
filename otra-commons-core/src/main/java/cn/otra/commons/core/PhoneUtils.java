package cn.otra.commons.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtils {

	//^[1][3-8]+\\d{9}
//	private static final Pattern PATTERN = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
	private static final Pattern PATTERN = Pattern.compile("^[1][3-8]+\\d{9}$");

	public static boolean isMobileNO(String mobiles) {
		Matcher m = PATTERN.matcher(mobiles);
		return m.matches();
	}

	public static void main(String[] args) {
		System.err.println(isMobileNO("18605005566"));
	}
	
}
