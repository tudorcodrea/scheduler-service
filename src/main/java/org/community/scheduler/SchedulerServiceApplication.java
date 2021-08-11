package org.community.scheduler;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author tudor.codrea
 *
 */
@SpringBootApplication
@ComponentScan({ "org.community.scheduler", "org.community.scheduler.repository", "de.chandre.quartz.spring" })
@EnableSwagger2
@EnableWebMvc
@EnableAutoConfiguration
@EnableScheduling
@EnableCaching
@EnableBatchProcessing
public class SchedulerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerServiceApplication.class, args);
	}

}
