package cn.otra.commons.core.server;

import java.net.Socket;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class EcApp {
	private static final Logger logger = Logger.getLogger(EcApp.class);
	private boolean isClose;

	public boolean isClose() {
		return isClose;
	}
	
	public void setClose(boolean isClose) {
		this.isClose = isClose;
	}
	
	public void exit(String appId,Socket socket) {
		try {
			setClose(true);
			logger.warn("closing server ["+appId+"]......");
			
			releaseBeforeExit();
			
			Thread.sleep(3000);
			logger.warn("server ["+appId+"] closed!");
			socket.getOutputStream().write("1".getBytes("UTF-8"));
			socket.close();
			LogManager.shutdown();//关闭log4j
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
	/**
	 * 正常关闭资源
	 */
	public abstract void releaseBeforeExit() ;
	
}
