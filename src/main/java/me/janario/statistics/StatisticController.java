package me.janario.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import me.janario.statistics.domain.StatisticsDto;
import me.janario.statistics.domain.StatisticsService;

@RestController
public class StatisticController {

	@Autowired
	private StatisticsService statisticsService;

	@GetMapping("/statistics")
	public StatisticsDto statistics() {
		return statisticsService.getStatistics();
	}
}
