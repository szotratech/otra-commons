package cn.otra.commons.web.meta;

import java.util.Map;

import cn.otra.commons.web.meta.vo.TypeMetaVO;

public class TypeMeta {

	private String typeName;
	private Class<?> type;
	private String name;
	private String mark;// 参数说明
	private Map<String, String> marks;// 参数说明
	private Boolean required;//是否必须

	@SuppressWarnings("unchecked")
	public TypeMeta(Class<?> type, String typeName, String name, Object mark) {
		super();
		this.type = type;
		this.typeName = typeName;
		this.name = name;
		if (mark != null) {
			if (mark instanceof String) {
				this.mark = (String) mark;
			}
			if (mark instanceof Map) {
				this.marks = (Map<String, String>) mark;
			}
		}
	}

	public TypeMetaVO toVO() {
		TypeMetaVO vo = new TypeMetaVO(type.getName(), name, mark,marks,required);
		return vo;
	}

	public void setMarks(Map<String, String> marks) {
		this.marks = marks;
	}

	public Map<String, String> getMarks() {
		return marks;
	}

	public Class<?> getType() {
		return type;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

}
