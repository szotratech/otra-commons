package cn.otra.commons.web;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.otra.commons.web.meta.ControllerMeta;
import cn.otra.commons.web.meta.FunctionMeta;
import cn.otra.commons.web.meta.TypeMeta;

public class ApiUtils {

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
				|| clazz == Date.class) {
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
	
	//uri=/api/invokeForm/user/login
	public static final StringBuilder getMethodInvokeForm(String host,Integer port,String uri,String params) {
		String [] arrys = uri.split("/");
		String module = arrys[3];
		String function = arrys[4];
		StringBuilder builder = new StringBuilder();
		String newUri = "/"+arrys[1]+"/"+module+"/"+function;
		builder.append("<b>").append(newUri).append("</b><br/>");
		//enctype="multipart/form-data"
		boolean isMultipart = false;
		if(params != null && params.trim().length() > 0) {//has parameters
			String [] paramArry = params.split(",");
			for(String p:paramArry) {
				String[] typeAndName = p.split(":");
				if(typeAndName.length == 2) {
					if(typeAndName[0].contains("File")) {//File or File[]
						isMultipart = true;
						break;
					}
				}
			}
		} 
		if(isMultipart) {
			builder.append("<form ").append("action=\"http://"+host+":"+port+"/api/"+module+"/"+function+"\"").append(" enctype=\"multipart/form-data\" method=\"POST\">");
		} else {
			builder.append("<form ").append("action=\"http://"+host+":"+port+"/api/"+module+"/"+function+"\"").append(" method=\"POST\">");
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
					if(type.equals("File")) {
						builder.append("<tr><td>").append(name).append("</td><td>").append("<input type=\"file\" name=\"").append(name).append("\"/></td></tr>");
					} else if(type.equals("File[]")) {
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
		builder.append("|<b>").append(controllerMeta.getName()).append("</b><br/>");
		for(FunctionMeta functionMeta:controllerMeta.getFunctionMetas()) {
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
			builder.append("<a href=\""+domain+"/"+controllerMeta.getMapping()+"/"+functionMeta.getMapping()+"?params="+methodTypeString+"\">").append(functionMeta.getName()).append("</a>");
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
