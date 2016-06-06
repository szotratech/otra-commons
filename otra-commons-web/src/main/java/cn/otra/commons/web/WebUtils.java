package cn.otra.commons.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.log4j.Logger;

import cn.otra.commons.web.meta.vo.MFile;

public class WebUtils {

	private static final String FILE_ITEM_UPLOAD_KEY = "com.bkc.osgi.commons.http.fileItemUpload";
	private static final String FILE_UPLOAD_KEY = "com.bkc.osgi.commons.http.fileupload";
	private static final String DEFAULT_ENCODING = "utf-8";
	private static final Map<String, Object[]> emptyParamMap = new HashMap<String, Object[]>();
	private static final Logger LOG = Logger.getLogger(WebUtils.class);
//	public static final Object getSimpleParameters(HttpServletRequest request,String name) {
//		if (!FileUploadBase.isMultipartContent(new ServletRequestContext(request))) {
//			return request.getParameter(name);
//		}
//		return null;
//	}
	
	/**
	 * 获取客户端IP，考虑nginx造成的影响
	 * @param request
	 * @return
	 */
	public static final String getRemoteIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	} 
	
	public static final Object getParameter(HttpServletRequest request,String name) {
		if (!FileUploadBase.isMultipartContent(new ServletRequestContext(request))) {
			return request.getParameter(name);
		}

		Map<String,FileItem[]> params = getFileItems(request);
		if (params == null) {
			params = check(request);
		}
		
		return getFiles(request).get(name);
	}
	
	public static final boolean isMultipartRequest(HttpServletRequest request) {
		return FileUploadBase.isMultipartContent(new ServletRequestContext(request));
	}
	
	private static final Map<String,FileItem[]> check(HttpServletRequest request) {
		Map<String,FileItem[]> params = getFileItems(request);
		Map<String, Object> multipartMap;
		if (params == null) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(256000);

			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(-1L);

			params = new HashMap<String,FileItem[]>();
			multipartMap = new HashMap<String, Object>();
			try {
				List<FileItem> items = upload.parseRequest(request);
//				System.err.println(" &&&&&&&&&&&&&&&&&&&& items.size="+items.size());
				for(FileItem item:items) {
//					System.err.println(" &&&&&&&&&&&&&&&&&&&& "+item.getFieldName()+" >> "+item.getString()+" >> "+item.isFormField());
					FileItem[] current = (FileItem[]) params.get(item.getFieldName());
					if (current == null) {
						current = new FileItem[] { item };
					} else {
						FileItem[] newCurrent = new FileItem[current.length + 1];
						System.arraycopy(current, 0, newCurrent, 0,current.length);
						newCurrent[current.length] = item;
						current = newCurrent;
					}
					params.put(item.getFieldName(), current);
				}
			} catch (FileUploadException fue) {
//				fue.printStackTrace();
			}
			
			for(Map.Entry<String, FileItem[]> en:params.entrySet()) {
				FileItem[] param = en.getValue();
				List<MFile> files = null;// = new File[param.length];
				List<String> strList = null;
//				Object []ps = new Object[param.length];
				for(int i=0;i<param.length;i++) {
					FileItem fileItem = param[i];
					String fileFieldName = null;
					if(fileItem.getContentType() != null) {//upload field
						fileFieldName = fileItem.getName();
						if(!isFileName(fileFieldName)) {
							continue;
						}
						if(files == null) {
							files = new ArrayList<MFile>();
						}
						MFile file = null;
						try {
//							file = File.createTempFile(getUUID(), getSuffix(fileItem.getName()));
							file = createTimepFile(fileItem);
							files.add(file);
						} catch (Exception e) {
							e.printStackTrace();
						}
						LOG.info(">>>>>>> write file ["+file.getFile().getAbsolutePath()+"] to dist.");
					} else {
						if(strList == null) {
							strList = new ArrayList<String>();
						}
						try {
							strList.add(fileItem.getString(DEFAULT_ENCODING));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
				if(files != null) {
					MFile [] fileArry = new MFile[files.size()];
					for(int k=0;k<fileArry.length;k++) {
						fileArry[k] = files.get(k);
					}
					multipartMap.put(en.getKey(), fileArry);
				}
				if(strList != null) {
					String [] strArry = new String[strList.size()];
					for(int k=0;k<strArry.length;k++) {
						strArry[k] = strList.get(k);
					}
					multipartMap.put(en.getKey(), strArry);
				}
			}
			request.setAttribute(FILE_ITEM_UPLOAD_KEY,params);
			request.setAttribute(FILE_UPLOAD_KEY,multipartMap);
		}
		return params;
	}
	
	private static final MFile createTimepFile(FileItem fileItem) throws Exception {
		String fileName = fileItem.getName();
//		String prefix = fileItem.getName().substring(0,fileItem.getName().lastIndexOf("."));
		String suffix = null;
		if(fileName == null || fileName.trim().length() == 0) {
			suffix = ".tmp";
		} else {
			suffix = fileName.substring(fileName.lastIndexOf("."));
		}
//		if(prefix.length()<3) {
//			prefix = prefix+"___";
//		}
		
		File dir = new File(WebUtils.class.getClass().getResource("/").getFile());// new File("./temp/files");
		dir = new File(dir,"temp/files");
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File file = null;
		if(dir.exists()) {
			file = File.createTempFile("otra-", suffix,dir);
		} else {
			file = File.createTempFile("otra-", suffix);
		}
		fileItem.write(file);
		MFile mFile = new MFile();
		mFile.setFileName(fileName);
		mFile.setFile(file);
		return mFile;//fileName.substring(fileName.lastIndexOf("."));
	}
	
	private static final boolean isFileName(String value) {
		if(value == null) {
			return false;
		}
		if(value.trim().length() == 0) {
			return false;
		}
		if(!value.trim().contains(".")) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private static final Map<String, Object[]> getFiles(HttpServletRequest request) {
		return (Map<String, Object[]>)request.getAttribute(FILE_UPLOAD_KEY);
	}
	
	@SuppressWarnings("unchecked")
	public static final Map<String, Object[]> getParameters(HttpServletRequest request) {
		Map<String, Object[]> paramsMap = null;
		if (!FileUploadBase.isMultipartContent(new ServletRequestContext(request))) {
			paramsMap = request.getParameterMap();
			if(paramsMap == null) {
				return emptyParamMap;
			} else {
				return paramsMap;
			}
		}
		Map<String,FileItem[]> params = getFileItems(request);
		if(params == null) {
			params = check(request);
		}
		paramsMap = getFiles(request);
		if(paramsMap == null) {
			return emptyParamMap;
		}
		return paramsMap;
	}
//	
//	private static final String getUUIDName(FileItem fileItem) {
//		String fileName = fileItem.getName();
//		System.err.println(">>>>>fileName=["+fileName+"]");
//		int lastIndex = fileName.lastIndexOf(".");
//		if(lastIndex == -1) {
//			return fileName;
//		} else {
//			String uuid = UUID.randomUUID().toString().replace("-", "");
//			return uuid + fileName.substring(fileName.lastIndexOf("."));
//		}
//	}
	
//	private static final String getUUID() {
//		return UUID.randomUUID().toString().replace("-", "");
//	}
	
	@SuppressWarnings("unchecked")
	private static final Map<String,FileItem[]> getFileItems(HttpServletRequest request) {
		return (Map<String,FileItem[]>) request.getAttribute(FILE_ITEM_UPLOAD_KEY);
	}
	
	

}
