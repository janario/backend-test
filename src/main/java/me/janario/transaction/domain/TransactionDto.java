package me.janario.transaction.domain;

import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.NotNull;

public class TransactionDto {
	@NotNull
	private BigDecimal amount;
	@NotNull
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

	public TransactionResponseDto toResponse(long id) {
		return new TransactionResponseDto(id, getAmount(), getTimestamp());
	}
}
