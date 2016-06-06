package cn.otra.commons.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTest {
	public static void main(String[] args) {

		try {
			// Grab the Scheduler instance from the Factory
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

			// and start it off
			scheduler.start();
			
			 // define the job and tie it to our HelloJob class
		    JobDetail job = newJob(BaseJob.class)
		        .withIdentity("myJob", "group1")
		        .build();

		    // Trigger the job to run now, and then repeat every 40 seconds
		    Trigger trigger = newTrigger()
		    		 .withIdentity("trigger1", "group1")
		    		 .withSchedule(cronSchedule("0/3 * * * * ?"))
		    		 .forJob("myJob", "group1")
		    		 .build();

		    // Tell quartz to schedule the job using our trigger
		    scheduler.scheduleJob(job, trigger);

		    try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			scheduler.shutdown();

		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}
}
