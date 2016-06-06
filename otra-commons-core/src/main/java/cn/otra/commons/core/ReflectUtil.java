package cn.otra.commons.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtil {

	public static final Map<Class<?>, Field[]> classFields = new ConcurrentHashMap<Class<?>, Field[]>();
	public static final Map<Class<?>, Map<String, Field>> classFieldMaps = new ConcurrentHashMap<Class<?>, Map<String, Field>>();
	
	/**
	 * 获取给定对象的所有属性，包括父类属性
	 * @param value
	 * @return
	 */
	public static final Field[] getFields (Class<?> entityClass) {
		Field[] fs = classFields.get(entityClass);
		if(fs != null) {
			return fs;
		} 
		
		fs = entityClass.getDeclaredFields();
		
		//如果是继承关系的话，把父类的属性也加进来，目前只支持一层父类
		Class<?> pClass = entityClass.getSuperclass();
		Field[] pfs = null;
		if(pClass != Object.class) {
			pfs = pClass.getDeclaredFields();
		}
		
		if(pfs != null) {
			Field[] temp = entityClass.getDeclaredFields();
			fs = new Field[pfs.length+temp.length];
			System.arraycopy(pfs, 0, fs, 0, pfs.length);
			System.arraycopy(temp, 0, fs, pfs.length, temp.length);
		}
		classFields.put(entityClass, fs);
		return fs;
	}
	
	public static final Map<String, Field> getFieldMap (Class<?> entityClass) {
		Map<String, Field> map = classFieldMaps.get(entityClass);
		if (map != null) {
			return map;
		}
		Field[] fs = getFields(entityClass);
		map = new HashMap<String, Field>();
		for(Field f:fs) {
			map.put(f.getName(), f);
		}
		classFieldMaps.put(entityClass, map);
		return map;
	}
}
