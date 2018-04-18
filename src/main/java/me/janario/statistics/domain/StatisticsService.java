package me.janario.statistics.domain;

import static me.janario.util.Utils.doWithLock;
import static me.janario.util.Utils.safeGet;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
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

	private BigDecimal sum = BigDecimal.ZERO;
	private final SortedSet<TransactionResponseDto> transactions = new TreeSet<>(
			Comparator.comparing(TransactionResponseDto::getAmount)
					.thenComparing(TransactionResponseDto::getId));

	public void register(TransactionResponseDto dto) {
		if (dto.isOlderThan60Seconds()) {
			return;
		}

		doWithLock(lock, () -> {
			sum = sum.add(dto.getAmount());
			transactions.add(dto);
		});
		taskScheduler.schedule(() -> this.unregister(dto), dto.expireOn());
	}

	private void unregister(TransactionResponseDto dto) {
		doWithLock(lock, () -> {
			sum = sum.subtract(dto.getAmount());
			transactions.remove(dto);
		});
	}

	public StatisticsDto getStatistics() {

		BigDecimal min = safeGet(transactions::first);
		BigDecimal max = safeGet(transactions::last);
		BigDecimal sum = this.sum;
		long count = transactions.size();

		BigDecimal avg = BigDecimal.ZERO;

		if (count > 0) {
			avg = sum.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
		}
		return new StatisticsDto(sum, avg, max, min, count);
	}

	void cleanAll() {
		sum = BigDecimal.ZERO;
		transactions.clear();
	}
}
