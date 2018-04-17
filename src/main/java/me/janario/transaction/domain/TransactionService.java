package me.janario.transaction.domain;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {
	private final AtomicInteger ids = new AtomicInteger();
	private final Map<Integer, TransactionDto> transactions = new ConcurrentHashMap<>();

	public int register(TransactionDto dto) {
		int id;
		transactions.put(id = ids.incrementAndGet(), dto);
		return id;
	}

	public TransactionDto findById(Integer id) {
		return transactions.get(id);
	}

	public Collection<TransactionDto> list() {
		return transactions.values();
	}

	void cleanAll() {
		transactions.clear();
		ids.set(0);
	}
}
