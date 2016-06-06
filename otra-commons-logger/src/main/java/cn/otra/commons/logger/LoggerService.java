package cn.otra.commons.logger;

public interface LoggerService {
	/**
	 * 极光推
	 */
	String ACTION_JPUSH = "jpush";
	/**
	 * 邮件相关
	 */
	String ACTION_MAIL = "email";
	
	/**
	 * 充值扣费相关
	 */
	String ACTION_MONEY = "money";
	
	/**
	 * 短信相关
	 */
	String ACTION_MMS = "mms";
	
	/**
	 * 车鉴定相关
	 */
	String ACTION_CJD = "cjd";
	

	/**
	 * 订单相关
	 */
	String ACTION_ORDER = "order";
	
	void log(String action,String content);
	
	void log(String action,String opId,String opName,String content,String mark,String extId);
	
}
