package cn.otra.commons.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


public class DefaultConfig {

	private static final Logger logger = Logger.getLogger(DefaultConfig.class);
	private String appId;
	private Map<String, String> configMap = new ConcurrentHashMap<String, String>();
	private Properties properties;
//	private static final String FILE_NAME = "server.properties";
	private String configName = "server.properties";
	private boolean isInit = false;

	public void setConfigName(String configName) {
		this.configName = configName;
	}
	
	private void showConf() {
		for (Map.Entry<String, String> en:configMap.entrySet()) {
			logger.info(">>>>>>"+en.getKey()+"="+en.getValue());
		}
	}
	
	public DefaultConfig () {}
	
	public DefaultConfig (String appId) {
		this.appId = appId;
	}
	
	public DefaultConfig (String appId,String configName) {
		this.configName = configName;
		this.appId = appId;
	}
	
	private void init() {
		if(isInit) {
			return;
		}
		if(appId == null) {
			isInit = true;
			throw new RuntimeException("no appId found!");
		}
		try {
			if(configName == null) {
				configName = "server.properties";
			}
			properties = PropUtil.getProperties(configName);
			for (Map.Entry<Object, Object> en:properties.entrySet()) {
				String key = (String)en.getKey();
				String val = (String)en.getValue();
				configMap.put(key, val);
			}
			logger.info(">>>>>>>>>>>>>get configuration from local.[server.properties]>>>>>>>>>>>>>");
			showConf();
		} catch (Exception e) {
			logger.error("init",e);
			System.exit(1);
		}
		isInit = true;
	}
	
	public void put(String key,Object value) {
		properties.put(key, value);
	}
	
	public void store() {
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(configName));
			properties.store(os, null);
			init();
		} catch (Exception e) {
			logger.error("store",e);
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Map<String, String> getVal() {
		return configMap;
	}
	
	public String getVal(String key) {
		init();
		String value = configMap.get(key);
		if(value == null) {
			throw new RuntimeException("no propertie ["+key+"] found in configuration.");
		}
		return value;
	}
	
	public Integer getInt(String key) {
		String value = getVal(key);
		return Integer.parseInt(value);
	}
	
	public Long getLong(String key) {
		String value = getVal(key);
		return Long.parseLong(value);
	}

	public Float getFloat(String key) {
		String value = getVal(key);
		return Float.parseFloat(value);
	}
	
	public Double getDouble(String key) {
		String value = getVal(key);
		return Double.parseDouble(value);
	}
	
	public Boolean getBoolean(String key) {
		String value = getVal(key);
		return Boolean.valueOf(value);
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}


}
