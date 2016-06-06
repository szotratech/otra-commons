package cn.otra.commons;

import java.io.File;


public class Delete {

	public static void main(String[] args) {
		File tmp = new File("/tmp");
		File[] files = tmp.listFiles();
		for(File file:files) {
			if(file.lastModified() < System.currentTimeMillis() - 30000) {
				file.delete();
			}
		}
	}
}
