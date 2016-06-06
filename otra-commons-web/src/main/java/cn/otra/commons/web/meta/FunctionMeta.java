package cn.otra.commons.web.meta;

import java.lang.reflect.Method;

import cn.otra.commons.web.meta.vo.FunctionMetaVO;
import cn.otra.commons.web.meta.vo.TypeMetaVO;

public class FunctionMeta {

	private String name;
	private String mapping;
	private TypeMeta returnType;
	private TypeMeta[] parameters;
	private Method method;
	private String mark;
	
	public FunctionMeta(String name, String mapping, TypeMeta returnType, TypeMeta[] parameters, Method method,String mark) {
		super();
		this.name = name;
		this.mapping = mapping;
		this.returnType = returnType;
		this.parameters = parameters;
		this.method = method;
		this.mark = mark;
		
	}
	
	public FunctionMetaVO toVO() {
		TypeMetaVO [] typeMetaVOs = new TypeMetaVO[parameters.length];
		for(int i=0;i<typeMetaVOs.length;i++) {
			typeMetaVOs[i] = parameters[i].toVO();
		}
		FunctionMetaVO vo = new FunctionMetaVO(name, mapping, returnType.toVO(), typeMetaVOs,mark);
		return vo;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeMeta getReturnType() {
		return returnType;
	}

	public void setReturnType(TypeMeta returnType) {
		this.returnType = returnType;
	}

	public TypeMeta[] getParameters() {
		return parameters;
	}

	public void setParameters(TypeMeta[] parameters) {
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
