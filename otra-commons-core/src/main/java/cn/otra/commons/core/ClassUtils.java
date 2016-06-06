package cn.otra.commons.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassUtils {
	//缓存反射，以最大限度降低调用开销
	static final Map<Class<?>,Field[]> fieldMap = new HashMap<Class<?>,Field[]>();
	static final Map<Class<?>,Map<String,String>> mysqlFieldMap = new HashMap<Class<?>,Map<String,String>>();
	private static final Map<Class<?>,TypeEnum> TYPE_MAP = new HashMap<Class<?>,TypeEnum>();
	private static final Map<Class<?>, Map<String, Field>> nameFieldMap = new HashMap<Class<?>, Map<String,Field>>();
	enum TypeEnum {
		//_short,_int,_long,_float,_double,_boolean,Short,Integer,Long,Float,Double,Boolean,String,Date,Object
		Short,Integer,Long,Float,Double,Boolean,String,Date,Object
	}
	static void init() {
//		TYPE_MAP.put(short.class, TypeEnum._short);
//		TYPE_MAP.put(int.class, TypeEnum._int);
//		TYPE_MAP.put(long.class, TypeEnum._long);
//		TYPE_MAP.put(float.class, TypeEnum._float);
//		TYPE_MAP.put(double.class, TypeEnum._double);
//		TYPE_MAP.put(boolean.class, TypeEnum._boolean);
		TYPE_MAP.put(Short.class, TypeEnum.Short);
		TYPE_MAP.put(Integer.class, TypeEnum.Integer);
		TYPE_MAP.put(Long.class, TypeEnum.Long);
		TYPE_MAP.put(Float.class, TypeEnum.Float);
		TYPE_MAP.put(Double.class, TypeEnum.Double);
		TYPE_MAP.put(Boolean.class, TypeEnum.Boolean);
		TYPE_MAP.put(String.class, TypeEnum.String);
		TYPE_MAP.put(Date.class, TypeEnum.Date);
		TYPE_MAP.put(Object.class, TypeEnum.Object);
	}
	static {
		init();
	}
	
	public static final <T> Field[] getFields(Class<T> entityClass,boolean withSuperClass) {
		Field[] fs = fieldMap.get(entityClass);
		
		if(fs == null) {
			fs = ReflectUtil.getFields(entityClass);
			
			//缓存反射方法getDeclaredFields的调用结果
			fieldMap.put(entityClass, fs);
			//缓存javaType<-->mysqlType名称对应,如:userName<-->user_name
			if(mysqlFieldMap.get(entityClass) == null) {
				Map<String,String> fm = new HashMap<String, String>();
				mysqlFieldMap.put(entityClass, fm);
				for(Field f:fs) {
					if(!Modifier.isFinal(f.getModifiers())) {
						String name = getMysqlStandField(f.getName());
						fm.put(f.getName(), name);
					}
				}
			}
			
		}
		return fs;
	}
	
	
	/**
	 * 将一个类的的属性名和属性封装到一个MAP中并返回
	 * @param entityClass
	 * @return
	 */
	public static final <T> Map<String, Field> getDeclaredFieldMap(Class<T> entityClass) {
		Map<String, Field> map = nameFieldMap.get(entityClass);
		if(map == null) {
			map = new HashMap<String, Field>();
			nameFieldMap.put(entityClass, map);
			Field[] fs = getFields(entityClass, true);
			for(Field f:fs) {
				map.put(f.getName(), f);
			}
		}
		return map;
	}
	
	public static final <T> Field getDeclaredField(Class<T> entityClass,String fieldName) {
		Map<String, Field> map = getDeclaredFieldMap(entityClass);
		return map.get(fieldName);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T map2Entity(Map<String,Object> map,Class<T> entityClass) {
		T t = null;
		try {
			
			if(Number.class.isAssignableFrom(entityClass)) {
				for(Map.Entry<String, Object> en:map.entrySet()) {
					t = (T)getRealValue(entityClass, en.getValue());
				}
				return t;
			}
			t = entityClass.newInstance();
			
			Field[] fs = getFields(entityClass, true);
			
			for(Field f:fs) {
				if(Modifier.isFinal(f.getModifiers())) {
					continue;
				}
				f.setAccessible(true);
				String fieldName = f.getName();
				String key = mysqlFieldMap.get(entityClass).get(fieldName);
				if(key == null) {
					continue;
				}
				Object value = map.get(key);
				if(value == null) {
					value = map.get(fieldName);
				}
				f.set(t, getRealValue(f,value));
			}
		} catch (Exception e) {
			throw new RuntimeException("map2Entity",e);
		} 
		return t;
	}
	
	public static final Object getRealValue(Field field,Object value) {
		if(value == null) {
			TypeEnum key = TYPE_MAP.get(field.getType());
			if(key == null) {
				return null;
			}
			switch (key) {
//				case _int:
//					return 0;
//				case _long:
//					return 0l;
//				case _float:
//					return 0.0f;
//				case _double:
//					return 0.0;
//				case _boolean:
//					return false;
				default:
					return null;
			}
//			return null;
		} else {
			if(value instanceof String) {
				return getValueByClass(field.getType(),value.toString());
			}
			if(value instanceof Number) {
				return getValueByClass(field.getType(),(Number)value);
			}
			if(value instanceof Date) {
				return (Date)value;
			}
			if(value instanceof Boolean) {
				return (Boolean)value;
			}
		}
		
		return null;
	}
	
	public static final boolean isNumber(String str) {
		if(str == null || str.trim().length() == 0) {
			return false;
		}
		char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && chars[start + 1] == 'x') {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                        && (chars[i] < 'a' || chars[i] > 'f')
                        && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
              // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent   
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns
                && (chars[i] == 'd'
                    || chars[i] == 'D'
                    || chars[i] == 'f'
                    || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l'
                || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
	}
	
	public static final boolean checkType(Class<?> clazz,String value) {
		if(value == null) {
			return true;
		}
		TypeEnum type = TYPE_MAP.get(clazz);
		switch (type) {
		case Integer:
			try {
				Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		case Long:
			try {
				Long.parseLong(value);
			} catch (NumberFormatException e) {
				return false;
			}
		case Float:
			try {
				Float.parseFloat(value);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		case Double:
			try {
				Double.parseDouble(value);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		case Boolean:
			try {
				Boolean.parseBoolean(value);
			} catch (Exception e) {
				return false;
			}
			return true;
		case String:
			return true;
		case Date:
			return stringToDate(value) != null;
		case Object:
			return true;
		default:
			break;
		}
		return false;
	}
	
	private static final Date stringToDate(String value) {
		if(value == null) {
			return null;
		}
		int length = value.length();
		String pattern = null;
		if(value.contains("-")) {
			switch (length) {
			case 8://yy-MM-dd
				pattern = "yy-MM-dd";
				break;
			case 10://yyyy-MM-dd
				pattern = "yyyy-MM-dd";
				break;
			case 19://yyyy-MM-dd HH:mm:ss
				pattern = "yyyy-MM-dd HH:mm:ss";
				break;

			default:
				break;
			}
		} else {
			switch (length) {
			case 8://yyyyMMdd
				pattern = "yyyyMMdd";
				break;
			case 14://yyyyMMddHHmmss
				pattern = "yyyyMMddHHmmss";
				break;

			default:
				break;
			}
		}
//		return new SimpleDateFormat(pattern)
		if(pattern == null) {
			return null;
		} else {
			try {
				return new SimpleDateFormat(pattern).parse(value);
			} catch (ParseException e) {
				throw new RuntimeException("暂时不支持["+value+"]格式的日期。",e);
			}
		}
	}
	
	public static final Object getRealValue(Class<?> clazz,Object value) {
		if(value == null) {
			switch (TYPE_MAP.get(clazz)) {
//				case _int:
//					return 0;
//				case _long:
//					return 0l;
//				case _float:
//					return 0.0f;
//				case _double:
//					return 0.0;
//				case _boolean:
//					return false;
				default:
					return null;
			}
		} else {
			if(value instanceof String) {
				return getValueByClass(clazz,value.toString());
			}
			if(value instanceof Number) {
				return getValueByClass(clazz,(Number)value);
			}
			if(value instanceof Date) {
				return (Date)value;
			}
			if(value instanceof Boolean) {
				return (Boolean)value;
			}
		}
		
		return null;
	}
	
	public static final List<Method> getMethods(Class<?> actualServiceClass) throws NoSuchMethodException, SecurityException {
		Class<?> itfClass = actualServiceClass.getInterfaces()[0];
		Method[] ms = itfClass.getDeclaredMethods();
		List<Method> mList = new ArrayList<Method>();
		for(Method m:ms) {
			Method method = actualServiceClass.getMethod(m.getName(), m.getParameterTypes());
			mList.add(method);
		}
		return mList;
	}
	
	private static final Object getValueByClass(Class<?> clazz,String value) {
//		System.err.println("clazz="+clazz.getSimpleName());
		TypeEnum type = TYPE_MAP.get(clazz);
		switch (type) {
		case Integer:
			if(!isNumber(value)) {
				return null;
			}
			return Integer.parseInt(value);
		case Long:
			if(!isNumber(value)) {
				return null;
			}
			return Long.parseLong(value);
		case Float:
			if(!isNumber(value)) {
				return null;
			}
			return Float.parseFloat(value);
		case Double:
			if(!isNumber(value)) {
				return null;
			}
			return Double.parseDouble(value);
		case Boolean:
			try {
				return Boolean.parseBoolean(value);
			} catch (Exception e) {
				return false;
			}
		case String:
			return value;
		case Date:
			return stringToDate(value);
		case Object:
			return value;
		default:
			break;
		}
		return null;
	}
	
	private static final Object getValueByClass(Class<?> clazz,Number value) {
//		System.err.println("clazz="+clazz.getSimpleName());
		TypeEnum type = TYPE_MAP.get(clazz);
		switch (type) {
		case Integer:
			return value.intValue();
//		case _int:
//			return value == null?0:value.intValue();
		case Long:
//		case _long:
			return value.longValue();
		case Float:
//		case _float:
			return value.floatValue();
		case Double:
//		case _double:
			return value.doubleValue();
		case Boolean:
//		case _boolean:
			return value.intValue()==1;
		case String:
			return value.toString();
		case Object:
			return value;
		default:
			break;
		}
		return null;
	}
	
	public static final <T> List<T> map2List(List<Map<String,Object>> list,Class<T> entityClass) {
		List<T> nList = new ArrayList<T>();
		for(Map<String,Object> map:list) {
			nList.add(map2Entity(map,entityClass));
		}
		return nList;
	}
	
	public static final String getMysqlStandField(String javaField) {
		char [] chars = javaField.toCharArray();
		int count = 0;
		for(int i=0;i<chars.length;i++) {
			char c = chars[i];
			if(c >= 'A' && c <='Z') {
				count += 2;
			} else {
				count ++;
			}
		}
		
		char [] dest = new char[count];
		int index = 0;
		for(int i=0;i<chars.length;i++) {
			char c = chars[i];
			if(c >= 'A' && c <='Z') {
				count += 2;
				dest[index++] = '_';
				dest[index++] = (char)(c+32);
			} else {
				dest[index++] = c;
			}
		}
		return new String(dest);
	}
	
	public static void main(String[] args) {
//		String f = getMysqlStandField("email0");
//		System.err.println(f);
		System.err.println(Number.class.isAssignableFrom(Long.class));
	}
	
}
