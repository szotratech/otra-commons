package cn.otra.commons.web.meta.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TypeMetaVO {

	private String typeName;
	private String name;
	private String mark;// 参数说明
	private List<KeyValue> marks;// 参数说明
	private Boolean required;//是否必须

	public TypeMetaVO() {
	}

	public TypeMetaVO(String typeName, String name, String mark,
			Map<String, String> marks,Boolean required) {
		super();
		this.typeName = typeName;
		this.name = name;
		this.mark = mark;
		this.marks = new ArrayList<KeyValue>();
		this.required = required;
		if (marks != null) {
			for (Map.Entry<String, String> en : marks.entrySet()) {
				this.marks.add(new KeyValue(en.getKey(), en.getValue()));
			}
			Collections.sort(this.marks, new Comparator<KeyValue>() {

				@Override
				public int compare(KeyValue o1, KeyValue o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});
		}
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

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
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

	public List<KeyValue> getMarks() {
		return marks;
	}

	public void setMarks(List<KeyValue> marks) {
		this.marks = marks;
	}

}
