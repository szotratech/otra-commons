package cn.otra.commons.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 属性文件加载类
 * @author ecxiaodx
 *
 */
public class PropUtil {
	
	private static Map<String,Properties> propMap = new ConcurrentHashMap<String, Properties>();

	/**
	 * 加载属性文件
	 * @param prop
	 * @param propFile 属性文件路径（相对classes根目录）
	 * @return
	 */
	public static final Properties loadProperty(String propFile) {
		InputStream input = null;
		try {
			input = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(input == null) {
			input = PropUtil.class.getResourceAsStream(propFile);
		}
		try {
			Properties prop = new Properties();
			prop.load(input);
			return prop;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {}
		}
	}
	
	
	public static final Properties loadProperty(InputStream inputStream) {
		try {
			Properties prop = new Properties();
			prop.load(inputStream);
			return prop;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {}
		}
	}
	
	
	/**
	 * 根据文件名，返回属性配置对象
	 * @param propFile 属性文件路径（相对classes根目录）
	 * @return
	 */
	public static final Properties getProperties(String propFile) {
		Properties prop = propMap.get(propFile);
		if(prop == null) {
			prop = loadProperty(propFile);
			propMap.put(propFile, prop);
		}
		return prop;
	}
	
	/**
	 * 
	 * @param propFile 属性文件路径（相对classes根目录）
	 * @param key	属性KEY
	 * @param reloadFile 是否重新读取文件（<b>reloadFile=false</b>时，会利用缓存，只加载一次配置文件,<b>reloadFile=true</b>时，每次都重新打开配置文件）
	 * @return
	 */
	public static final String getProp(String propFile,String key,boolean ...reloadFile) {
		Properties prop = null;
		if(reloadFile.length > 0 && reloadFile[0] ) {
			prop = loadProperty(propFile);
			return prop.getProperty(key);
		}
		prop = propMap.get(propFile);
		if(prop == null) {
			prop = loadProperty(propFile);
			propMap.put(propFile, prop);
		}
		return prop.getProperty(key);
	}
	
	public static final String getProp(InputStream inputStream,String key,boolean ...reloadFile) {
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return prop.getProperty(key);
	}
	
}
