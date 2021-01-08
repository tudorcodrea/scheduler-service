package org.community.scheduler.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.community.scheduler.entity.SchedulerJobHistory;
import org.community.scheduler.service.api.IJobHistoryService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class JobCompletionListener extends JobExecutionListenerSupport {

	@Autowired
	private IJobHistoryService jobHistoryService;

	@Override
	public void afterJob(JobExecution jobExecution) {
		
		String successDetail = "";
		SchedulerJobHistory jobHistory = null;
		
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			
			StringBuilder sb = new StringBuilder();
			
			for (StepExecution step : jobExecution.getStepExecutions()) {
				sb.append(step.getExecutionContext().containsKey("message") ? step.getExecutionContext().get("message")
						: step.getExitStatus().getExitDescription());
				if (step.getExecutionContext().containsKey("jobHistory")) {
					jobHistory = (SchedulerJobHistory) step.getExecutionContext().get("jobHistory");
				}
			}
			
			jobExecution.setExitStatus(new ExitStatus(jobExecution.getExitStatus().getExitCode(), sb.toString()));
			successDetail = sb.toString();
			
			if (log.isInfoEnabled()) {
				log.info("Job terminated with status:" + jobExecution.getExitStatus().getExitCode());
			}
		}
		if (jobHistory != null) {
			jobHistory.setEndTime(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS").format(new Date()));
			jobHistory.setExitStatus(jobExecution.getStatus().name());
			jobHistory.setLastRun(new Date().getTime());
			
			if (jobExecution.getStatus() == BatchStatus.FAILED) {
				StringBuilder sb = new StringBuilder();
				if (jobExecution.getAllFailureExceptions().size() > 0) {
					for (Throwable t : jobExecution.getAllFailureExceptions()) {
						sb.append(t.getMessage());
					}
				}
				jobHistory.setDetails(sb.toString());
				jobHistory.setStatus("Failed");
			} else {
				jobHistory.setDetails(successDetail.length() > 2049 ? successDetail.substring(0, 2049) : successDetail);
				jobHistory.setStatus("Completed");
			}
			
			try {
				jobHistoryService.update(jobHistory);
			} catch (Exception e) {
				log.error("Failed to update job history:" + e);
			}
		}
	}

}
