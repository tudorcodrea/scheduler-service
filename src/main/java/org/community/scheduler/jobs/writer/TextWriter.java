package org.community.scheduler.jobs.writer;

import java.util.List;

import org.community.scheduler.entity.ProcessedTextEntity;
import org.community.scheduler.entity.TextEntity;
import org.community.scheduler.repository.api.IProcessedTextRepository;
import org.community.scheduler.service.api.IProcessedTextService;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

/**
 * Demo Writer
 * Taking the result and persisting it to {@link IProcessedTextRepository}
 * 
 * @author tudor.codrea
 *
 */
@Log4j2
@Component
public class TextWriter implements ItemWriter<List<TextEntity>> {

	private ExecutionContext executionContext;

	@Autowired
	private IProcessedTextService processedTextService;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.executionContext = stepExecution.getExecutionContext();
	}

	@Override
	public void write(List<? extends List<TextEntity>> items) throws Exception {

		for (List<TextEntity> textEntitieslist : items) {
			
			textEntitieslist.forEach(i -> {
				processedTextService.insert(fromTextEntity(i));
			});
			
			if (log.isInfoEnabled()) {
				log.info("ProcessedTextEntities written:" + items.size());
			}
		}
		
		String str = "Items wrote:" + items.size() + ";";
		if (executionContext.containsKey("message")) {
			String message = executionContext.getString("message");
			message += str;
			executionContext.put("message", message);
		} else {
			executionContext.put("message", str);
		}
	}

	private ProcessedTextEntity fromTextEntity(TextEntity te) {
		return ProcessedTextEntity.builder().textDate(te.getTextDate()).textValue(te.getTextValue()).build();
	}

}
