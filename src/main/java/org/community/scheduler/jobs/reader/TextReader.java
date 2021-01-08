package org.community.scheduler.jobs.reader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.community.scheduler.entity.SchedulerJobHistory;
import org.community.scheduler.entity.TextEntity;
import org.community.scheduler.enums.Jobs;
import org.community.scheduler.jobs.processor.TextProcess;
import org.community.scheduler.repository.api.ITextRepository;
import org.community.scheduler.service.api.IJobHistoryService;
import org.community.scheduler.service.api.ITextService;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

/**
 * Demo Reader
 * Reading from {@link ITextRepository} and passes the result to {@link TextProcess}
 * 
 * @author tudor.codrea
 *
 */
@Log4j2
@Component
public class TextReader implements ItemReader<List<TextEntity>> {

	@Value("${job.minute-processor.enable:true}")
	private Boolean isJobEnabled;

	@Autowired
	private IJobHistoryService jobHistoryService;

	@Autowired
	private ITextService textService;

	private Boolean executed = false;

	private ExecutionContext executionContext;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.executionContext = stepExecution.getExecutionContext();
	}

	@Override
	public List<TextEntity> read() throws Exception {
		if (!isJobEnabled) {
			if (log.isInfoEnabled()) {
				log.info("Text processing job not enabled");
			}
			return new ArrayList<TextEntity>();
		}
		Optional<SchedulerJobHistory> jobHistory = jobHistoryService.getLastRunByName(Jobs.MINUTE_PROCESSOR_JOB.getJobName());
		Date start = getTo();
		if (jobHistory.isPresent() && jobHistory.get().getLastRun() != null
				&& jobHistory.get().getLastRun() > start.getTime()) {
			if (log.isInfoEnabled()) {
				log.info("Minute processing job already executed. Is allowed only once per minute");
			}
			return new ArrayList<TextEntity>();
		}

		if (!executed) {
			if (log.isInfoEnabled()) {
				log.info("Minute processing job reading");
			}
			
			createJobHistory();
			
			List<TextEntity> textEntities = textService.findAllBetweenTextDates(getFrom(), getTo());
			
			String readerMessage = "Number of items read:" + textEntities.size() + ";";
			if (log.isDebugEnabled()) {
				log.debug(readerMessage);
			}
			
			if (executionContext.containsKey("message")) {
				String message = executionContext.getString("message");
				message += readerMessage;
				executionContext.put("message", message);
			} else {
				executionContext.put("message", readerMessage);
			}
			executed = true;
			return textEntities;
		} else {
			executed = false;
		}
		
		if (log.isInfoEnabled()) {
			log.info("Minute reading ended");
		}
		return new ArrayList<TextEntity>();
	}

	private void createJobHistory() {
		SchedulerJobHistory jh = new SchedulerJobHistory();
		jh.setJobName(Jobs.MINUTE_PROCESSOR_JOB.getJobName());
		jh.setStartTime(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS").format(new Date()));
		jh.setStatus("Running");
		try {
			SchedulerJobHistory newJobHistory = jobHistoryService.insert(jh);
			executionContext.put("jobHistory", newJobHistory);
		} catch (Exception e) {
			log.error("Job history insert problem", e);
		}
	}

	private Date getFrom() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.MINUTE, -1);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if (log.isDebugEnabled()) {
			log.debug("From date:" + cal.toString());
		}
		return cal.getTime();
	}

	private Date getTo() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if (log.isDebugEnabled()) {
			log.debug("To date:" + cal.toString());
		}
		return cal.getTime();
	}
}
