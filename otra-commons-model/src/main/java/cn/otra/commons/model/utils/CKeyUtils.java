package cn.otra.commons.model.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.otra.commons.annotation.CKey;
import cn.otra.commons.annotation.Mark;
import cn.otra.commons.model.PairVO;

public class CKeyUtils {

	private static final List<PairVO> emptyList = new ArrayList<PairVO>();
	public static final List<PairVO> getList(Object obj) {
		if(obj == null) {
			return emptyList;
		}
		List<PairVO> list = new ArrayList<PairVO>();
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field f:fields) {
			CKey key = f.getAnnotation(CKey.class);
			if(key != null) {
				try {
					f.setAccessible(true);
					Object value = f.get(obj);
					if(value != null) {
						list.add(new PairVO(f.getName(),key.value(),value.toString()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		return list;
	}
	
	public static final List<PairVO> getMarkList(Object obj) {
		if(obj == null) {
			return emptyList;
		}
		List<PairVO> list = new ArrayList<PairVO>();
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field f:fields) {
			Mark mark = f.getAnnotation(Mark.class);
			try {
				f.setAccessible(true);
				Object value = f.get(obj);
				if(value != null) {
					if(List.class.isAssignableFrom(f.getType())) {
						PairVO pairVO = new PairVO();
						pairVO.setName(f.getName());
						pairVO.setShowKey(mark.value());
						pairVO.setSubList(new ArrayList<PairVO>());
						for(Object listObj:(List)value) {
							//TODO
//							pairVO.getSubList().add(getMarkList(listObj));
						}
					} else {
						list.add(new PairVO(f.getName(),mark.value(),value.toString()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
