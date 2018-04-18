package me.janario.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class SchedulingConfiguration implements SchedulingConfigurer {

	private ThreadPoolTaskScheduler taskScheduler;

	@PostConstruct
	private void init() {
		taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(50);
		taskScheduler.initialize();
	}

	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {
		return taskScheduler;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setTaskScheduler(taskScheduler);
	}
}
