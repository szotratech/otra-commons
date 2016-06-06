package cn.otra.commons.web.meta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.otra.commons.web.meta.vo.ControllerMetaVO;
import cn.otra.commons.web.meta.vo.FunctionMetaVO;

public class ControllerMeta implements Serializable {

	private static final long serialVersionUID = -8037032645185551069L;
	private String name;
	private String mapping;
	private FunctionMeta[] functionMetas;
	private Map<String, FunctionMeta> functionMap;
	private Object dest;

	public ControllerMeta(String name, String mapping,
			FunctionMeta[] functionMetas) {
		super();
		this.name = name;
		this.mapping = mapping;
		this.functionMetas = functionMetas;
		functionMap = new HashMap<String, FunctionMeta>();
		for(FunctionMeta fn:functionMetas) {
			functionMap.put(fn.getMapping(), fn);
		}
	}
	
	public ControllerMetaVO toVO() {
		FunctionMetaVO [] functionMetaVOs = new FunctionMetaVO[this.functionMetas.length];
		for(int i=0;i<this.functionMetas.length;i++) {
			functionMetaVOs[i] = this.functionMetas[i].toVO();
		}
		ControllerMetaVO vo = new ControllerMetaVO(this.name, this.mapping, functionMetaVOs);
		return vo;
	}
	
	public FunctionMeta getFunctionMeta(String methodName) {
		return functionMap.get(methodName);
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

	public FunctionMeta[] getFunctionMetas() {
		return functionMetas;
	}

	public void setFunctionMetas(FunctionMeta[] functionMetas) {
		this.functionMetas = functionMetas;
	}

	public Map<String, FunctionMeta> getFunctionMap() {
		return functionMap;
	}

	public void setFunctionMap(Map<String, FunctionMeta> functionMap) {
		this.functionMap = functionMap;
	}

	public Object getDest() {
		return dest;
	}

	public void setDest(Object dest) {
		this.dest = dest;
	}

}
