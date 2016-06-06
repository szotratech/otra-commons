/**
 * Author:     Dawn/邓支晓
 * Created on: 2012-7-26 上午10:15:04
 * Description:
 */
package cn.otra.commons.web.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import cn.otra.commons.annotation.EcField;
import cn.otra.commons.core.DateUtils;
import cn.otra.commons.core.DateUtils.TimeFormatter;
import cn.otra.commons.core.ReflectUtil;

public class ExcelUtil {

	private final static Logger logger = Logger.getLogger(ExcelUtil.class);

	private static ExcelUtil eu = new ExcelUtil();

	private ExcelUtil() {

	}

	public static ExcelUtil getInstance() {
		return eu;
	}

	// 获取Excel类型，是2003版本还是2007版本
	private static final boolean isOffice2003(String outPath) {
		boolean office2003 = true;
		if (outPath != null) {
			String fileType = outPath.substring((outPath.lastIndexOf(".") + 1));
			if (fileType.equals("xlsx"))
				office2003 = false;
		}
		return office2003;
	}

	public static final List<Map<String, Object>> readExcelFile(InputStream inputStream) {
		Workbook workbook = null;
		List<Map<String, Object>> resMap = new ArrayList<Map<String,Object>>();
		try {
			workbook = WorkbookFactory.create(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			// 得到总行数
			int totalRows = sheet.getLastRowNum();
			
			Row firstRow = sheet.getRow(0);
			
			Map<Integer, String> header = new HashMap<Integer, String>();
			int idx = 0;
			for (Cell cell : firstRow) {
				header.put(idx++, getCellValue(cell));
			}
			for(int i=1;i<=totalRows;i++) {
				Row row = sheet.getRow(i);
				idx = 0;
				Map<String, Object> dataMap = new HashMap<String, Object>();
				int cellSize = row.getLastCellNum();
				for(int k=0;k<cellSize;k++) {
					Cell cell = row.getCell(k,Row.CREATE_NULL_AS_BLANK);
					String cellKey = (String)header.get(k);
					if(StringUtils.hasText(cellKey)) {
						cellKey = cellKey.trim();
					}
					String cellValue = getCellValue(cell);
					if(StringUtils.hasText(cellValue)) {
						cellValue = cellValue.trim();
					}
					if(cellKey != null && cellKey.trim().length() > 0) {
						dataMap.put(cellKey, cellValue);
					}
				}
				resMap.add(dataMap);
			}
			
		} catch (Exception e) {
			logger.error("将Excel数据读取到对象集合中异常!", e);
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resMap;
	}
	
	public static final List<Map<String, Object>> readExcelFile(String path) {
		try {
			return readExcelFile(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("",e);
		}
	}
	
	public static void main(String[] args) {
//		List<Map<String, Object>> list = readExcelFile("D:/xiaodx/vinDatas/VIN1.xlsx");
//		Map<String, String> allVins = new HashMap<String, String>();
//		List<String> vins = new ArrayList<String>();
//		for(Map<String, Object> map:list) {
//			for(Map.Entry<String, Object> en:map.entrySet()) {
//				String vin = (String)en.getValue();
//				if(vin == null || vin.trim().length() == 0) {
//					continue;
//				}
//				vins.add(vin.trim());
//				allVins.put(vin.trim(), vin.trim());
//			}
//		}
//		
//		list = readExcelFile("D:/xiaodx/vinDatas/VIN2.xlsx");
//		for(Map<String, Object> map:list) {
//			for(Map.Entry<String, Object> en:map.entrySet()) {
//				String vin = (String)en.getValue();
//				if(vin == null || vin.trim().length() == 0) {
//					continue;
//				}
//				vins.add(vin.trim());
//				allVins.put(vin.trim(), vin.trim());
//			}
//		}
//		saveData(allVins, "D:/xiaodx/vinDatas/vinsNoRepeat.data");
//		saveData(vins, "D:/xiaodx/vinDatas/allVin.data");
		
//		DecimalFormat df = new DecimalFormat("0");  
//		String whatYourWant = df.format(cell.getNumericCellValue());  
		
		List<Map<String, Object>> list = readExcelFile("C:/Users/bkc-01/Desktop/tpl/人保微信开通违章提醒用户模板.xls");
		System.err.println(list);
	}
	
	private static final void saveData(List<String> vins,String file) {
		BufferedWriter writer = null;//
		try {
			writer = new BufferedWriter(new FileWriter(file,true));
			int idx = 0;
			for(String vin:vins) {
				idx ++;
				writer.newLine();
				writer.write(vin);
				if(idx %100 == 0) {
					writer.flush();
				}
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static final void saveData(Map<String, String> allVins,String file) {
		BufferedWriter writer = null;//
		try {
			writer = new BufferedWriter(new FileWriter(file,true));
			int idx = 0;
			for(Map.Entry<String, String> en:allVins.entrySet()) {
				idx ++;
				String vin = en.getValue();
				writer.newLine();
				writer.write(vin);
				if(idx %100 == 0) {
					writer.flush();
				}
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static final String getCellValue(Cell cell) {
		String str = null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			str = "";
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			str = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			str = cell.getCellFormula();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if(HSSFDateUtil.isCellDateFormatted(cell)) {
				str = DateUtils.dateToString(cell.getDateCellValue(), TimeFormatter.FORMATTER1);
			} else {
				DecimalFormat df = new DecimalFormat("0");  
				str = df.format(cell.getNumericCellValue());  
//				str = String.valueOf(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING:
			str = cell.getStringCellValue();
			break;
		default:
			str = null;
			break;
		}
		return str;
	}


	/**
	 * 
	 * @param title excel标题
	 * @param entity 导出实体类名称
	 * @param dataset 导出的数据列表
	 * @param exportPath 导出目标文件地址
	 * @param pattern 日期格式
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static final <T> void exportExcelFromObjects(String title, Class<T> entity, List<T> dataset, String exportPath, String... pattern) throws Exception {
		if(dataset.size() == 0) {
			logger.warn("no data to export.");
		}
		OutputStream out = null;
		try {
			boolean isXSSF = isOffice2003(exportPath);
			Workbook workbook = null;
			if (isXSSF) {
				workbook = new HSSFWorkbook();
			} else {
				workbook = new XSSFWorkbook();
			}
			// 生成一个表格
			Sheet sheet = workbook.createSheet(title);
	
			//1.产生表格标题行
			Row row = sheet.createRow(0);
			Field[] fs = ReflectUtil.getFields(entity);
			int columnIndex = 0;
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				String fcomment = f.getName();
				EcField ecTitle = f.getAnnotation(EcField.class);
				if (ecTitle != null) {
					fcomment = ecTitle.value();
				}
				if (Modifier.isFinal(f.getModifiers())) {
					continue;
				}
				Cell cell = row.createCell(columnIndex++);
				RichTextString text = null;//new HSSFRichTextString(fcomment);
				if(isOffice2003(exportPath)) {
					text = new HSSFRichTextString(fcomment);
				} else {
					text = new XSSFRichTextString(fcomment);
				}
				cell.setCellValue(text);
			}
			
			//2.数据
			int index = 0;
			for(int didx =0;didx<dataset.size();didx++) {
				T t = dataset.get(didx);
				index++;
				row = sheet.createRow(index);
				Map<String, Field> fields = ReflectUtil.getFieldMap(t.getClass());
				columnIndex = 0;
				for (int i = 0; i < fs.length; i++) {
					Field f = fs[i];
					if (Modifier.isFinal(f.getModifiers())) {
						continue;
					}
					String col = f.getName();
					Field field = fields.get(col);
					
					Cell cell = row.createCell(columnIndex++);
					// cell.setCellStyle(style2);
					field.setAccessible(true);
					Object value = field.get(t);
					String textValue = null;
					if (value != null) {
						if (value instanceof Date) {
							Date date = (Date) value;
							String timeFormatter = null;
							if(pattern.length == 0) {
								timeFormatter = DateUtils.TimeFormatter.FORMATTER1;
							} else {
								timeFormatter = pattern[0];
							}
							SimpleDateFormat sdf = new SimpleDateFormat(timeFormatter);
							textValue = sdf.format(date);
						}  else {
							// 其它数据类型都当作字符串简单处理
							textValue = value.toString();
						}
					}
	
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null) {
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
						} else {
							RichTextString richString = null;//new HSSFRichTextString(textValue);
							if(isOffice2003(exportPath)) {
								richString = new HSSFRichTextString(textValue);
							} else {
								richString = new XSSFRichTextString(textValue);
							}
							cell.setCellValue(richString);
						}
					}
				}
			}
			out = new FileOutputStream(exportPath);
			workbook.write(out);
		} finally {
			if (out != null) {
				try {out.close();} catch (IOException e) {}
			}
		}
	}
}
