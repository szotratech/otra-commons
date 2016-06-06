package cn.otra.commons.web.meta.vo;

public class FunctionMetaVO {

	private String name;
	private String mapping;
	private TypeMetaVO returnType;
	private TypeMetaVO[] parameters;
	private String mark;
	public FunctionMetaVO() {
	}
	public FunctionMetaVO(String name, String mapping, TypeMetaVO returnType, TypeMetaVO[] parameters,String mark) {
		super();
		this.name = name;
		this.mapping = mapping;
		this.returnType = returnType;
		this.parameters = parameters;
		this.mark = mark;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeMetaVO getReturnType() {
		return returnType;
	}

	public void setReturnType(TypeMetaVO returnType) {
		this.returnType = returnType;
	}

	public TypeMetaVO[] getParameters() {
		return parameters;
	}

	public void setParameters(TypeMetaVO[] parameters) {
		this.parameters = parameters;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}

}
