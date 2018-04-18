package me.janario.transaction.domain;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TransactionResponseDto {
	@JsonIgnore
	private long id;
	private BigDecimal amount;
	private Instant time;

	public TransactionResponseDto() {
	}

	public TransactionResponseDto(long id, BigDecimal amount, Instant time) {
		this.id = id;
		this.amount = amount;
		this.time = time;
	}

	public long getId() {
		return id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Instant getTime() {
		return time;
	}

	public Instant expireOn() {
		return time.plusSeconds(60);
	}

	public boolean isOlderThan60Seconds() {
		return getTime().isBefore(Instant.now().minusSeconds(60));
	}
}
