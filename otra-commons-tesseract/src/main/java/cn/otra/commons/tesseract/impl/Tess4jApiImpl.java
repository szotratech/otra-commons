package cn.otra.commons.tesseract.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import cn.otra.commons.core.ImageUtils;
import cn.otra.commons.core.UUIDUtils;
import cn.otra.commons.tesseract.api.Tess4jApi;

public class Tess4jApiImpl implements Tess4jApi  {

	private static final Logger LOG = Logger.getLogger(Tess4jApiImpl.class);
	private String tessPath;//windows 要指定
	
	@PostConstruct
	public void init() {
		if(tessPath == null) {
			tessPath = "";
		}
		if(tessPath != null && !tessPath.endsWith("/")) {
			tessPath = tessPath+"/";
		}
		String osName = System.getProperty("os.name");
		System.err.println("************osName=["+osName+"]");
		if(osName.startsWith("Windows")) {
			System.setProperty("jna.library.path", "32".equals(System.getProperty("sun.arch.data.model")) ? tessPath+"lib/win32-x86" : tessPath+"lib/win32-x86-64");
		}
		System.err.println("************jna.library.path="+System.getProperty("jna.library.path"));
	}
	
	@Override
	public String parse(File image) {
		if(image == null || !image.exists()) {
			LOG.error("image is null or not exists! image="+(image==null?image:image.getAbsoluteFile()));
			return null;
		}
		try {
			Tesseract instance = new Tesseract();
			if(tessPath != null && tessPath.trim().length() > 0) {
				instance.setDatapath(tessPath+"tessdata");
			}
			String code = instance.doOCR(image);
			if(code != null) {
				code = code.trim();
			}
			return code;
		} catch (TesseractException e) {
			e.printStackTrace();;
		} finally {
			if(image != null && image.exists()) {
				image.delete();
			}
		}
		return null;
	}
	
	@Override
	public String parse(String imageData,String suffix) {
		File file = ImageUtils.getImageFromBase64String(imageData, suffix);
		return parse(file);
	}
	
	private static String getFileSuffix(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".")+1);
	}
	
	@Override
	public String parse(File file, Boolean isGray, Boolean isBinary, Integer eraseBorderValue) {
		String fileName = file.getName();
		String suffixName = getFileSuffix(fileName).toLowerCase();
		return parse(file, suffixName, isGray, isBinary, eraseBorderValue);
	}
	
	@Override
	public String parse(String imageData) {
		return parse(imageData, "jpg");
	}
	
	@Override
	public String parse(String imageData, String suffix, Boolean isGray,
			Boolean isBinary, Integer eraseBorderValue) {
		File file = ImageUtils.getImageFromBase64String(imageData, suffix);
		return parse(file, suffix, isGray, isBinary, eraseBorderValue);
	}

	@Override
	public String parse(File file, String suffix, Boolean isGray,
			Boolean isBinary, Integer eraseBorderValue) {
		String uuidName = UUIDUtils.getUUID();
		if(!suffix.startsWith(".")) {
			suffix = suffix.replace(".", "");
		}
		try {
			if (eraseBorderValue > 0) {
				BufferedImage image = ImageIO.read(file);
				int width = image.getWidth();
				int height = image.getHeight();
				image = image.getSubimage(eraseBorderValue, eraseBorderValue, width-2*eraseBorderValue, height-2*eraseBorderValue);
				file.delete();
				file = File.createTempFile(uuidName, "."+suffix);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				ImageIO.write(image, suffix, fileOutputStream);
			}

			if (isGray) {
				BufferedImage image = ImageIO.read(file);
				int width = image.getWidth();
				int height = image.getHeight();
				BufferedImage grayImage = new BufferedImage(width, height,
						BufferedImage.TYPE_BYTE_GRAY);
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int rgb = image.getRGB(i, j);
						grayImage.setRGB(i, j, rgb);
					}
				}
				file.delete();
				file = File.createTempFile(uuidName, "."+suffix);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				ImageIO.write(grayImage, suffix, fileOutputStream);
			}

			if (isBinary) {
				BufferedImage image = ImageIO.read(file);
				int width = image.getWidth();
				int height = image.getHeight();
				BufferedImage binaryImage = new BufferedImage(width, height,
						BufferedImage.TYPE_BYTE_BINARY);
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int rgb = image.getRGB(i, j);
						binaryImage.setRGB(i, j, rgb);
					}
				}
				file.delete();
				file = File.createTempFile(uuidName, "."+suffix);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				ImageIO.write(image, suffix, fileOutputStream);
			}
			return parse(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setTessPath(String tessPath) {
		this.tessPath = tessPath;
	}
	
	public static void main(String[] args) {
		String osName = System.getProperty("os.name");
		System.err.println(osName);
		
		Tess4jApiImpl tess4jApi = new Tess4jApiImpl();
		tess4jApi.setTessPath("D:/xiaodx/softs/apache-servicemix-5.4.0/tess");
		tess4jApi.init();
		String image = "/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUADwDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD1xpXEiBkYAHLFPmHQ44HOPw7Vy1x4zf8A4TS10WzSKa1YSiecqxIkVC3ljoMrgZ68MBwQa3bu4uhbSSwGJbpVKRiUEx7z/exzjI6j0I+vma6f4k0XX/D9p5ukSXivdyxTs0h812XMhmYgEtjAHfgZqoRTvc1hFPc9D1/UbyLT0fSLaO8uCx+VpRHsGD8xyRnnjHU57VW8P6zda5o1jqFx5Uc0vmAiJSAMPgYBbnoOP06VY1nU9M0m2gvL2YpbSOFC7SeSD02jPGP0A9K5z4eanYS6Nb6ckqyX0auzwFWA2mQ9SRg4yp49fywckppXJ6GlqOpeJLV7m6zpwsYS0nlF2EzIpPoCASBkA9Nwz0NP1jX7vSfCn9sWiB98McsaTDKgMyDkDGOG6ZwPU1l3/iHSb7W57XU7o21jZy7fs0kTyG4kUkZcjPyA9Fzg4yfStrWfsE/htJ75Hn0qWNGl8pWOEJUhyBhsZAY4xwD366McLcyuLB/wlgu4m1RdFNmCRKLXzfMIIIwM8dcVoyeeZGy04IOCY0+97nBHP9MVwuiS2f8Awk1snhR7p9LjVnv41LiDJ+4QJCTvJAHHYDHAavRPOkyWhiEqNznIXHHTn8/xpIurHlf9IilUGURn7qyZx65KnB9vmP6VHdDyZvlJPlxhlzzjnA/LJxRRTMhJY1VokA+SSVgV7cNjj8OKJLWJJRwT++CEHjIIz2+tFFAA6g2BfL/Jt+XccNwp5H49qkiiSeXJVQWG4kAHsp757saKKAGWqq15NDgiNSSAGIwQfrV4W0QUKUVscAsMkD0oooBn/9k=";
		String code = tess4jApi.parse(image);
		System.err.println(code);
		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
