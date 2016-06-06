package cn.otra.commons.web;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class UploadUtils {
	
	public static final String getUUIDFileName(File file) {
		String origFileName = file.getName();//原文件名
		String endFix = origFileName.indexOf(".") != -1 ? origFileName.substring(origFileName.lastIndexOf("."),origFileName.length()) : null;
		String newFileName = UUID.randomUUID().toString().replace("-", "")+endFix;
		return newFileName;
	}
	
	public static final String getUploadDir(String model) {
		return "upload/"+model+"/"+new SimpleDateFormat("yyyy/MM/dd").format(new Date())+"/";
	}
	
	/**
	 * 
	 * @param origFileName 原文件名
	 * @return
	 */
	public static final String getUUIDFileName(String origFileName) {
		String endFix = origFileName.indexOf(".") != -1 ? origFileName.substring(origFileName.lastIndexOf("."),origFileName.length()) : null;
		String newFileName = UUID.randomUUID().toString().replace("-", "")+endFix;
		return newFileName;
	}
	
	public static void main(String[] args) {
		System.err.println(getUploadDir("orgInfo")+getUUIDFileName(new File("d:/a.txt")));
	}
	
	
}
