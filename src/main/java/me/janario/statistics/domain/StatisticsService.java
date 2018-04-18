package me.janario.statistics.domain;

import static me.janario.util.Utils.doWithLock;
import static me.janario.util.Utils.safeGet;

import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import me.janario.transaction.domain.TransactionResponseDto;

@Service
public class StatisticsService {
	@Autowired
	private TaskScheduler taskScheduler;


	private final Lock lock = new ReentrantLock();
	private CurrentStatistics currentStatistics = new CurrentStatistics();

	public void register(TransactionResponseDto dto) {
		if (dto.isOlderThan60Seconds()) {
			return;
		}

		doWithLock(lock, () -> currentStatistics = currentStatistics.addAmount(dto.getAmount()));
		taskScheduler.schedule(() -> this.unregister(dto), dto.expireOn());
	}

	private void unregister(TransactionResponseDto dto) {
		doWithLock(lock, () -> currentStatistics = currentStatistics.removeAmount(dto.getAmount()));
	}

	public StatisticsDto getStatistics() {
		CurrentStatistics currentStatistics = this.currentStatistics;
		BigDecimal min = safeGet(currentStatistics.sortedAmout::firstKey);
		BigDecimal max = safeGet(currentStatistics.sortedAmout::lastKey);
		BigDecimal sum = currentStatistics.sum;
		long count = currentStatistics.count;

		BigDecimal avg = BigDecimal.ZERO;
		if (count > 0) {
			avg = sum.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
		}
		return new StatisticsDto(sum, avg, max, min, count);
	}

	void cleanAll() {
		currentStatistics = new CurrentStatistics();
	}

	private static class CurrentStatistics {
		private final BigDecimal sum;
		private final long count;
		private final SortedMap<BigDecimal, Integer> sortedAmout;

		public CurrentStatistics() {
			this(BigDecimal.ZERO, 0, new TreeMap<>());
		}

		public CurrentStatistics(BigDecimal sum, long count, SortedMap<BigDecimal, Integer> sortedAmout) {
			this.sum = sum;
			this.count = count;
			this.sortedAmout = new TreeMap<>(sortedAmout);
		}

		public CurrentStatistics addAmount(BigDecimal amount) {
			CurrentStatistics statistics = new CurrentStatistics(
					sum.add(amount), (count + 1),
					sortedAmout);
			statistics.sortedAmout.merge(amount, 1, (o, n) -> o + n);
			return statistics;
		}

		public CurrentStatistics removeAmount(BigDecimal amount) {
			CurrentStatistics statistics = new CurrentStatistics(
					sum.subtract(amount), (count - 1),
					sortedAmout);
			statistics.sortedAmout.merge(amount, -1, (o, n) -> {
				int i = o + n;
				return (i == 0) ? null : i;
			});
			return statistics;
		}
	}
}
