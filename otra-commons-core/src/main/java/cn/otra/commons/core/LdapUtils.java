package cn.otra.commons.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LdapUtils {

	static class LdapBuilder {
		//TODO
	}
	
	static class LdapEle {
		//TODO
	}
	
	public static final LdapBuilder getBuilder() {
		return new LdapBuilder();
	}
	
	public static final String getFilterFromMap(Map<String, Serializable> map) {
		if(map == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder("(&");
		for(Map.Entry<String, Serializable> en:map.entrySet()) {
			String key = en.getKey();
			Serializable value = en.getValue();
			builder.append("(").append(key).append("=").append(value).append(")");
		}
		builder.append(")");
		return builder.toString();
	}
	
	public static void main(String[] args) {
		Map<String, Serializable> filter = new HashMap<String, Serializable>();
		filter.put("version", "0.0.1");
		filter.put("mode", "remote");
		System.err.println(getFilterFromMap(filter));
	}
}
