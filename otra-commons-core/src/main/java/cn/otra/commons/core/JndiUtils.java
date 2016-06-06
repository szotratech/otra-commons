package cn.otra.commons.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class JndiUtils {
	
	private static final Logger LOG = Logger.getLogger(JndiUtils.class);
	private static InitialContext initialContext;
	private static final Map<String, Object> services = new ConcurrentHashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	public static final <T extends Object> T getOsgiService(String serviceName) {
		LOG.info("**************JndiUtils.getOsgiService("+serviceName+")");
		try {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			
			T service = (T)services.get(serviceName);
			if(service == null) {
				service = (T)initialContext.lookup("osgi:service/" + serviceName);
			}
			return service;
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Object> T getOsgiService(String serviceName,String filter) {
		LOG.info("**************JndiUtils.getOsgiService("+serviceName+","+filter+")");
		if(filter == null || filter.trim().length() == 0) {
			return getOsgiService(serviceName);
		}
		try {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				LOG.error("",e);
				throw new RuntimeException(e);
			}
			filter = filter.trim();
			if(!filter.startsWith("(")) {
				filter = "("+filter+")";
			}
			T service = (T)initialContext.lookup("osgi:service/" + serviceName+"/"+filter);
			return service;
		} catch (NamingException e) {
			LOG.error("",e);
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		
		DataSource dataSource = getOsgiService(DataSource.class.getName(),"(mode=bkc)");
		System.err.println("dataSource="+dataSource);
		
	}
	
}
