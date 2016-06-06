package cn.otra.commons.quartz;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BaseJob implements Job {
	
	private String expression;
	private Object bean;
	private String method;
	private static final Logger LOG = Logger.getLogger(BaseJob.class);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
//		jobDetail.getJobDataMap().put("_jobBean", job.getBean());
//		jobDetail.getJobDataMap().put("_jobBeanMethod", job.getMethod());
		bean = context.getJobDetail().getJobDataMap().get("_jobBean");
		method = (String)context.getJobDetail().getJobDataMap().get("_jobBeanMethod");
		try {
			Method mt = bean.getClass().getMethod(method);
			mt.invoke(bean);
			if(LOG.isDebugEnabled()) {
				LOG.debug("BaseJob.execute============"+bean.getClass().getSimpleName()+"."+method+"();");
			}
		} catch (Exception e) {
			LOG.error("",e);
		}
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getExpression() {
		return expression;
	}
	
	public Object getBean() {
		return bean;
	}
	
	public String getMethod() {
		return method;
	}
	
}
