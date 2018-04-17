package me.janario.transaction.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.janario.statistics.domain.StatisticsService;

@Service
public class TransactionService {
	private final AtomicLong ids = new AtomicLong();
	private final Map<Long, TransactionResponseDto> transactions = new ConcurrentHashMap<>();

	@Autowired
	private StatisticsService statisticsService;

	public TransactionResponseDto register(TransactionDto dto) {
		TransactionResponseDto response = dto.toResponse(ids.incrementAndGet());
		statisticsService.register(response);
		transactions.put(response.getId(), response);
		return response;
	}

	public TransactionResponseDto findById(Long id) {
		return transactions.get(id);
	}

	void cleanAll() {
		transactions.clear();
		ids.set(0);
	}
}
