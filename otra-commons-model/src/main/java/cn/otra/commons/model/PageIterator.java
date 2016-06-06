package cn.otra.commons.model;

import java.io.Serializable;

public interface PageIterator<T extends Serializable> {

	boolean hasNext();
	
	ECPage<T> next();
	
}
