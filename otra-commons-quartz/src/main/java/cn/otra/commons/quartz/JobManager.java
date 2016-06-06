package cn.otra.commons.quartz;


import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public class JobManager {

	private Scheduler scheduler;

	private List<BaseJob> jobs;
	
	public JobManager(Scheduler scheduler,List<BaseJob> jobs) {
		this.scheduler = scheduler;
		this.jobs = jobs;
	}

	public void init() throws SchedulerException {
		System.err.println("=========JobManager.init=========");
		// and start it off
		scheduler.start();
		String group = "JobManagerGroup";
		for(BaseJob job:jobs) {
			String identity = job.getClass().getName();
			// define the job and tie it to our HelloJob class
			JobDetail jobDetail = newJob(job.getClass()).withIdentity(identity, group).build();
			jobDetail.getJobDataMap().put("_jobBean", job.getBean());
			jobDetail.getJobDataMap().put("_jobBeanMethod", job.getMethod());
			String triggerIdentity = job.getClass().getName()+"Trigger";
			// Trigger the job to run now, and then repeat with the expression
			Trigger trigger = newTrigger().withIdentity(triggerIdentity, group).withSchedule(cronSchedule(job.getExpression()))
					.forJob(identity, group).build();
			// Tell quartz to schedule the job using our trigger
			scheduler.scheduleJob(jobDetail, trigger);
		}
		
	}
	
	public void destroy() throws SchedulerException {
		System.err.println("=========JobManager.destroy=========");
		scheduler.shutdown();
	}
}
