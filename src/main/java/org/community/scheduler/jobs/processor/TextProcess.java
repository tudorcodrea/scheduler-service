package org.community.scheduler.jobs.processor;

import java.util.List;

import org.community.scheduler.entity.TextEntity;
import org.community.scheduler.jobs.reader.TextReader;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

/**
 * Demo Process
 * Taking the list from the {@link TextReader} and
 * log it's content
 * 
 * @author tudor.codrea
 *
 */
@Log4j2
@Component
public class TextProcess implements ItemProcessor<List<TextEntity>, List<TextEntity>> {

	private ExecutionContext executionContext;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.executionContext = stepExecution.getExecutionContext();
	}

	@Override
	public List<TextEntity> process(List<TextEntity> textEntities) throws Exception {
		
		List<TextEntity> processedItems = processTextValues(textEntities);
		
		String str = "Items processed:" + processedItems.size() + ";";
		if (log.isInfoEnabled()) {
			log.info(str);
		}
		
		if (executionContext.containsKey("message")) {
			String message = executionContext.getString("message");
			message += str;
			executionContext.put("message", message);
		} else {
			executionContext.put("message", str);
		}
		return processedItems;
	}

	private List<TextEntity> processTextValues(List<TextEntity> textEntities) {

		log.info("Processed TextEntity list: " + textEntities);
		
		return textEntities;
	}
}
