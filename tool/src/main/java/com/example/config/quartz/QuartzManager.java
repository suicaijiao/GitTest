package com.example.config.quartz;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;


/**
 * @Description: 定时任务管理类
 * 
 * @ClassName: QuartzManager
 * @Copyright: Copyright (c) 2014
 * 
 * @author Comsys-LZP
 * @date 2014-6-26 下午03:15:52
 * @version V2.0
 */
public class QuartzManager {
	private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
	private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";
	private static String TRIGGER_GROUP_NAME = "EXTJWEB_TRIGGERGROUP_NAME";
	
	/**
	 * 
	 *@Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名 
	 * @param jobName
	 * @param cls
	 * @param time      
	 * @return: void      
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public static void addJob(String jobName, Class cls, String time) {
		try {
			
			initSchedulerFactory(gSchedulerFactory);
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = new JobDetail(jobName,JOB_GROUP_NAME,cls);// 任务名，任务组，任务执行类
			// 触发器
			CronTrigger trigger = new CronTrigger(jobName,TRIGGER_GROUP_NAME);// 触发器名,触发器组
			trigger.setCronExpression(time);// 触发器时间设定
			sched.scheduleJob(jobDetail, trigger);
			// 启动
			if (!sched.isShutdown()) {
				sched.start();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 *@Description: 添加一个定时任务 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param jobClass
	 * @param time      
	 * @return: void      
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
			Class jobClass, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = new JobDetail(jobName, jobGroupName, jobClass);// 任务名，任务组，任务执行类
			// 触发器
			CronTrigger trigger = new CronTrigger(triggerName, triggerGroupName);// 触发器名,触发器组
			trigger.setCronExpression(time);// 触发器时间设定
			sched.scheduleJob(jobDetail, trigger);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
	 *@Description:  
	 * @param jobName
	 * @param time      
	 * @return: void      
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public static void modifyJobTime(String jobName, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			CronTrigger trigger = (CronTrigger) sched.getTrigger(jobName, TRIGGER_GROUP_NAME);
			if (trigger == null) {
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				JobDetail jobDetail = sched.getJobDetail(jobName, JOB_GROUP_NAME);
				Class objJobClass = jobDetail.getJobClass();
				removeJob(jobName);
				addJob(jobName, objJobClass, time);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 *@Description:  修改一个任务的触发时间
	 * @param triggerName
	 * @param triggerGroupName
	 * @param time      
	 * @return: void      
	 * @throws
	 */
	public static void modifyJobTime(String triggerName, String triggerGroupName, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerName, triggerGroupName);
			if (trigger == null) {
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				CronTrigger ct = (CronTrigger) trigger;
				// 修改时间
				ct.setCronExpression(time);
				// 重启触发器
				sched.resumeTrigger(triggerName, triggerGroupName);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 *@Description: 移除一个任务(使用默认的任务组名，触发器名，触发器组名) 
	 * @param jobName      
	 * @return: void      
	 * @throws
	 */
	public static void removeJob(String jobName) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.pauseTrigger(jobName, TRIGGER_GROUP_NAME);// 停止触发器
			sched.unscheduleJob(jobName, TRIGGER_GROUP_NAME);// 移除触发器
			sched.deleteJob(jobName, JOB_GROUP_NAME);// 删除任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 *@Description: 移除一个任务 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName      
	 * @return: void      
	 * @throws
	 */
	public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.pauseTrigger(triggerName, triggerGroupName);// 停止触发器
			sched.unscheduleJob(triggerName, triggerGroupName);// 移除触发器
			sched.deleteJob(jobName, jobGroupName);// 删除任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 *@Description:启动所有定时任务        
	 * @return: void      
	 * @throws
	 */
	public static void startJobs() {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 *@Description: 关闭所有定时任务       
	 * @return: void      
	 * @throws
	 */
	public static void shutdownJobs() {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			if (!sched.isShutdown()) {
				sched.shutdown();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * 查询job信息
	 * 
	 * @param job
	 *            QuartzJob
	 */
//	public static void queryJob(QuartzJob job) {
//		try {
//			Scheduler scheduler = gSchedulerFactory.getScheduler();
//			// JobDetail jobDetail = scheduler.getJobDetail(job.getJobName(),
//			// JOB_GROUP_NAME);
//			Trigger[] triggers = scheduler.getTriggersOfJob(job.getJobName(), JOB_GROUP_NAME);
//			if (triggers != null && triggers.length > 0) {
//				Date nextFireTime = triggers[0].getNextFireTime();
//				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				job.setNextFireTime(df.format(nextFireTime));
//			}
//			int state = scheduler.getTriggerState(job.getJobName(), TRIGGER_GROUP_NAME);
//			job.setState(state);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 
	 * @param jobGroupName
	 */
	public static void queryJobs() {
		try {
			Scheduler scheduler = gSchedulerFactory.getScheduler();

			for (String groupName : scheduler.getJobGroupNames()) {

				// loop all jobs by groupname
				for (String jobName : scheduler.getJobNames(groupName)) {

					// get job's trigger
					Trigger[] triggers = scheduler.getTriggersOfJob(jobName, groupName);
					Date nextFireTime = triggers[0].getNextFireTime();

					System.out.println("[jobName] : " + jobName + " [groupName] : " + groupName + " - " + nextFireTime);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void initSchedulerFactory(SchedulerFactory gSchedulerFactory)throws SchedulerException, IOException{
		Properties props = new Properties();
		props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		props.put("org.quartz.threadPool.threadCount", "10");
        ((StdSchedulerFactory) gSchedulerFactory).initialize(props);
	}
}