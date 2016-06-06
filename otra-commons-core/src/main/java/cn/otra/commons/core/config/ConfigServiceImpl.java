package cn.otra.commons.core.config;

import java.lang.reflect.Method;

public class ConfigServiceImpl implements ConfigService {

	private Object propertyPlaceholder;

	public void init() {
		try {
			if(getPropertyMethod == null) {
				getPropertyMethod = propertyPlaceholder.getClass().getDeclaredMethod("getProperty", String.class);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String get(String key) {
		try {
			if(getPropertyMethod == null) {
				getPropertyMethod = propertyPlaceholder.getClass().getDeclaredMethod("getProperty", String.class);
			}
			if(getPropertyMethod != null) {
				getPropertyMethod.setAccessible(true);
				return (String)getPropertyMethod.invoke(propertyPlaceholder, key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Method getPropertyMethod;
	
	public void setPropertyPlaceholder(Object propertyPlaceholder) {
		this.propertyPlaceholder = propertyPlaceholder;
	}

	@Override
	public Integer getInt(String key) {
		String value = get(key);
		return Integer.parseInt(value);
	}

	@Override
	public Long getLong(String key) {
		return Long.parseLong(get(key));
	}
	
	@Override
	public Boolean getBoolean(String key) {
		return Boolean.parseBoolean(get(key));
	}
	
}
