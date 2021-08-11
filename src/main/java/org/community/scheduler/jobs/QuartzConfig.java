package org.community.scheduler.jobs;

import java.io.IOException;

import org.community.scheduler.enums.Jobs;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

//	defaulting to 10 sec if property not found
	@Value("${job.minute-processor.cron-expression:0/10 * * ? * * *}")
	private String jobOnecronExpression;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private JobLocator jobLocator;

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		return jobRegistryBeanPostProcessor;
	}

	@Bean
	public JobDetail jobOneDetail() {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("jobName", Jobs.MINUTE_PROCESSOR_JOB.getJobName());
		jobDataMap.put("jobLauncher", jobLauncher);
		jobDataMap.put("jobLocator", jobLocator);

		return JobBuilder.newJob(CustomQuartzJob.class).withIdentity(Jobs.MINUTE_PROCESSOR_JOB.getJobName())
				.setJobData(jobDataMap).storeDurably().build();
	}

	@Bean
	public Trigger jobOneTrigger() {

		CronScheduleBuilder croneSchedulerBuilder = CronScheduleBuilder.cronSchedule(jobOnecronExpression)
				.withMisfireHandlingInstructionFireAndProceed();

		return TriggerBuilder.newTrigger().forJob(jobOneDetail()).withIdentity("jobOneTrigger")
				.withSchedule(croneSchedulerBuilder).build();
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		scheduler.setTriggers(jobOneTrigger());
		scheduler.setJobDetails(jobOneDetail());
		return scheduler;
	}

}