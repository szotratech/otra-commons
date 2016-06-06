package cn.otra.commons.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
	
	private static final File tempDir;
	static {
		String classPathDir = ImageUtils.class.getClass().getResource("/").getFile();
		if(classPathDir != null) {
			if(!classPathDir.endsWith("/")) {
				classPathDir = classPathDir+"/";
			}
		}
		tempDir = new File(classPathDir+"temp/images/");
		if(!tempDir.exists()) {
			tempDir.mkdirs();
		}
	}
	
	public static File getImageFromBase64String(String imageData,String... suffix) {
		FileOutputStream fos = null;		
		File file = null;
		try {
			
			String suffixStr = null;
			if(suffix != null && suffix.length > 0) {
				suffixStr = suffix[0].toLowerCase();
				if(!suffixStr.startsWith(".")) {
					suffixStr = '.'+suffixStr;
				}
			} else {
				suffixStr = ".jpg";
			}
			if(tempDir != null) {
				file = File.createTempFile(UUIDUtils.getUUID(), suffixStr,tempDir);
			} else {
				file = File.createTempFile(UUIDUtils.getUUID(), suffixStr);
			}
			
			fos = new FileOutputStream(file);
			byte data[] = Base64Cache.getBase64().decodeFast(imageData);
			fos.write(data);
			return file;
		} catch (Exception e) {
			throw new RuntimeException("",e);
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static final String getStringFromImage(byte[] bytes) { 
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		// 读取图片字节数组
		// 对字节数组Base64编码
		return Base64Cache.getBase64().encodeToString(bytes, false);// 返回Base64编码过的字节数组字符串
	}
	
	public static final String getStringFromImage(File imageFile) { 
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imageFile);
			data = new byte[in.available()];
			in.read(data);
		} catch (IOException e) {
			throw new RuntimeException("",e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		// 对字节数组Base64编码
		return Base64Cache.getBase64().encodeToString(data, false);// 返回Base64编码过的字节数组字符串
	}
	
	/**
	 * 创建临时文件
	 * @param bytes
	 * @param suffix 文件后缀
	 * @return
	 */
	public static final File createTempFile(byte[] bytes,String suffix) {
		if(bytes == null) {
			throw new RuntimeException("bytes is null");
		}
		if(suffix == null || suffix.trim().length() == 0) {
			throw new RuntimeException("suffix is null");
		}
		FileOutputStream fos = null;
		try {
			File dest = null;// File.createTempFile(UUIDUtils.getUUID(), ".jpg");
			if(tempDir != null) {
				dest = File.createTempFile(UUIDUtils.getUUID(), suffix,tempDir);
			} else {
				dest = File.createTempFile(UUIDUtils.getUUID(), suffix);
			}
			fos = new FileOutputStream(dest);
			fos.write(bytes);
			return dest;
		} catch (Exception e) {
			throw new RuntimeException("",e);
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
		
	}
	
	public static final void moveFile(File srcFile,String destName) {
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		InputStream in = null;
		byte[] data = new byte[1024];
		File dest = new File(destName);
		FileOutputStream fos = null;
		// 读取图片字节数组
		try {
			fos = new FileOutputStream(dest);
			in = new FileInputStream(srcFile);
			int len = -1;
			while((len = in.read(data)) != -1) {
				fos.write(data,0,len);
			}
			fos.flush();
		} catch (IOException e) {
			throw new RuntimeException("",e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static void main(String[] args) {
		File file = new File("d:/a.jpg");
		String imageString = ImageUtils.getStringFromImage(file);
		System.err.println(imageString);
		File imageFile = ImageUtils.getImageFromBase64String(imageString);
		ImageUtils.moveFile(imageFile, "d:/b.jpg");
		
//		System.err.println(Base64.encode("123456"));
	}
	
}
