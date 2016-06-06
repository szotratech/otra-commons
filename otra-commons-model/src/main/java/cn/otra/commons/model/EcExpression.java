package cn.otra.commons.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class EcExpression {
	private String expression;
	private static final String EXP_SPLIT = "~";//分隔符
	private TreeMap<Double, DoubleString> numberMap;
	private Map<Object, String> simpleMap;
	public EcExpression(String expression) {
		this.expression = expression;
		init();
	}
	
	private void init() {
		String baseStr = expression.substring(1,expression.length()-1);
		String []kvs = baseStr.split(",");
		for(String kv:kvs) {
			String [] arry = kv.split("=");
			if(arry.length == 2) {
				String k = arry[0];
				String v = arry[1];
				
				if(k.contains(EXP_SPLIT)) {
					if(numberMap == null) {
						numberMap = new TreeMap<Double, DoubleString>();
					}
					String []arry2 = k.split(EXP_SPLIT);
					Double minValue = Double.parseDouble(arry2[0]);
					Double maxValue = Double.parseDouble(arry2[1]);
					numberMap.put(maxValue, new DoubleString(minValue, maxValue, v));
				} else {
					if(simpleMap == null) {
						simpleMap = new HashMap<Object, String>();
					}
					simpleMap.put(k.trim(), v);
				}
			}
		}
	}
	
	 //* 		boolean to String:expression="{true=正确,false=错误}"
	 //* 		number  to String:expression="{0=停用,1=启用,2-3=失败,4-5=异常}"
	
	public String get(Object key) {
		if(key == null) {
			key = "null";
		}
		if(simpleMap != null) {
			String value = simpleMap.get(key.toString().trim());
			if(value != null && value.trim().length() > 0) {
				return value.trim();
			}
		}
		if(numberMap == null) {
			return key.toString();
		}
		//大于或等于key的最小值
		Double dKey = objectToDouble(key);
		Double ceilingKey = numberMap.ceilingKey(dKey);
		if(ceilingKey != null) {
			DoubleString ds = numberMap.get(ceilingKey);
			if(ds.getMinValue() <= dKey) {
				return ds.getStrValue();
			}
		}
		return key.toString();
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
	
	static class DoubleString {
		private Double minValue;
		private Double maxValue;
		private String strValue;
		public DoubleString(Double minValue, Double maxValue, String strValue) {
			super();
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.strValue = strValue;
		}
		public Double getMinValue() {
			return minValue;
		}
		public void setMinValue(Double minValue) {
			this.minValue = minValue;
		}
		public Double getMaxValue() {
			return maxValue;
		}
		public void setMaxValue(Double maxValue) {
			this.maxValue = maxValue;
		}
		public String getStrValue() {
			return strValue;
		}
		public void setStrValue(String strValue) {
			this.strValue = strValue;
		}
		
		
	}
	
	public static void main(String[] args) {
		String str = "{0=失败,1=成功}";
		EcExpression expression = new EcExpression(str);
		System.err.println("get(1)="+expression.get(1));
	}
}
