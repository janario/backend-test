package me.janario.statistics;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import me.janario.transaction.domain.TransactionDto;
import me.janario.transaction.domain.TransactionService;

@RestController
public class StatisticController {

	@Autowired
	private TransactionService transactionService;

	@GetMapping("/statistics")
	public StatisticsDto statistics() {
		BigDecimal sum = BigDecimal.ZERO;
		long count = 0;

		BigDecimal max = null;
		BigDecimal min = null;

		for (TransactionDto dto : transactionService.list()) {
			if (dto.isOlderThan60Seconds()) {
				continue;
			}
			sum = sum.add(dto.getAmount());
			count++;

			if (max == null || dto.getAmount().compareTo(max) > 0) {
				max = dto.getAmount();
			}
			if (min == null || dto.getAmount().compareTo(min) < 0) {
				min = dto.getAmount();
			}
		}

		BigDecimal avg = BigDecimal.ZERO;
		if (count > 0) {
			avg = sum.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
		}

		return new StatisticsDto(sum, avg, max, min, count);
	}
}
