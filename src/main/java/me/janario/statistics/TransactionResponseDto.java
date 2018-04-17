package me.janario.statistics;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponseDto {
	private BigDecimal amount;
	private Instant time;

	public TransactionResponseDto() {
	}

	public TransactionResponseDto(BigDecimal amount, Instant time) {
		this.amount = amount;
		this.time = time;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Instant getTime() {
		return time;
	}
}
