package cn.otra.commons.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.otra.commons.core.server.EcApp;

public class ServerUtils {
private static final Logger logger = Logger.getLogger(ServerUtils.class);
	private static final String charset = "UTF-8";
	
	public static final void waitingForShutdown(final EcApp app,final DefaultConfig defaultConfig) {
		if(defaultConfig == null) {
			logger.warn("no config found.");
			return;
		}
		logger.warn("waitingForShutdown [...]");
		new Thread(new Runnable() {
			public void run() {
				try {
					if(defaultConfig.getVal("server.stop.port") == null) {
						logger.error("no config[server.stop.port] found.");
						return;
					}
					int port = Integer.valueOf(defaultConfig.getVal("server.stop.port"));
					ServerUtils.writeCloseConf(defaultConfig);//生成停服配置
					ServerSocket socket = new ServerSocket(port);
					while(!app.isClose()) {
						try {
							Socket client = socket.accept();
							byte []buf = new byte[128];
							int res = client.getInputStream().read(buf);
							String result = new String(buf,0,res,charset);
							String [] msg = result.split("_");
							if(msg.length == 2) {
								//关服命令
								if(defaultConfig.getVal("server.stop.key") == null) {
									logger.error("no config[server.stop.key] found.");
									return;
								}
								String closeOrder = msg[0]+"_"+MD5Utils.md5(msg[0]+"_"+defaultConfig.getVal("server.stop.key"));
								if(closeOrder.equals(result)) {
									app.exit(defaultConfig.getAppId(),client);
									break;
								}
							}
							client.getOutputStream().write("-1".getBytes(charset));
							client.close();
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println("["+port+"]端口启动失败");
							System.exit(1);
						}
					}
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}).start();
		
		logger.warn("waitingForShutdown [OK]");
	}
	
	/**
	 * 请求关闭服务器
	 * @param appName
	 */
	public final static void applyToShutdownServer(String appName) {
		try {
			Properties properties = PropUtil.loadProperty("close.properties");
			Socket socket = new Socket("127.0.0.1",Integer.valueOf(properties.getProperty("server.stop.port")));
			String closeOrder = "shutdown_"+MD5Utils.md5("shutdown_"+properties.getProperty("server.stop.key"));
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(closeOrder.getBytes(charset));
			byte[]buf = new byte[1];
			int count = socket.getInputStream().read(buf);
			if(count == -1) {//收到服务器断开连接消息
				socket.close();
				return;
			}
			String res = new String(buf,0,count,charset);
			if(res.equals("1")) {
				logger.info(appName+"'s server close success!");
			} else if(res.equals("-1")) {
				logger.info(appName+"'s server close fail!");
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * 生成关服的配置文件
	 */
	private static final void writeCloseConf(DefaultConfig defaultConfig) {
		BufferedWriter writer = null;//new FileWriter(new File(System.getProperty("user.dir")+"/close.properties"));
		try {
			writer = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir")+"/close.properties")));
			writer.write("server.stop.port="+defaultConfig.getVal("server.stop.port"));
			writer.newLine();
			writer.write("server.stop.key="+defaultConfig.getVal("server.stop.key"));
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			logger.error("",e);
			System.exit(1);
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
			}
		}
	}
	
	public static final String getHostName() {
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
						.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						if("127.0.0.1".equals(ip.getHostAddress())) {
							continue;
						}
						return ip.getHostName();
					}
				}
			}
		} catch (SocketException e) {
			logger.error("",e);
			return null;
		}
		return null;
	}
	
	public static final List<String> getIps() {
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			List<String> ips = new ArrayList<String>();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						if("127.0.0.1".equals(ip.getHostAddress())) {
							continue;
						}
						ips.add(ip.getHostAddress());
					}
				}
			}
			return ips;
		} catch (SocketException e) {
			return null;
		}
	}

}
