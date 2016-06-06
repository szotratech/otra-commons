package cn.otra.commons.core.config;

public interface ConfigService {
	
	String get(String key);
	
	Integer getInt(String key);
	
	Long getLong(String key);
	
	Boolean getBoolean(String key);
	
}
