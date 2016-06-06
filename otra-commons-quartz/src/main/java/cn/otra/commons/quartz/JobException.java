package cn.otra.commons.quartz;


public class JobException extends RuntimeException {

	private static final long serialVersionUID = -33023460918567672L;
	public JobException() {
		super();
	}
	
	public JobException(String msg) {
		super(msg);
	}
	
	public JobException(Throwable throwable) {
		super(throwable);
	}
	
}
