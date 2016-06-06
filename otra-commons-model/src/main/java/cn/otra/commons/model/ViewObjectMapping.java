package cn.otra.commons.model;

import java.lang.reflect.Field;

public class ViewObjectMapping {
	private Field [] fields;
	private String fileName;
	private String dataKey;
	public ViewObjectMapping(String fileName,String dataKey,Field [] fields) {
		super();
		this.fileName = fileName;
		this.dataKey = dataKey;
		this.fields = fields;
	}
	public Field[] getFields() {
		return fields;
	}
	public void setFields(Field[] fields) {
		this.fields = fields;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDataKey() {
		return dataKey;
	}
	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}
	
}
