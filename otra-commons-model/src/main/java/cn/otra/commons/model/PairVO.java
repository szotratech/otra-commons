package cn.otra.commons.model;

import java.io.Serializable;
import java.util.List;

public class PairVO implements Serializable {

	private static final long serialVersionUID = 3380364556819128420L;
	private String name;
	private String showKey;
	private Serializable showValue;
	
	//子信息
	private List<PairVO> subList;
	
	public PairVO() {
		super();
	}

	public PairVO(String name, String showKey, Serializable showValue) {
		super();
		this.name = name;
		this.showKey = showKey;
		this.showValue = showValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShowKey() {
		return showKey;
	}

	public void setShowKey(String showKey) {
		this.showKey = showKey;
	}

	public Serializable getShowValue() {
		return showValue;
	}

	public void setShowValue(Serializable showValue) {
		this.showValue = showValue;
	}

	public List<PairVO> getSubList() {
		return subList;
	}

	public void setSubList(List<PairVO> subList) {
		this.subList = subList;
	}

}
