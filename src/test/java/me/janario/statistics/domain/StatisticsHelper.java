package me.janario.statistics.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatisticsHelper {
	@Autowired
	private StatisticsService statisticsService;

	public void cleanAll() {
		//keep it package level to use only on test
		statisticsService.cleanAll();
	}
}