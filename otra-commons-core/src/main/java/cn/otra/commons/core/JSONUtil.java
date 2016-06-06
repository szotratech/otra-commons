package cn.otra.commons.core;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;

public class JSONUtil {
	private static ObjectMapper objectMapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY);;
	private static final Map<Class<?>, TypeReference<?>> REF_MAP = new ConcurrentHashMap<Class<?>, TypeReference<?>>();
	private static final DateFormat datFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static {
		// 忽略未知属性（在接口升级很有用）
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationConfig.Feature.AUTO_DETECT_FIELDS, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.setDateFormat(datFormat);
		
	}

	public static ObjectMapper getInstance() {
		return objectMapper;
	}

	/**
	 * 转化实体为json字符串
	 * 请使用toString代替
	 * @throws IOException
	 * 
	 */
	@Deprecated
	public static String toJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("fromJson", e);
		}
	}
	
	public static String toString(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("fromJson", e);
		}
	}

	/**
	 * 转化实体为json字符串
	 * 请使用getObject代替
	 * @param obj
	 * @param c
	 * @return
	 */
	@Deprecated
	public static <T> T fromJson(String json, final Class<T> c) {
		if (json == null) {
			return null;
		}
		if (json.trim().equals("")) {
			return null;
		}
		try {
			TypeReference<?> reference = REF_MAP.get(c);
			if(reference == null) {
				reference = new TypeReference<T>() {
					@Override
					public Type getType() {
						return c;
					}
				};
				REF_MAP.put(c, reference);
			} else {
//				System.err.println("from cache.map.size="+REF_MAP.size());
			}
			return objectMapper.readValue(json, reference);
		} catch (Exception e) {
			throw new RuntimeException("fromJson---"+json, e);
		}
	}
	
	/**
	 * 请使用getObject代替
	 * @param json
	 * @param reference
	 * @return
	 */
	@Deprecated
	public static <T> T fromJson(String json, TypeReference<T> reference) {
		if (json == null) {
			return null;
		}
		if (json.trim().equals("")) {
			return null;
		}
		try {
			return objectMapper.readValue(json, reference);
		} catch (Exception e) {
			throw new RuntimeException("fromJson---"+json, e);
		}
	}
	
	public static <T> T getObject(String json, TypeReference<T> reference) {
		return fromJson(json, reference);
	}
	
	public static <T> T getObject(String json, Class<T> clazz) {
		return fromJson(json, clazz);
	}
	
	public static Map<?, ?> getObject(String json) {
		return fromJson(json);
	}

	public static <T> T parseList(String json, Class<T> c) {
		return getObject(json, c);
	}

	/**
	 * json 转为list
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> getList(String json, Class<T> clazz) {
		TypeFactory t = TypeFactory.defaultInstance();
		List<T> list = null;
		try {
			list = objectMapper.readValue(json, t.constructCollectionType(ArrayList.class, clazz));
		} catch (Exception e) {
			throw new RuntimeException("getList", e);
		}
		// 如果T确定的情况下可以使用下面的方法：
		// List<T> list = objectMapper.readValue(jsonVal, new TypeReference<List<T>>() {});
		return list;
	}
	
	/**
	 * 将json字符串转化成Map对象
	 * 请使用getObject代替
	 * @param json
	 * @return
	 */
	@Deprecated
	public static Map<?, ?> fromJson(String json) {
		if(json == null || json.trim().length() == 0) {
			return null;
		}
		LinkedHashMap<?, ?> result = fromJson(json, LinkedHashMap.class);
		return result;
	}

	public static void main(String[] args) {
		Foo foo = new Foo();
		foo.setTime(DateUtils.getTodayDate(0, 0, 0));
		String json = toString(foo);
		System.err.println(json);
		
//		Integer a = 200;
//		Integer b = 200;
//		Integer c = 100;
//		Integer d = 100;
//		System.err.println(a==b);//false,比较地址
//		System.err.println(a==200);//true,转成基本类型再比较
//		System.err.println(c==d);//true,取缓存池的值进行比较（-128~128）
		
	}

	static class Foo {
		private Date time;
		public Date getTime() {
			return time;
		}
		public void setTime(Date time) {
			this.time = time;
		}
	}
	
}
