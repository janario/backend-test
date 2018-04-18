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
	private final Map<Long, TransactionResponseDto> transactionsById = new ConcurrentHashMap<>();

	@Autowired
	private StatisticsService statisticsService;

	public TransactionResponseDto register(TransactionDto dto) {
		TransactionResponseDto response = dto.toResponse(ids.incrementAndGet());
		transactionsById.put(response.getId(), response);

		statisticsService.register(response);
		return response;
	}

	public TransactionResponseDto findById(Long id) {
		return transactionsById.get(id);
	}
}
