package cn.otra.commons.web.export;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import cn.otra.commons.annotation.EcField;
import cn.otra.commons.core.DateUtils;
import cn.otra.commons.core.DateUtils.TimeFormatter;
import cn.otra.commons.core.NumberUtils;
import cn.otra.commons.core.ReflectUtil;
import cn.otra.commons.model.ECPage;
import cn.otra.commons.model.EcExpression;
import cn.otra.commons.model.ViewObjectMapping;

public class ExportUtils {
	private static final Map<String, EcExpression> EXPRESSION_MAP = new ConcurrentHashMap<String, EcExpression>();
	
	public static final void createExcel(Workbook workbook,ViewObjectMapping viewObject,Object datas) throws IllegalArgumentException, IllegalAccessException {
		Sheet sheet = workbook.createSheet("sheet 1");
		
		Row row = null;
		Cell cell = null;
		int r = 0;
		int c = 0;
		
		//Style for header cell
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		
		//Create header cells
		row = sheet.createRow(r++);
		
		for (Field f:viewObject.getFields()) {
			String fcomment = null;
			EcField ecTitle = f.getAnnotation(EcField.class);
			
			if (ecTitle != null) {
				fcomment = ecTitle.value();
			} 
			if (Modifier.isFinal(f.getModifiers())) {
				continue;
			}
			int cc = c++;
			cell = row.createCell(cc);
			cell.setCellStyle(style);
			cell.setCellValue(fcomment);
		}
		
		if(datas instanceof ECPage) {//page
			ECPage<?> page = (ECPage<?>) datas;
			processList(page.getList(), page, viewObject, sheet, r, c);
		} else {
 			List<?> list = (List<?>) datas;
			processList(list, null, viewObject, sheet, r, c);
		}
	}
	
	private static final void processList(List<?> list,ECPage<?> page,ViewObjectMapping viewObject,Sheet sheet, int r,int c) throws IllegalArgumentException, IllegalAccessException {
		Map<String, Field> datasFieldMap = null;
		for(int i=0;i<list.size();i++){
			Object obj = list.get(i);
			if(datasFieldMap == null) {
				datasFieldMap  = ReflectUtil.getFieldMap(obj.getClass());
			}
			Row row = sheet.createRow(r++);
			c = 0;
			
			//sn
			int sn = (i+1);
			for (Field f:viewObject.getFields()) {
				if (Modifier.isFinal(f.getModifiers())) {
					continue;
				}
				if("serialNo".equals(f.getName())) {
					if(page != null) {
						sn = (i+1)+page.getRowsPerPage()*(page.getCurrentPage() - 1);
					}
					row.createCell(c++).setCellValue(sn);
					continue;
				}
 				Field dataField = datasFieldMap.get(f.getName());
				dataField.setAccessible(true);
				Object value = dataField.get(obj);
				EcField ecTitle = f.getAnnotation(EcField.class);
				if(StringUtils.hasText(ecTitle.replace())) {
					value = replace(f, value);
				}
				if(value != null) {
					if (value instanceof Date) {
						Date date = (Date) value;
						if(ecTitle.type() != null && ecTitle.type().equalsIgnoreCase("date")) {
							value = DateUtils.dateToString(date, TimeFormatter.FORMATTER2);
						} else {
							value = DateUtils.dateToString(date, TimeFormatter.FORMATTER1);
						}
					}
				}
				if(value != null && value instanceof Number) {
					Double dVal = NumberUtils.objectToDouble(value);
					row.createCell(c++).setCellValue(dVal);
				} else {
					row.createCell(c++).setCellValue(value==null?null:value.toString());
				}
				
			}
		}
	
		
		//调整列宽
		for (int i=0;i<viewObject.getFields().length;i++) {
			Field f = viewObject.getFields()[i];
			EcField ecTitle = f.getAnnotation(EcField.class);
			if(ecTitle != null && ecTitle.width() > 0) {
				sheet.setColumnWidth(i, ecTitle.width()*256);
			} else {
				String cValue = sheet.getRow(0).getCell(i).getStringCellValue();
				if(StringUtils.hasText(cValue)) {
					sheet.setColumnWidth(i, (cValue.length()+2)*2*256);
				} else {
					sheet.autoSizeColumn(i);
				}
			}
		}
	}
	
	private static final Object replace(Field field,Object value) {
		if(value == null) {
			return null;
		}
		EcField ecTitle = field.getAnnotation(EcField.class);
		if(ecTitle != null && ecTitle.replace().length()>0) {
			EcExpression expression = EXPRESSION_MAP.get(ecTitle.replace());
			if(expression == null) {
				expression = new EcExpression(ecTitle.replace());
				EXPRESSION_MAP.put(ecTitle.replace(), expression);
			}
			return expression.get(value);
		}
		return value;
	}
}
