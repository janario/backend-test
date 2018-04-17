package me.janario.transaction.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionDto {
	private BigDecimal amount;
	private Instant timestamp;

	public TransactionDto() {
	}

	public TransactionDto(BigDecimal amount, Instant timestamp) {
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public boolean isOlderThan60Seconds() {
		return getTimestamp().isBefore(Instant.now().minusSeconds(60));
	}

	public TransactionResponseDto toResponse() {
		return new TransactionResponseDto(getAmount(), getTimestamp());
	}
}
