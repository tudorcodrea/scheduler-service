package org.community.scheduler.service.api;

import java.util.Date;
import java.util.List;

import org.community.scheduler.entity.TextEntity;

/**
 * @author tudor.codrea
 *
 */
public interface ITextService {

	List<TextEntity> findAllBetweenTextDates(Date startDate, Date endDate);

	TextEntity insert(TextEntity te);

	List<TextEntity> insertList(List<TextEntity> tes);
	
}
