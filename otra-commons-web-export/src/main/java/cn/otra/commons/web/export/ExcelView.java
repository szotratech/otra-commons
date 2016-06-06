package cn.otra.commons.web.export;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import cn.otra.commons.annotation.EcViewObject;
import cn.otra.commons.core.AnnUtils;
import cn.otra.commons.core.ReflectUtil;
import cn.otra.commons.model.ViewObjectMapping;

public class ExcelView extends AbstractExcelView {
	private static final Logger logger = Logger.getLogger(ExcelView.class);
	
	//key=requestMapping
	private static final Map<String, ViewObjectMapping> VIEW_OBJECT_MAP = new HashMap<String, ViewObjectMapping>();
	
	private String viewPackage;
	
	/**
	 * 初始化注解
	 */
	public ExcelView(Class<?> clazz,String viewPackage) {
		this(viewPackage);
	}
	
	public ExcelView(String viewPackage) {
		this.viewPackage = viewPackage;
		init();
	}
	
	public ExcelView() {}
	
	private void init() {
		Set<Class<?>> classes = null;
		classes = AnnUtils.getClasses(this.getClass().getClassLoader(),viewPackage);
//		System.err.println("ExcelView.initViewAnn, classes="+classes);
		for(Class<?> c:classes) {
			EcViewObject viewObject = c.getAnnotation(EcViewObject.class);
			if(viewObject == null) {
				continue;
			}
			if(viewObject.requestMapping() == null) {
				continue;
			}
			if(viewObject.requestMapping().length == 0) {
				continue;
			}
			for(int i=0,len=viewObject.requestMapping().length;i<len;i++) {
				String key = viewObject.requestMapping()[i];
				Field[] fields = ReflectUtil.getFields(c);
				ViewObjectMapping mapping = new ViewObjectMapping(viewObject.fileName()[i],viewObject.pageKey(),fields);
				VIEW_OBJECT_MAP.put(key, mapping);
			}
		}
	}
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		try {
			ViewObjectMapping viewObject = getViewObjectMapping(request);
			if(viewObject == null) {
				model.put("__excel__", "N");//导出失败
				return;
			}
			Object datas = model.get(viewObject.getDataKey());
			if(datas == null) {
				model.put("__excel__", "N");//没有数据或找不到数据，导出失败
				return;
			}
			
			//处理文件名
			processFileName(request, response, model, viewObject);
			
			ExportUtils.createExcel(workbook, viewObject, datas);
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}
	
	private ViewObjectMapping getViewObjectMapping(HttpServletRequest request) {
//		System.err.println("VIEW_OBJECT_MAP="+VIEW_OBJECT_MAP);
		String uriEndFix = request.getRequestURI();
		if(uriEndFix.contains(EXTENSION)) {
			uriEndFix = uriEndFix.replace(EXTENSION, "");
			int beginIndex = 0;
			char [] chars = uriEndFix.toCharArray();
			int count = 0;
			for(int i= chars.length - 1;i>0;i--) {
				if(chars[i] == '/') {
					count ++;
				}
				if(count == 2) {
					beginIndex = i;
					break;
				}
			}
			uriEndFix = uriEndFix.substring(beginIndex);
		}
		return VIEW_OBJECT_MAP.get(uriEndFix);
	}
	
	private void processFileName(HttpServletRequest request,
			HttpServletResponse response,Map<String, Object> model,ViewObjectMapping viewObject) throws UnsupportedEncodingException {
		//处理文件名
		String fileName = (String)model.get("_expFileName");
		if(StringUtils.hasText(fileName)) {
			fileName = fileName.trim(); 
		} else {
			fileName = viewObject.getFileName();
		}
		if(fileName != null) {
			fileName = URLEncoder.encode(fileName,request.getCharacterEncoding());
			if(fileName.length() > 150) {
				fileName = new String(fileName.getBytes(request.getCharacterEncoding()),"ISO8859-1");
			}
			response.addHeader("Content-disposition", "attachment; filename="+fileName+EXTENSION);
		}
	}
	
	public void setViewPackage(String viewPackage) {
		this.viewPackage = viewPackage;
		init();
	}
}
