package org.community.scheduler.enums;

/**
 * Job types
 * 
 * @author tudor.codrea
 *
 */
public enum Jobs {

	MINUTE_PROCESSOR_JOB("minuteProcessorJob"), HOURLY_PROCESSOR_JOB("hourlyProcessorJob"), DAILY_PROCESSOR_JOB("dailyProcessorJob"), WEEKLY_PROCESSOR_JOB("weeklyProcessorJob"),
	MONTHLY_PROCESSOR_JOB("monthlyProcessorJob");

	private String jobName;

	private Jobs(String jobName) {
		this.jobName = jobName;
	}

	public String getJobName() {
		return this.jobName;
	}

	public static Jobs fromJobName(String jobName) {
		for (Jobs value : Jobs.values()) {
			if (value.getJobName().equals(jobName)) {
				return value;
			}
		}
		return null;
	}

}
