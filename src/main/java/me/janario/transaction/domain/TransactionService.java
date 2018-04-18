package me.janario.transaction.domain;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.janario.statistics.domain.StatisticsService;

@Service
public class TransactionService {
	private final AtomicLong ids = new AtomicLong();

	@Autowired
	private StatisticsService statisticsService;

	public TransactionResponseDto register(TransactionDto dto) {
		TransactionResponseDto response = dto.toResponse(ids.incrementAndGet());
		statisticsService.register(response);
		return response;
	}

	public TransactionResponseDto findById(Long id) {
		return statisticsService.findById(id);
	}
}
