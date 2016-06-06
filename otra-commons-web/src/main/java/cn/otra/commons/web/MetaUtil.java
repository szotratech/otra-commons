package cn.otra.commons.web;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.otra.commons.web.meta.ControllerMeta;
import cn.otra.commons.web.meta.FunctionMeta;
import cn.otra.commons.web.meta.TypeMeta;
import cn.otra.commons.web.meta.vo.MFile;

public class MetaUtil {
	public static final String DEFAULT_METHOD = "default_method";
	/**
	 * 获取给定对象的所有属性，包括父类属性
	 * 
	 * @param value
	 * @return
	 */
	public static final Field[] getFields(Class<?> entityClass) {
		Field[] fs = entityClass.getDeclaredFields();

		// 如果是继承关系的话，把父类的属性也加进来，目前只支持一层父类
		Class<?> pClass = entityClass.getSuperclass();
		Field[] pfs = null;
		if (pClass != null && pClass != Object.class) {
			pfs = pClass.getDeclaredFields();
		}

		if (pfs != null) {
			Field[] temp = entityClass.getDeclaredFields();
			fs = new Field[pfs.length + temp.length];
			System.arraycopy(pfs, 0, fs, 0, pfs.length);
			System.arraycopy(temp, 0, fs, pfs.length, temp.length);
		}
		return fs;
	}

	public static final Map<String, Field> getFieldMap(Class<?> entityClass) {
		Field[] fs = getFields(entityClass);
		Map<String, Field> map = new HashMap<String, Field>();
		for (Field f : fs) {
			map.put(f.getName(), f);
		}
		return map;
	}

	public static final boolean isSimpleType(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return true;
		}
		// * @see java.lang.Boolean#TYPE
		// * @see java.lang.Character#TYPE
		// * @see java.lang.Byte#TYPE
		// * @see java.lang.Short#TYPE
		// * @see java.lang.Integer#TYPE
		// * @see java.lang.Long#TYPE
		// * @see java.lang.Float#TYPE
		// * @see java.lang.Double#TYPE
		// * @see java.lang.Void#TYPE
		if (clazz == Boolean.class || clazz == Character.class
				|| clazz == Byte.class || clazz == Short.class
				|| clazz == Integer.class || clazz == Long.class
				|| clazz == Float.class || clazz == Double.class
				|| clazz == String.class || clazz == File.class
				|| clazz == Date.class || clazz == MFile.class) {
			return true;
		}
		return false;
	}
	
	public static final String getSimpleName(String className) {
		if(className == null) {
			return className;
		}
		int lastIndex = className.lastIndexOf(".");
		if(lastIndex == -1) {
			return className;
		}
		return className.substring(lastIndex+1); // strip the package name
	}
	
	public static final boolean startWithNumber(String value) {
		if(value == null) {
			return false;
		}
		if(value.trim().length() == 0) {
			return false;
		}
		char c = '-';
		if(value.trim().startsWith("http://localhost")) {//特例
			return true;
		} else if(value.startsWith("http://")) {
			c = value.trim().charAt(7);
		} else {
			c = value.trim().charAt(0);
		}
		if(c >= '0' && c <= '9') {
			return true;
		}
		return false;
	}
	
	//uri=/api/invokeForm/user/login
	public static final StringBuilder getMethodInvokeForm(String host,Integer port,String uri,String params) {
//		uri = uri.substring(uri.indexOf("/invokeForm")+11);
		String [] arrys = uri.split("/");
		String app = null;
		String module = null;
		String function = null;
		if(uri.startsWith("/invokeForm")) {
			app = "";
			module = arrys[2];
			if(arrys.length > 3) {
				function = arrys[3];
			} else {
				function = "";
			}
		} else {
			app = arrys[1];
			app = app+"/";
			module = arrys[3];
			function = arrys[4];
		}
		
		StringBuilder builder = new StringBuilder();
		String newUri = "/"+app+module+"/"+function;
		builder.append("<b>").append(newUri).append("</b><br/>");
		//enctype="multipart/form-data"
		boolean isMultipart = false;
		if(params != null && params.trim().length() > 0) {//has parameters
			String [] paramArry = params.split(",");
			for(String p:paramArry) {
				String[] typeAndName = p.split(":");
				if(typeAndName.length == 2) {
					if(typeAndName[0].contains("File") || typeAndName[0].contains("MFile")) {//File or File[]
						isMultipart = true;
						break;
					}
				}
			}
		} 
		if(isMultipart) {
			if(startWithNumber(host)) {
				builder.append("<form ").append("action=\""+host+":"+port+"/"+app+module+"/"+function+"\"").append(" enctype=\"multipart/form-data\" method=\"POST\">");
			} else {
				builder.append("<form ").append("action=\""+host+"/"+app+module+"/"+function+"\"").append(" enctype=\"multipart/form-data\" method=\"POST\">");
			}
		} else {
			if(startWithNumber(host)) {
				builder.append("<form ").append("action=\""+host+":"+port+"/"+app+module+"/"+function+"\"").append(" method=\"POST\">");
			} else {
				builder.append("<form ").append("action=\""+host+"/"+app+module+"/"+function+"\"").append(" method=\"POST\">");
			}
		}
		
		builder.append("<table>");
		if(params != null && params.trim().length() > 0) {//has parameters
			//&params=java.lang.Integer:issueId,java.lang.String:content,java.lang.Integer:page,int:rowsPerPage
			String [] paramArry = params.split(",");
			for(String p:paramArry) {
				String[] typeAndName = p.split(":");
				if(typeAndName.length == 2) {
					String type = typeAndName[0];
					String name = typeAndName[1];
					if(type.equals("HttpServletRequest")) {
						continue;
					}
					if(type.equals("HttpServletResponse")) {
						continue;
					}
					if(type.equals("ModelMap")) {
						continue;
					}
					if(type.equals("File") || type.equals("MFile")) {
						builder.append("<tr><td>").append(name).append("</td><td>").append("<input type=\"file\" name=\"").append(name).append("\"/></td></tr>");
					} else if(type.equals("File[]") || type.equals("MFile[]")) {
						for(int i=0;i<5;i++) {
							builder.append("<tr><td>").append(name).append("</td><td>").append("<input type=\"file\" name=\"").append(name).append("\"/></td></tr>");
						}
					} else {
						builder.append("<tr><td>").append(name).append("</td><td>").append("<input type=\"text\" name=\"").append(name).append("\"/></td></tr>");
					}
				}
			}
		} 
		
//		else {//no parameters
//			
//		}
		builder.append("<tr><td colspan=2 align=right><input type=\"submit\" value=\"提交\"/></td></tr></table>");
		builder.append("</form>");
		return builder;
	}
	//domain=/api/invokeForm
	public static final void showHtml(StringBuilder builder,ControllerMeta controllerMeta,String domain) {
		builder.append("*----------------------------------------------------------------------<br/>");
		builder.append("|<b>").append(controllerMeta.getName()).append("</b><b style='color:RED;font-weight:bold;'>(/"+controllerMeta.getMapping()+")</b><br/>");
		for(FunctionMeta functionMeta:controllerMeta.getFunctionMetas()) {
			String mapping = functionMeta.getMapping();
			if(mapping != null && (mapping.trim().equals("") || mapping.trim().equals("/"))) {
				mapping = DEFAULT_METHOD;
			}
			builder.append("|&nbsp;&nbsp;");
			TypeMeta [] fullParameters = functionMeta.getParameters();
			StringBuilder methodTypeString = new StringBuilder();
			for(int i = 0; i < fullParameters.length; i++) {
				TypeMeta parameterMeta = fullParameters[i];
				methodTypeString.append(getSimpleName(parameterMeta.getTypeName())).append(":").append(parameterMeta.getName());
				if(i < fullParameters.length - 1) {
					methodTypeString.append(",");
				}
			}
			if(mapping.equals(DEFAULT_METHOD)) {
				builder.append("<a target=\"_blank\" style='color:RED;font-weight:bold;' href=\""+domain+"/"+controllerMeta.getMapping()+"/?params="+methodTypeString+"\">").append(mapping).append("</a>");
			} else {
				builder.append("<a target=\"_blank\" href=\""+domain+"/"+controllerMeta.getMapping()+"/"+mapping+"?params="+methodTypeString+"\">").append(mapping).append("</a>");
			}
			builder.append("(");
			for(int i = 0; i < fullParameters.length; i++) {
				TypeMeta parameterMeta = fullParameters[i];
				builder.append(getSimpleName(parameterMeta.getTypeName())).append(" ").append(parameterMeta.getName());
				if(i < fullParameters.length - 1) {
					builder.append(",");
				}
			}
			
			builder.append(")<br/>");
		}
		builder.append("*----------------------------------------------------------------------<br/>");
	}

	public static void main(String[] args) {
		System.err.println(isSimpleType(String.class));
	}
}
