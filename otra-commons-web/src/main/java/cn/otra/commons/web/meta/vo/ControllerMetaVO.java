package cn.otra.commons.web.meta.vo;

import java.io.Serializable;

public class ControllerMetaVO implements Serializable {

	private static final long serialVersionUID = -8037032645185551069L;
	private String name;
	private String mapping;
	private FunctionMetaVO[] functionMetas;

	public ControllerMetaVO() {
	}
	public ControllerMetaVO(String name, String mapping, FunctionMetaVO[] functionMetas) {
		super();
		this.name = name;
		this.mapping = mapping;
		this.functionMetas = functionMetas;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public FunctionMetaVO[] getFunctionMetas() {
		return functionMetas;
	}

	public void setFunctionMetas(FunctionMetaVO[] functionMetas) {
		this.functionMetas = functionMetas;
	}

}
