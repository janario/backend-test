package me.janario.statistics;

import java.math.BigDecimal;

public class StatisticsDto {
	private BigDecimal sum;
	private BigDecimal avg;
	private BigDecimal max;
	private BigDecimal min;
	private long count;

	public StatisticsDto() {
	}

	public StatisticsDto(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, long count) {
		this.sum = sum;
		this.avg = avg;
		this.max = max;
		this.min = min;
		this.count = count;
	}

	public BigDecimal getSum() {
		return sum;
	}

	public BigDecimal getAvg() {
		return avg;
	}

	public BigDecimal getMax() {
		return max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public long getCount() {
		return count;
	}
}
