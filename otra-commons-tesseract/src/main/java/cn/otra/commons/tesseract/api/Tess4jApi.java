package cn.otra.commons.tesseract.api;

import java.io.File;

public interface Tess4jApi {
	String parse(File image);
	String parse(String imageData);
	String parse(String imageData,String suffix);
	String parse(String imageData,String suffix, Boolean isGray, Boolean isBinary,Integer eraseBorderValue);
	String parse(File file, Boolean isGray, Boolean isBinary,Integer eraseBorderValue);
	String parse(File file, String suffix, Boolean isGray, Boolean isBinary,Integer eraseBorderValue);
}
