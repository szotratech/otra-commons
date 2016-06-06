package cn.otra.commons.core;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import cn.otra.commons.core.DateUtils.TimeFormatter;

public class BeanUtils {
	
	public final static Object getRealVal(Class<?> toClass,Object fromValue) {
		if(fromValue == null || "null".equals(fromValue)) {
			return null;
		}
		if(toClass == Integer.class) {
			return Integer.valueOf((String)fromValue);
		}
		if(toClass == Long.class) {
			return Long.valueOf((String)fromValue);
		}
		if(toClass == Float.class) {
			return Float.valueOf((String)fromValue);
		}
		if(toClass == File.class) {
			return (File)fromValue;
		}
		if(toClass == Boolean.class) {
			return Boolean.valueOf((String)fromValue);
		}
		return fromValue;
	}
	
	public final static String toString(Object obj) {
		if(obj == null) {
			return null;
		}
		return getPropertiesString(obj);
	}
	
	/**
	 * use toString(Object obj)　instead
	 * @param obj
	 * @return
	 */
	@Deprecated 
	public final static String getPropertiesString(Object obj) {
		try {
			Class<?> clazz = obj.getClass();
			StringBuilder sb = new StringBuilder();
			Field[] fs = ReflectUtil.getFields(obj.getClass());
			sb.append(clazz.getSimpleName()).append(":[");
			for (Field f : fs) {
				f.setAccessible(true);
				if (!Modifier.isStatic(f.getModifiers())) {
					Object fv = f.get(obj);
					if(fv instanceof Date) {
						Date dateVal = (Date)fv;
						sb.append(f.getName()).append(":").append(DateUtils.dateToString(dateVal, TimeFormatter.FORMATTER1)).append(",");
					} else {
						sb.append(f.getName()).append(":").append(fv).append(",");
					}
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
			return sb.toString();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	
	/**
	 * 获取对象属性值
	 * @param obj
	 * @param col
	 * @return
	 */
	public final static Object getPropertie(Object obj,String col) {
		try {
			Field[] fs = ReflectUtil.getFields(obj.getClass());
			for (Field f : fs) {
				f.setAccessible(true);
				if (Modifier.isPrivate(f.getModifiers())
						&& !Modifier.isStatic(f.getModifiers())) {
					if(f.getName().equals(col)) {
						return f.get(obj);
					}
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final void trimObject(Object obj) {
		if(obj == null) {
			return;
		}
		Field fs [] = ReflectUtil.getFields(obj.getClass());
		try {
			for(Field f:fs) {
				f.setAccessible(true);
				if(f.getType() == String.class) {
					String sValue = (String)f.get(obj);
					if(sValue != null) {
						f.set(obj, sValue.trim());
					}
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
