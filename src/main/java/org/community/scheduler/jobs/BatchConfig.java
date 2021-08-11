package org.community.scheduler.jobs;

import java.util.List;

import org.community.scheduler.entity.TextEntity;
import org.community.scheduler.enums.Jobs;
import org.community.scheduler.jobs.processor.TextProcess;
import org.community.scheduler.jobs.reader.TextReader;
import org.community.scheduler.jobs.writer.TextWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private TextReader textReader;

	@Autowired
	private TextProcess textProcessor;

	@Autowired
	private TextWriter textWriter;

	@Autowired
	private JobCompletionListener completionListener;

	@Bean("minuteProcessorJob")
	public Job textJob() {
		return jobBuilderFactory.get(Jobs.MINUTE_PROCESSOR_JOB.getJobName()).incrementer(new RunIdIncrementer())
				.listener(completionListener).flow(textStep()).end().build();
	}

	@Bean
	public Step textStep() {
		return stepBuilderFactory.get("textStep").<List<TextEntity>, List<TextEntity>>chunk(1).reader(textReader)
				.processor(textProcessor).writer(textWriter).build();
	}
	
}
