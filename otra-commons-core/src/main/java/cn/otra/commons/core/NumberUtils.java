package cn.otra.commons.core;

import java.math.BigDecimal;

public class NumberUtils {
	
	/**
	 * 格式化double类型数据
	 * @param value 
	 * @param bits 保留小数位数
	 * @return
	 */
	public static double formatDouble(double value,int bits) {
		BigDecimal bg = new BigDecimal(value);
		double f1 = bg.setScale(bits, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
	
	/**
	 * 格式化float类型数据
	 * @param value
	 * @param bits 保留小数位数
	 * @return
	 */
	public static float formatFloat(float value,int bits) {
		BigDecimal bg = new BigDecimal(value);
		float f1 = bg.setScale(bits, BigDecimal.ROUND_HALF_UP).floatValue();
		return f1;
	}
	
	public static final boolean startWithNumber(String value) {
		if(value == null) {
			return false;
		}
		if(value.trim().length() == 0) {
			return false;
		}
		char c = value.trim().substring(0,1).charAt(0);
		if(c >= '0' && c <= '9') {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		System.err.println("aaa="+startWithNumber("aaa"));
		System.err.println("0aaa="+startWithNumber("0aaa"));
		System.err.println("9aaa="+startWithNumber("9aaa"));
	}
	
	/**
	 * 格式化float类型数据(保留2位小数)
	 * @param value
	 * @return
	 */
	public static float formatFloat(float value) {
		return formatFloat(value,2);
	}
	
	public static final Double objectToDouble(Object value) {
		if(value instanceof Double) {
			return (Double)value;
		} else if(value instanceof Integer) {
			return ((Integer)value).doubleValue();
		} else if(value instanceof Float) {
			return ((Float)value).doubleValue();
		} else if(value instanceof Long) {
			return ((Long)value).doubleValue();
		} else if(value instanceof Short) {
			return ((Short)value).doubleValue();
		} else {
			return null;
		}
	}
}
