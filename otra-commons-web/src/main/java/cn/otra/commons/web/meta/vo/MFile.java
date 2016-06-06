package cn.otra.commons.web.meta.vo;

import java.io.File;
import java.io.Serializable;

public class MFile implements Serializable {
	private static final long serialVersionUID = 6417798784327985970L;

	private String fileName;
	private File file;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "MFile [fileName=" + fileName + ", file=" + file + "]";
	}

}
